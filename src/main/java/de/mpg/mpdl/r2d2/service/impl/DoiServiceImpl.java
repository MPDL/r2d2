package de.mpg.mpdl.r2d2.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import de.mpg.mpdl.r2d2.db.DatasetVersionRepository;
import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.NotFoundException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.service.DoiService;

//TODO: DOI Service & Interface currently not used. Remove both if no explicit (user controlled) Doi creation will be implemented.
public class DoiServiceImpl implements DoiService {

  @Autowired
  private DatasetVersionRepository datasetVersionRepository;

  @Override
  public String createDoiForDataset(UUID datasetId, R2D2Principal user)
      throws R2d2TechnicalException, NotFoundException, AuthorizationException {
    //TODO: checkAA

    //TODO: Use findLatestVersion() OR findLatestPublicVersion() ?
    DatasetVersion latestDatasetVersion = datasetVersionRepository.findLatestVersion(datasetId);

    //TODO: Create DOI-Metadata XML

    //TODO: REST call for DOI -> DoiRepository

    //TODO: Save DOI in Dataset

    //TODO: Return DOI
    return null;
  }

}
