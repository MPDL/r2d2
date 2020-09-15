package de.mpg.mpdl.r2d2.service;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.InvalidStateException;
import de.mpg.mpdl.r2d2.exceptions.NotFoundException;
import de.mpg.mpdl.r2d2.exceptions.OptimisticLockingException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.exceptions.ValidationException;
import de.mpg.mpdl.r2d2.model.FileChunk;
import de.mpg.mpdl.r2d2.model.VersionId;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.service.util.FileDownloadWrapper;

public interface FileService extends GenericService<File> {

  public File create(File object, R2D2Principal user) throws R2d2TechnicalException, ValidationException, AuthorizationException;

  public File update(File object, R2D2Principal user) throws R2d2TechnicalException, OptimisticLockingException, ValidationException,
      NotFoundException, InvalidStateException, AuthorizationException;

  public boolean delete(UUID id, R2D2Principal user)
      throws R2d2TechnicalException, OptimisticLockingException, NotFoundException, InvalidStateException, AuthorizationException;

  public File get(UUID id, R2D2Principal user) throws R2d2TechnicalException, NotFoundException, AuthorizationException;

  public Page<File> list(Pageable pageable, R2D2Principal user) throws R2d2TechnicalException, NotFoundException, AuthorizationException;

  public File uploadSingleFile(File file, InputStream fileStream, R2D2Principal user) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException;

  public File initNewFile(File file, R2D2Principal user) throws R2d2TechnicalException, OptimisticLockingException, ValidationException,
      NotFoundException, InvalidStateException, AuthorizationException;

  public FileChunk uploadFileChunk(UUID fileId, FileChunk chunk, InputStream fileStream, R2D2Principal user) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException;

  public File completeChunkedUpload(UUID fileId, int parts, R2D2Principal user) throws R2d2TechnicalException, OptimisticLockingException,
      ValidationException, NotFoundException, InvalidStateException, AuthorizationException;

  public FileDownloadWrapper getFileContent(UUID fileId, R2D2Principal user) throws R2d2TechnicalException, OptimisticLockingException,
      ValidationException, NotFoundException, InvalidStateException, AuthorizationException;

}
