package de.mpg.mpdl.r2d2.service;

import java.time.OffsetDateTime;
import java.util.UUID;

import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.InvalidStateException;
import de.mpg.mpdl.r2d2.exceptions.NotFoundException;
import de.mpg.mpdl.r2d2.exceptions.OptimisticLockingException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.exceptions.ValidationException;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;

public interface DatasetVersionService {

  public DatasetVersion create(DatasetVersion object, UserAccount user)
      throws R2d2TechnicalException, ValidationException, AuthorizationException;

  public DatasetVersion update(DatasetVersion object, UserAccount user) throws R2d2TechnicalException, OptimisticLockingException,
      ValidationException, NotFoundException, InvalidStateException, AuthorizationException;

  public DatasetVersion createNewVersion(DatasetVersion object, UserAccount user) throws R2d2TechnicalException, OptimisticLockingException,
      ValidationException, NotFoundException, InvalidStateException, AuthorizationException;

  public void delete(UUID id, OffsetDateTime lastModificationDate, UserAccount user)
      throws R2d2TechnicalException, OptimisticLockingException, NotFoundException, InvalidStateException, AuthorizationException;

  public DatasetVersion get(UUID id, UserAccount user) throws R2d2TechnicalException, NotFoundException, AuthorizationException;

  public void publish(UUID id, OffsetDateTime lastModificationDate, UserAccount user) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException;

}
