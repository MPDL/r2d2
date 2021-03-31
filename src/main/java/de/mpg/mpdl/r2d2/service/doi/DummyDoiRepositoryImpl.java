package de.mpg.mpdl.r2d2.service.doi;

import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.transformation.doi.DoiDataCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Temporal implementation of the DoiRepository. NO Rest-Calls to Datacite. Creates and returns
 * dummy dois.
 */
// TODO: Remove DummyDoiRepository if not needed anymore
@Repository("DummyDoiRepositoryImpl")
public class DummyDoiRepositoryImpl implements DoiRepository {

  private static Logger LOGGER = LoggerFactory.getLogger(DummyDoiRepositoryImpl.class);

  @Autowired
  private DoiDataCreator doiDataCreator;

  @Override
  public String createDraftDoi(DatasetVersion datasetVersion) throws R2d2TechnicalException {
    doiDataCreator.createDoiDataForDraftDoiCreation(datasetVersion);

    String doi = "10.12345/dummy-draft-doi." + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

    LOGGER.info("Created a Draft Doi: " + doi);

    return doi;
  }

  @Override
  public String updateToFindableDoi(DatasetVersion datasetVersion) throws R2d2TechnicalException {
    doiDataCreator.createDoiDataForDoiPublication(datasetVersion);

    String doi = datasetVersion.getMetadata().getDoi();

    LOGGER.info("Updated Doi to Findable Doi: " + doi);

    return doi;
  }
}
