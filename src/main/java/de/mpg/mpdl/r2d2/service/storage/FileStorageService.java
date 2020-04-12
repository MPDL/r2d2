package de.mpg.mpdl.r2d2.service.storage;

import java.io.InputStream;
import java.util.UUID;

import org.jclouds.blobstore.BlobStoreContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.InvalidStateException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.exceptions.ValidationException;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.File.UploadState;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.service.FileService;

@Service
public class FileStorageService {

  private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

  private static final String SEGMENTS = "segments";

  private BlobStoreContext blobStoreContext;

  private SwiftObjectStoreRepository repository;
  private FileService service;

  @Autowired
  public FileStorageService(BlobStoreContext context, SwiftObjectStoreRepository repo, FileService svc) {
    this.repository = repo;
    this.blobStoreContext = context;
    this.repository.setContext(blobStoreContext);
    this.service = svc;
  }

  public String initializeUpload(String container, String fileName, int totalNumberOfChunks, R2D2Principal user)
      throws ValidationException, AuthorizationException, R2d2TechnicalException {

    File file = new File();
    file.setFilename(fileName);
    file.setTotalParts(totalNumberOfChunks);
    try {
      logger.info("creating" + container);
      logger.info("repo? " + repository);
      repository.createContainer(container);
      file = service.create(file, user);
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }
    return file.getId().toString();
  }

  public File upload(String fileId, int fileChunkNumber, byte[] bytes, String name, String contentType, R2D2Principal user)
      throws R2d2TechnicalException {

    String path = SEGMENTS + "/" + String.format("%06d", fileChunkNumber);
    File file = null;
    try {
      file = service.get(UUID.fromString(fileId), user);
      repository.createContainer(fileId);
      String eTag = repository.uploadFile(fileId, bytes, path, name, contentType);
      if (!eTag.isBlank()) {
        file = updateFile(file, user, fileChunkNumber);
      }
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }
    return file;
  }

  public File uploadManifest(String container, String fileId, R2D2Principal user) throws R2d2TechnicalException {
    File file = null;
    try {
      file = service.get(UUID.fromString(fileId), user);
      if (file.getCompletedParts() != file.getTotalParts()) {
        throw new InvalidStateException("total number of chunks doesn't match completed number of chunks");
      } else {
        String eTag = repository.creatreManifest(fileId, SEGMENTS, container);
        if (!eTag.isBlank()) {
          file = updateFile(file, user, Integer.MAX_VALUE);
        }
      }
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }
    return file;
  }

  public InputStream download(String container, String File, R2D2Principal user) {
    return repository.downloadFile(container, File);
  }

  private File updateFile(File file, R2D2Principal user, int chunk) throws R2d2TechnicalException {
    if (chunk == Integer.MAX_VALUE) {
      file.setState(UploadState.COMPLETE);
    } else {
      file.setState(UploadState.ONGOING);
      file.setCompletedParts(chunk);
    }

    try {
      file = service.update(file, user);
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }
    return file;
  }
}
