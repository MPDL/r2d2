package de.mpg.mpdl.r2d2.service;

import java.time.OffsetDateTime;
import java.util.UUID;

import de.mpg.mpdl.r2d2.exceptions.InvalidStateException;
import de.mpg.mpdl.r2d2.exceptions.NotFoundException;
import de.mpg.mpdl.r2d2.exceptions.OptimisticLockingException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.exceptions.ValidationException;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.User;

public interface DatasetService {
	
	public DatasetVersion create(DatasetVersion object, User user)
			throws R2d2TechnicalException, ValidationException;

	public DatasetVersion update(DatasetVersion object, User user)
			throws R2d2TechnicalException, OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException;
	
	public DatasetVersion createNewVersion(DatasetVersion object, User user)
			throws R2d2TechnicalException, OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException;

	public void delete(UUID id, OffsetDateTime lastModificationDate, User user)
			throws R2d2TechnicalException, OptimisticLockingException, NotFoundException, InvalidStateException;

	public DatasetVersion get(UUID id, User user)
			throws R2d2TechnicalException, NotFoundException;
	
	public void publish(UUID id, OffsetDateTime lastModificationDate, User user)
			throws R2d2TechnicalException, OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException;

}
