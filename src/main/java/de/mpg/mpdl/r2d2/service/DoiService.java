package de.mpg.mpdl.r2d2.service;

import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.NotFoundException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;

import java.util.UUID;

public interface DoiService {

  public String createDoiForDataset(UUID datasetId, R2D2Principal user)
      throws R2d2TechnicalException, NotFoundException, AuthorizationException;
}
