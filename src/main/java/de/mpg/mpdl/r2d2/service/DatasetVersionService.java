package de.mpg.mpdl.r2d2.service;

import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.InvalidStateException;
import de.mpg.mpdl.r2d2.exceptions.NotFoundException;
import de.mpg.mpdl.r2d2.exceptions.OptimisticLockingException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.exceptions.ValidationException;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.DatasetVersionMetadata;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.FileChunk;
import de.mpg.mpdl.r2d2.model.VersionId;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.service.util.FileDownloadWrapper;

public interface DatasetVersionService extends GenericService<DatasetVersion> {

  public DatasetVersion create(DatasetVersion object, R2D2Principal user)
      throws R2d2TechnicalException, ValidationException, AuthorizationException, InvalidStateException;

  public DatasetVersion update(UUID id, DatasetVersion object, R2D2Principal user) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException;

  /*
  public DatasetVersion addOrRemoveFile(UUID id, UUID fileId, OffsetDateTime lastModificationDate, R2D2Principal user, String action)
      throws R2d2TechnicalException, OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException,
      AuthorizationException;
  */
  /*
  public DatasetVersion createNewVersion(UUID id, DatasetVersion object, R2D2Principal user) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException;
  */

  public void delete(UUID id, OffsetDateTime lastModificationDate, R2D2Principal user)
      throws R2d2TechnicalException, OptimisticLockingException, NotFoundException, InvalidStateException, AuthorizationException;

  public DatasetVersion get(VersionId id, R2D2Principal user) throws R2d2TechnicalException, NotFoundException, AuthorizationException;

  public DatasetVersion getLatest(UUID id, R2D2Principal user) throws R2d2TechnicalException, NotFoundException, AuthorizationException;

  public DatasetVersion publish(UUID id, OffsetDateTime lastModificationDate, R2D2Principal user) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException;

  //public FileDownloadWrapper getFileContent(VersionId datasetId, UUID fileId, R2D2Principal user) throws R2d2TechnicalException,
  //   OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException;

  public Page<File> listFiles(VersionId datasetId, Pageable pageable, R2D2Principal user)
      throws AuthorizationException, R2d2TechnicalException, NotFoundException;

  public File getFileForDataset(VersionId datasetId, UUID fileId, R2D2Principal user)
      throws AuthorizationException, R2d2TechnicalException, NotFoundException;

  public DatasetVersion addFile(UUID id, UUID fileId, OffsetDateTime lastModificationDate, R2D2Principal user)
      throws R2d2TechnicalException, OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException,
      AuthorizationException;

  public DatasetVersion removeFile(UUID id, UUID fileId, OffsetDateTime lastModificationDate, R2D2Principal user)
      throws R2d2TechnicalException, OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException,
      AuthorizationException;

  public DatasetVersion updateFiles(UUID id, List<UUID> fileIds, OffsetDateTime lastModificationDate, R2D2Principal user)
      throws R2d2TechnicalException, OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException,
      AuthorizationException;
  /*
  public File uploadSingleFile(UUID datasetId, File file, InputStream fileStream, R2D2Principal user) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException;
  
  public File initNewFile(UUID datasetId, File file, R2D2Principal user) throws R2d2TechnicalException, OptimisticLockingException,
      ValidationException, NotFoundException, InvalidStateException, AuthorizationException;
  
  public FileChunk uploadFileChunk(UUID datasetId, UUID fileId, FileChunk chunk, InputStream fileStream, R2D2Principal user)
      throws R2d2TechnicalException, OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException,
      AuthorizationException;
  */


}
