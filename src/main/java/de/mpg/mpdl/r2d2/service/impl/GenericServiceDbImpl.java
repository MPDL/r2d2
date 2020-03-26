package de.mpg.mpdl.r2d2.service.impl;

import java.time.OffsetDateTime;

import de.mpg.mpdl.r2d2.exceptions.OptimisticLockingException;
import de.mpg.mpdl.r2d2.model.BaseDb;
import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.Dataset.State;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;

public class GenericServiceDbImpl<E> {



  protected void checkEqualModificationDate(OffsetDateTime date1, OffsetDateTime date2) throws OptimisticLockingException {
    if (date1 == null || date2 == null || !date1.isEqual(date2)) {
      throw new OptimisticLockingException("Object changed in the meantime: " + date1 + "  does not equal  " + date2);
    }
  }

}
