package de.mpg.mpdl.r2d2.search.service;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.search.model.SearchQuery;
import de.mpg.mpdl.r2d2.search.model.SearchResult;

public interface GenericSearchService<E> {


  public SearchResult<E> search(SearchQuery sq, boolean mineOnly, R2D2Principal principal)
      throws R2d2TechnicalException, AuthorizationException;

  public SearchResponse searchDetailed(SearchSourceBuilder ssb, long scrollTime, boolean mineOnly, R2D2Principal principal)
      throws R2d2TechnicalException, AuthorizationException;

  public SearchResponse searchDetailed(SearchSourceBuilder ssb, boolean mineOnly, R2D2Principal principal)
      throws R2d2TechnicalException, AuthorizationException;

}
