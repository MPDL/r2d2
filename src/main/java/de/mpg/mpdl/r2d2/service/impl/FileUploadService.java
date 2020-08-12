package de.mpg.mpdl.r2d2.service.impl;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.r2d2.db.StagingFileRepository;
import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.InvalidStateException;
import de.mpg.mpdl.r2d2.exceptions.NotFoundException;
import de.mpg.mpdl.r2d2.exceptions.OptimisticLockingException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.exceptions.ValidationException;
import de.mpg.mpdl.r2d2.model.FileChunk;
import de.mpg.mpdl.r2d2.model.FileChunk.Progress;
import de.mpg.mpdl.r2d2.model.StagingFile;
import de.mpg.mpdl.r2d2.model.StagingFile.UploadState;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.search.dao.GenericDaoEs;
import de.mpg.mpdl.r2d2.search.dao.StagingFileDaoEs;
import de.mpg.mpdl.r2d2.service.StagingFileService;
import de.mpg.mpdl.r2d2.service.storage.SwiftObjectStoreRepository;

@Service
public class FileUploadService extends GenericServiceDbImpl<StagingFile> implements StagingFileService {

  public FileUploadService(Class<StagingFile> modelClazz) {
		super(modelClazz);
		// TODO Auto-generated constructor stub
	}

@Autowired
  StagingFileRepository stagingFileRepository;

  @Autowired
  SwiftObjectStoreRepository objectStoreRepository;

  @Autowired
  StagingFileDaoEs stagingFileDaoEs;

  @Override
  public StagingFile create(StagingFile object, R2D2Principal user)
      throws R2d2TechnicalException, ValidationException, AuthorizationException {
    setBasicCreationProperties(object, user.getUserAccount());
    try {
      stagingFileRepository.save(object);
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }
    return object;
  }

  @Override
  public StagingFile update(StagingFile object, R2D2Principal user) throws R2d2TechnicalException, OptimisticLockingException,
      ValidationException, NotFoundException, InvalidStateException, AuthorizationException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
  public boolean delete(UUID id, R2D2Principal user)
      throws R2d2TechnicalException, OptimisticLockingException, NotFoundException, InvalidStateException, AuthorizationException {
    try {
      stagingFileRepository.deleteById(id);
      return objectStoreRepository.deleteContainer(id.toString());
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }
    // stagingFileDaoEs.deleteImmediatly(id.toString());
  }

  @Override
  public StagingFile get(UUID id, R2D2Principal user) throws R2d2TechnicalException, NotFoundException, AuthorizationException {
    StagingFile sf = stagingFileRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(String.format("File with id %s NOT FOUND!", id.toString())));
    return sf;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public StagingFile uploadSingleFile(StagingFile file, InputStream fileStream, R2D2Principal user) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException {

    checkAa("upload", user);

    StagingFile sf = create(file, user);
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
  public StagingFile initNewFile(StagingFile file, R2D2Principal user) throws R2d2TechnicalException, OptimisticLockingException,
      ValidationException, NotFoundException, InvalidStateException, AuthorizationException {

    checkAa("upload", user);

    StagingFile sf = create(file, user);
    objectStoreRepository.createContainer(file.getId().toString());
    // stagingFileDaoEs.createImmediately(sf.getId().toString(), sf);

    return sf;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public FileChunk uploadFileChunk(UUID fileId, FileChunk chunk, InputStream fileStream, R2D2Principal user) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException {

    checkAa("upload", user);

    StagingFile sf = stagingFileRepository.findById(fileId)
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
  protected GenericDaoEs<StagingFile> getIndexDao() {
    return stagingFileDaoEs;
  }

  @PostFilter("filterObject.creator.id == principal.userAccount.id")
  public List<StagingFile> list() {
    List<StagingFile> list = new ArrayList<>();
    stagingFileRepository.findAll().iterator().forEachRemaining(list::add);
    return list;
  }
  
  @PostAuthorize("returnObject.creator.id == principal.userAccount.id")
  public StagingFile list(UUID id) throws NotFoundException {
	  StagingFile sf = stagingFileRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("File with id %s MOT FOUND!", id.toString())));
    return sf;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public StagingFile completeChunkedUpload(UUID fileId, int parts, R2D2Principal user) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException {
    checkAa("upload", user);
    StagingFile sf = stagingFileRepository.findById(fileId)
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
      throw new InvalidStateException(String.format("Incorrect number of parts (expected %d, but got %d) in file with id %s", parts, sf.getStateInfo().getChunks().size(), fileId.toString()));
    }

  }

}
