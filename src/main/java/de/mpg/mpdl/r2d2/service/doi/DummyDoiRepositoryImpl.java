package de.mpg.mpdl.r2d2.service.doi;

import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.transformation.doi.DoiDataCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Repository("DummyDoiRepositoryImpl")
public class DummyDoiRepositoryImpl implements DoiRepository {

  @Autowired
  private DoiDataCreator doiDataCreator;

  @Override
  public String createDraftDoi(DatasetVersion datasetVersion) throws R2d2TechnicalException {
    //        doiDataCreator.createDoiDataForDraftDoiCreation(datasetVersion);

    return "10.12345/dummy-doi." + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
  }

  @Override
  public String updateToFindableDoi(DatasetVersion datasetVersion) throws R2d2TechnicalException {
    //        doiDataCreator.createDoiDataForDoiPublication(datasetVersion);

    return "10.12345/dummy-doi." + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
  }
}
