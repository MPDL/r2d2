package de.mpg.mpdl.r2d2.search.service.impl;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Basic;

import de.mpg.mpdl.r2d2.aa.AuthorizationService;
import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.OptimisticLockingException;
import de.mpg.mpdl.r2d2.exceptions.R2d2ApplicationException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.BaseDateDb;
import de.mpg.mpdl.r2d2.model.BaseDb;
import de.mpg.mpdl.r2d2.model.aa.Grant;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.model.aa.UserAccountRO;
import de.mpg.mpdl.r2d2.model.aa.UserAccount.Role;
import de.mpg.mpdl.r2d2.search.dao.GenericDaoEs;
import de.mpg.mpdl.r2d2.search.es.daoimpl.DatasetVersionDaoImpl;
import de.mpg.mpdl.r2d2.search.model.SearchQuery;
import de.mpg.mpdl.r2d2.search.model.SearchRecord;
import de.mpg.mpdl.r2d2.search.model.SearchResult;
import de.mpg.mpdl.r2d2.search.service.GenericSearchService;
import de.mpg.mpdl.r2d2.util.Utils;

public abstract class GenericSearchServiceImpl<E> implements GenericSearchService<E> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GenericSearchServiceImpl.class);

  @Autowired
  private AuthorizationService aaService;


  @Autowired
  private ObjectMapper jsonObjectMapper;

  private Class<E> modelClazz;

  public GenericSearchServiceImpl(Class<E> modelClazz) {
    this.modelClazz = modelClazz;
  }

  public SearchResponse searchDetailed(SearchSourceBuilder ssb, boolean mineOnly, R2D2Principal principal)
      throws R2d2TechnicalException, AuthorizationException {

    return searchDetailed(ssb, -1, mineOnly, principal);
  }

  public SearchResponse searchDetailed(SearchSourceBuilder ssb, long scrollTime, boolean mineOnly, R2D2Principal principal)
      throws R2d2TechnicalException, AuthorizationException {

    if (getIndexDao() != null) {
      QueryBuilder qb = ssb.query();

      QueryBuilder myDatasetQuery = getAdditionalFilterQuery(qb, mineOnly, principal);;
      QueryBuilder aaQuery = aaService.getAaFilterQuery(getAaKey(), getAaMethod(), principal, null);

      BoolQueryBuilder filterQuery = QueryBuilders.boolQuery();
      if (qb != null) {
        filterQuery.must(qb);
      }

      if (myDatasetQuery != null) {
        filterQuery.filter(myDatasetQuery);
      }

      if (aaQuery != null) {
        filterQuery.filter(aaQuery);
      }

      qb = filterQuery;


      ssb.query(qb);
      LOGGER.debug(ssb.toString());
      return getIndexDao().searchDetailed(ssb, scrollTime);
    }
    return null;
  }

  protected abstract QueryBuilder getAdditionalFilterQuery(QueryBuilder qb, boolean mineOnly, R2D2Principal p);

  public SearchResult<E> search(SearchQuery sq, boolean mineOnly, R2D2Principal principal)
      throws R2d2TechnicalException, AuthorizationException {
    SearchResponse resp = searchDetailed(buildSearchSourceBuilderFromSearchQuery(sq), mineOnly, principal);
    return buildSearchRetrieveResponseFromElasticSearchResponse(resp);
  }


  protected SearchSourceBuilder buildSearchSourceBuilderFromSearchQuery(SearchQuery sq) {
    QueryBuilder qb = QueryBuilders.queryStringQuery(sq.getQuery() != null ? sq.getQuery() : "*");
    SearchSourceBuilder ssb = SearchSourceBuilder.searchSource();

    ssb.query(qb);
    ssb.size(sq.getSize());
    ssb.from(sq.getFrom());
    return ssb;
  }

  protected SearchResult<E> buildSearchRetrieveResponseFromElasticSearchResponse(SearchResponse sr) throws R2d2TechnicalException {
    SearchResult<E> srrVO;
    try {
      srrVO = new SearchResult<E>();
      //srrVO.setOriginalResponse(sr);
      srrVO.setTotal((int) sr.getHits().getTotalHits().value);
      srrVO.setScrollId(sr.getScrollId());

      List<SearchRecord<E>> hitList = new ArrayList<>();
      srrVO.setHits(hitList);
      for (SearchHit hit : sr.getHits().getHits()) {
        SearchRecord<E> srr = new SearchRecord<E>();
        hitList.add(srr);

        E source = jsonObjectMapper.readValue(hit.getSourceAsString(), modelClazz);

        srr.setSource(source);
        srr.setId(hit.getId());

      }
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }


    return srrVO;
  }

  protected abstract GenericDaoEs<E> getIndexDao();

  protected abstract String getAaKey();

  protected abstract String getAaMethod();



}
