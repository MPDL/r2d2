package de.mpg.mpdl.r2d2.search.service;

import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.VersionId;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.rest.controller.dto.DatasetVersionDto;
import de.mpg.mpdl.r2d2.search.model.DatasetVersionIto;
import de.mpg.mpdl.r2d2.search.model.FileIto;
import de.mpg.mpdl.r2d2.search.model.SearchQuery;
import de.mpg.mpdl.r2d2.search.model.SearchResult;

public interface FileSearchService extends GenericSearchService<FileIto> {

  public SearchResult<FileIto> searchFilesForDataset(SearchQuery sq, VersionId datasetId, R2D2Principal principal)
      throws R2d2TechnicalException, AuthorizationException;

}
