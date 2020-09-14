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
import de.mpg.mpdl.r2d2.service.FileService;
import de.mpg.mpdl.r2d2.service.storage.SwiftObjectStoreRepository;

@Service
public class FileUploadService extends GenericServiceDbImpl<File> implements FileService {

  public FileUploadService() {
    super(File.class);
    // TODO Auto-generated constructor stub
  }

  @Autowired
  FileRepository fileRepository;
  
  @Autowired
  DatasetVersionRepository datasetVersionRepository;

  @Autowired
  SwiftObjectStoreRepository objectStoreRepository;

  @Autowired
  FileDaoEs fileDaoEs;

  @Override
  public File create(File object, R2D2Principal user) throws R2d2TechnicalException, ValidationException, AuthorizationException {
    // setBasicCreationProperties(object, user.getUserAccount());
    try {
      fileRepository.save(object);
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }
    return object;
  }

  @Override
  public File update(File object, R2D2Principal user) throws R2d2TechnicalException, OptimisticLockingException, ValidationException,
      NotFoundException, InvalidStateException, AuthorizationException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
  public boolean delete(UUID id, R2D2Principal user)
      throws R2d2TechnicalException, OptimisticLockingException, NotFoundException, InvalidStateException, AuthorizationException {
    try {
      fileRepository.deleteById(id);
      return objectStoreRepository.deleteContainer(id.toString());
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }
    // stagingFileDaoEs.deleteImmediatly(id.toString());
  }

  @Override
  public File get(UUID id, R2D2Principal user) throws R2d2TechnicalException, NotFoundException, AuthorizationException {
    File sf =
        fileRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("File with id %s NOT FOUND!", id.toString())));
    return sf;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public File uploadSingleFile(File file, InputStream fileStream, R2D2Principal user) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException {

    checkAa("upload", user);

    File sf = create(file, user);
    objectStoreRepository.createContainer(file.getId().toString());
    String eTag = objectStoreRepository.uploadFile(file, fileStream);
    sf.setChecksum(eTag);
    sf.setState(UploadState.COMPLETE);
    sf.setStorageLocation(objectStoreRepository.getPublicURI(file.getId().toString()));
    // stagingFileDaoEs.createImmediately(sf.getId().toString(), sf);

    return sf;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public File initNewFile(File file, R2D2Principal user) throws R2d2TechnicalException, OptimisticLockingException, ValidationException,
      NotFoundException, InvalidStateException, AuthorizationException {

    checkAa("upload", user);

    File sf = create(file, user);
    objectStoreRepository.createContainer(file.getId().toString());
    // stagingFileDaoEs.createImmediately(sf.getId().toString(), sf);

    return sf;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public FileChunk uploadFileChunk(UUID fileId, FileChunk chunk, InputStream fileStream, R2D2Principal user) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException {

    checkAa("upload", user);

    File sf = fileRepository.findById(fileId)
        .orElseThrow(() -> new NotFoundException(String.format("File with id %s MOT FOUND!", fileId.toString())));

    List<FileChunk> chunks = sf.getStateInfo().getChunks();
    if (sf.getState().equals(UploadState.COMPLETE)) {
      throw new InvalidStateException(String.format("File with id %s is in state %s", fileId.toString(), sf.getState().name()));
    }

    if (chunks.stream().anyMatch(c -> c.getNumber() == chunk.getNumber())) {
      FileChunk fc = chunks.stream().filter(c -> c.getNumber() == chunk.getNumber()).findFirst().get();
      if (fc.getProgress().equals(Progress.IN_PROGRESS)) {
        throw new InvalidStateException(String.format("Chunk with number %d is in state %s", fc.getNumber(), fc.getProgress().name()));
      } else {
        chunks.removeIf(c -> c.getNumber() == chunk.getNumber());
      }
    }

    String etag = objectStoreRepository.uploadChunk(sf, chunk, fileStream);
    chunk.setServerEtag(etag);
    chunk.setProgress(Progress.COMPLETE);
    chunks.add(chunk);
    sf.setState(UploadState.ONGOING);

    return chunk;
  }

  @Override
  protected GenericDaoEs<File> getIndexDao() {
    return fileDaoEs;
  }
// move 2 dataset service
  public Page<File> listFiles(UUID id, Pageable pageable, R2D2Principal user) throws AuthorizationException, R2d2TechnicalException {
	  
	  DatasetVersion latestVersion = datasetVersionRepository.findLatestVersion(id);

	    checkAa("get", user, latestVersion);
    Page<File> list = fileRepository.findAllForVersion(latestVersion.getVersionId(), pageable);
    return list;
  }

  @PostFilter("filterObject.creator.id == principal.userAccount.id")
  public List<File> list() {
    List<File> list = new ArrayList<>();
    fileRepository.findAll().iterator().forEachRemaining(list::add);
    return list;
  }

  @PostAuthorize("returnObject.creator.id == principal.userAccount.id")
  public File list(UUID id) throws NotFoundException {
    File sf =
        fileRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("File with id %s MOT FOUND!", id.toString())));
    return sf;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public File completeChunkedUpload(UUID fileId, int parts, R2D2Principal user) throws R2d2TechnicalException, OptimisticLockingException,
      ValidationException, NotFoundException, InvalidStateException, AuthorizationException {
    checkAa("upload", user);
    File sf = fileRepository.findById(fileId)
        .orElseThrow(() -> new NotFoundException(String.format("File with id %s NOT FOUND!", fileId.toString())));
    // TODO: check number of parts in object store ...
    if (sf.getStateInfo().getChunks().size() == parts) {
      String etag = objectStoreRepository.createManifest(sf);
      sf.setChecksum(etag);
      sf.getStateInfo().setExpectedNumberOfChunks(parts);
      sf.setState(UploadState.COMPLETE);
      sf.setStorageLocation(objectStoreRepository.getPublicURI(sf.getId().toString()));
      return sf;
    } else {
      throw new InvalidStateException(String.format("Incorrect number of parts (expected %d, but got %d) in file with id %s", parts,
          sf.getStateInfo().getChunks().size(), fileId.toString()));
    }

  }

}
