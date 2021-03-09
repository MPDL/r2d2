package de.mpg.mpdl.r2d2.service.impl;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.r2d2.db.DatasetVersionRepository;
import de.mpg.mpdl.r2d2.db.FileRepository;
import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.InvalidStateException;
import de.mpg.mpdl.r2d2.exceptions.NotFoundException;
import de.mpg.mpdl.r2d2.exceptions.OptimisticLockingException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.exceptions.ValidationException;
import de.mpg.mpdl.r2d2.model.FileChunk;
import de.mpg.mpdl.r2d2.model.FileChunk.Progress;
import de.mpg.mpdl.r2d2.model.VersionId;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.File.UploadState;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.search.dao.FileDaoEs;
import de.mpg.mpdl.r2d2.search.dao.GenericDaoEs;
import de.mpg.mpdl.r2d2.search.service.impl.IndexingService;
import de.mpg.mpdl.r2d2.service.FileService;
import de.mpg.mpdl.r2d2.service.storage.SwiftObjectStoreRepository;
import de.mpg.mpdl.r2d2.service.util.FileDownloadWrapper;

@Service
public class FileUploadService extends GenericServiceDbImpl<File> implements FileService {

  public FileUploadService() {
    super(File.class);
  }

  @Autowired
  FileRepository fileRepository;

  @Autowired
  DatasetVersionRepository datasetVersionRepository;

  @Autowired
  SwiftObjectStoreRepository objectStoreRepository;

  @Autowired
  private IndexingService indexingService;

  private File create(File object, R2D2Principal user) throws R2d2TechnicalException, ValidationException, AuthorizationException {
    try {
      setBasicCreationProperties(object, user.getUserAccount());
      fileRepository.save(object);
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }
    return object;
  }


  @Override
  //should return void. Throw exception if deletion of object storage fails
  public void delete(UUID id, R2D2Principal user)
      throws R2d2TechnicalException, OptimisticLockingException, NotFoundException, InvalidStateException, AuthorizationException {
    File file =
        fileRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("File with id %s NOT FOUND!", id.toString())));
    checkAa("get", user, file);
    //check if list of dataset versions is empty
    if (file.getState().equals(UploadState.PUBLIC) || file.getState().equals(UploadState.ATTACHED)) {
      throw new InvalidStateException(String.format("File with id %s is part of a dataset!", id.toString()));
    } else {
      try {
        fileRepository.deleteById(id);
        objectStoreRepository.deleteContainer(id.toString());
        indexingService.deleteFile(id, true);
      } catch (Exception e) {
        throw new R2d2TechnicalException(e);
      }
    }
  }

  @Override
  public File get(UUID id, R2D2Principal user) throws R2d2TechnicalException, NotFoundException, AuthorizationException {
    File file =
        fileRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("File with id %s NOT FOUND!", id.toString())));
    checkAa("get", user, file);
    return file;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public File uploadSingleFile(File file2upload, InputStream fileStream, R2D2Principal user) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException {

    checkAa("upload", user);

    File file = create(file2upload, user);
    objectStoreRepository.createContainer(file2upload.getId().toString());
    String eTag = objectStoreRepository.uploadFile(file2upload, fileStream);
    //TODO compare client etag with server etag 
    file.setChecksum(eTag);
    file.setState(UploadState.COMPLETE);
    file.setStorageLocation(objectStoreRepository.getPublicURI(file2upload.getId().toString()));
    file.setSize(objectStoreRepository.getFileSize(file2upload.getId().toString()));
    indexingService.reindexFile(file, true);
    return file;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public File initNewFile(File file2upload, R2D2Principal user) throws R2d2TechnicalException, OptimisticLockingException,
      ValidationException, NotFoundException, InvalidStateException, AuthorizationException {

    checkAa("upload", user);

    File file = create(file2upload, user);
    objectStoreRepository.createContainer(file2upload.getId().toString());
    indexingService.reindexFile(file, true);

    return file;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public FileChunk uploadFileChunk(UUID fileId, FileChunk chunk, InputStream fileStream, R2D2Principal user) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException {

    checkAa("upload", user);

    File file = fileRepository.findById(fileId)
        .orElseThrow(() -> new NotFoundException(String.format("File with id %s MOT FOUND!", fileId.toString())));

    List<FileChunk> chunks = file.getStateInfo().getChunks();
    if (file.getState().equals(UploadState.COMPLETE)) {
      throw new InvalidStateException(String.format("File with id %s is in state %s", fileId.toString(), file.getState().name()));
    }

    if (chunks.stream().anyMatch(c -> c.getNumber() == chunk.getNumber())) {
      FileChunk fc = chunks.stream().filter(c -> c.getNumber() == chunk.getNumber()).findFirst().get();
      if (fc.getProgress().equals(Progress.IN_PROGRESS)) {
        throw new InvalidStateException(String.format("Chunk with number %d is in state %s", fc.getNumber(), fc.getProgress().name()));
      } else {
        chunks.removeIf(c -> c.getNumber() == chunk.getNumber());
      }
    }

    String etag = objectStoreRepository.uploadChunk(file, chunk, fileStream);
    chunk.setServerEtag(etag);
    chunk.setProgress(Progress.COMPLETE);
    chunks.add(chunk);
    file.setState(UploadState.ONGOING);

    indexingService.reindexFile(file, true);
    return chunk;
  }


  @Override
  @Transactional(rollbackFor = Throwable.class)
  public File completeChunkedUpload(UUID fileId, int parts, R2D2Principal user) throws R2d2TechnicalException, OptimisticLockingException,
      ValidationException, NotFoundException, InvalidStateException, AuthorizationException {
    checkAa("upload", user);
    File file = fileRepository.findById(fileId)
        .orElseThrow(() -> new NotFoundException(String.format("File with id %s NOT FOUND!", fileId.toString())));
    // TODO: check number of parts in object store ...
    if (file.getStateInfo().getChunks().size() == parts) {
      String etag = objectStoreRepository.createManifest(file);
      //TODO compare client etag with server etag 
      file.setChecksum(etag);
      file.getStateInfo().setExpectedNumberOfChunks(parts);
      file.setState(UploadState.COMPLETE);
      file.setStorageLocation(objectStoreRepository.getPublicURI(file.getId().toString()));
      file.setSize(objectStoreRepository.getFileSize(file.getId().toString()));
      indexingService.reindexFile(file, true);

      return file;
    } else {
      throw new InvalidStateException(String.format("Incorrect number of parts (expected %d, but got %d) in file with id %s", parts,
          file.getStateInfo().getChunks().size(), fileId.toString()));
    }

  }

  @Override
  //Files should be indexed in elasticsearch
  //query "q" should pe possible
  //reindex files async, but datasets sync ? 
  public Page<File> list(Pageable pageable, R2D2Principal user) throws R2d2TechnicalException, NotFoundException, AuthorizationException {
    return fileRepository.findAll(pageable);
  }

  @Override
  public FileDownloadWrapper getFileContent(UUID id, R2D2Principal user) throws R2d2TechnicalException, OptimisticLockingException,
      ValidationException, NotFoundException, InvalidStateException, AuthorizationException {
    File file =
        fileRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("File with id %s NOT FOUND!", id.toString())));
    checkAa("get", user, file);
    FileDownloadWrapper wrapper = new FileDownloadWrapper(file, objectStoreRepository);
    return wrapper;
  }

}
