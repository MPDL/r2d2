package de.mpg.mpdl.r2d2.service.doi;

import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.DatasetVersion;

public interface DoiRepository {

  //TODO: Have an extra more generic Interface/Implementation for a this DoiRepository and/or Doi-WebClient?
  //with createDraft/updateEvent/... depending on the state/attributes of the DatasetVersion

  public String createDraftDoi(DatasetVersion datasetVersion) throws R2d2TechnicalException;

  public String updateToFindableDoi(DatasetVersion datasetVersion) throws R2d2TechnicalException;

}
