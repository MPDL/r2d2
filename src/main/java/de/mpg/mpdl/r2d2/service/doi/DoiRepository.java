package de.mpg.mpdl.r2d2.service.doi;

import de.mpg.mpdl.r2d2.model.DatasetVersion;

public interface DoiRepository {

  //TODO: Name the class DoiWebClient?

  public String create(DatasetVersion datasetVersion);

}
