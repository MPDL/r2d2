package de.mpg.mpdl.r2d2.service;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.UUID;

import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.InvalidStateException;
import de.mpg.mpdl.r2d2.exceptions.NotFoundException;
import de.mpg.mpdl.r2d2.exceptions.OptimisticLockingException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.exceptions.ValidationException;
import de.mpg.mpdl.r2d2.model.FileChunk;
import de.mpg.mpdl.r2d2.model.StagingFile;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;

public interface StagingFileService extends GenericService<StagingFile> {

  public StagingFile create(StagingFile object, R2D2Principal user)
      throws R2d2TechnicalException, ValidationException, AuthorizationException;

  public StagingFile update(StagingFile object, R2D2Principal user) throws R2d2TechnicalException, OptimisticLockingException,
      ValidationException, NotFoundException, InvalidStateException, AuthorizationException;

  public boolean delete(UUID id, R2D2Principal user)
      throws R2d2TechnicalException, OptimisticLockingException, NotFoundException, InvalidStateException, AuthorizationException;

  public StagingFile get(UUID id, R2D2Principal user) throws R2d2TechnicalException, NotFoundException, AuthorizationException;

  public StagingFile uploadSingleFile(StagingFile file, InputStream fileStream, R2D2Principal user) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException;

  public StagingFile initNewFile(StagingFile file, R2D2Principal user) throws R2d2TechnicalException, OptimisticLockingException,
      ValidationException, NotFoundException, InvalidStateException, AuthorizationException;

  public FileChunk uploadFileChunk(UUID fileId, FileChunk chunk, InputStream fileStream, R2D2Principal user) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException;

  public StagingFile completeChunkedUpload(UUID fileId, int parts, R2D2Principal user) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException;

}
