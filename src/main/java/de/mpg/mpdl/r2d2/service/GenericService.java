package de.mpg.mpdl.r2d2.service;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.R2d2ApplicationException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;

public interface GenericService<ModelObject> {
  
  public SearchResponse searchDetailed(SearchSourceBuilder ssb, long scrollTime, R2D2Principal principal)
      throws R2d2TechnicalException,  AuthorizationException;
  
  public SearchResponse searchDetailed(SearchSourceBuilder ssb, R2D2Principal principal)
      throws R2d2TechnicalException,  AuthorizationException;

}
