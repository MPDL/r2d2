package de.mpg.mpdl.r2d2.service.storage;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import de.mpg.mpdl.r2d2.db.FileRepository;
import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.InvalidStateException;
import de.mpg.mpdl.r2d2.exceptions.NotFoundException;
import de.mpg.mpdl.r2d2.exceptions.OptimisticLockingException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.exceptions.ValidationException;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.service.FileService;

public class SwiftObjectStoreService implements FileService {

  private FileRepository fileRepository;
  private FileStorageService storageService;

  @Autowired
  public SwiftObjectStoreService(FileRepository repository, FileStorageService service) {
    this.fileRepository = repository;
    this.storageService = service;
  }

  @Override
  public SearchResponse searchDetailed(SearchSourceBuilder ssb, long scrollTime, R2D2Principal principal)
      throws R2d2TechnicalException, AuthorizationException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SearchResponse searchDetailed(SearchSourceBuilder ssb, R2D2Principal principal)
      throws R2d2TechnicalException, AuthorizationException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public File create(File object, R2D2Principal user) throws R2d2TechnicalException, ValidationException, AuthorizationException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public File update(File object, R2D2Principal user) throws R2d2TechnicalException, OptimisticLockingException, ValidationException,
      NotFoundException, InvalidStateException, AuthorizationException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void delete(UUID id, OffsetDateTime lastModificationDate, R2D2Principal user)
      throws R2d2TechnicalException, OptimisticLockingException, NotFoundException, InvalidStateException, AuthorizationException {
    // TODO Auto-generated method stub

  }

  @Override
  public File get(UUID id, R2D2Principal user) throws R2d2TechnicalException, NotFoundException, AuthorizationException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void publish(UUID id, OffsetDateTime lastModificationDate, R2D2Principal user) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException {
    // TODO Auto-generated method stub

  }

}
