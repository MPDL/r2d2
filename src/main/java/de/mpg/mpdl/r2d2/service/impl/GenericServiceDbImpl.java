package de.mpg.mpdl.r2d2.service.impl;

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
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
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
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.model.aa.UserAccountRO;
import de.mpg.mpdl.r2d2.search.dao.GenericDaoEs;
import de.mpg.mpdl.r2d2.search.model.SearchQuery;
import de.mpg.mpdl.r2d2.search.model.SearchRecord;
import de.mpg.mpdl.r2d2.search.model.SearchResult;
import de.mpg.mpdl.r2d2.util.Utils;

public abstract class GenericServiceDbImpl<E> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GenericServiceDbImpl.class);

  @Autowired
  private AuthorizationService aaService;


  @Autowired
  private ObjectMapper jsonObjectMapper;

  private Class<E> modelClazz;

  public GenericServiceDbImpl(Class<E> modelClazz) {
    this.modelClazz = modelClazz;
  }

  /*
  public SearchResponse searchDetailed(SearchSourceBuilder ssb, R2D2Principal principal)
      throws R2d2TechnicalException, AuthorizationException {
  
    return searchDetailed(ssb, -1, principal);
  }
  
  public SearchResponse searchDetailed(SearchSourceBuilder ssb, long scrollTime, R2D2Principal principal)
      throws R2d2TechnicalException, AuthorizationException {
  
    if (getIndexDao() != null) {
      QueryBuilder qb = ssb.query();
      if (principal != null) {
        qb = aaService.modifyQueryForAa(this.getClass().getCanonicalName(), qb, principal);
      } else {
        qb = aaService.modifyQueryForAa(this.getClass().getCanonicalName(), qb, null);
      }
      ssb.query(qb);
      return getIndexDao().searchDetailed(ssb, scrollTime);
    }
    return null;
  }
  
  
  public SearchResult<E> search(SearchQuery sq, R2D2Principal principal) throws R2d2TechnicalException, AuthorizationException {
    QueryBuilder qb = QueryBuilders.queryStringQuery(sq.getQuery() != null ? sq.getQuery() : "*");
    SearchSourceBuilder ssb = SearchSourceBuilder.searchSource();
  
    ssb.query(qb);
    ssb.size(sq.getSize());
    ssb.from(sq.getFrom());
  
  
  
    SearchResponse resp = searchDetailed(ssb, principal);
    return getSearchRetrieveResponseFromElasticSearchResponse(resp);
  
  
  
  }
  
  private SearchResult<E> getSearchRetrieveResponseFromElasticSearchResponse(SearchResponse sr) throws R2d2TechnicalException {
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
  
  
  */



  protected void setBasicCreationProperties(BaseDateDb baseObject, UserAccount creator) {
    //Truncate to microseconds, as the database doesn't support more
    setBasicCreationProperties(baseObject, creator, Utils.generateCurrentDateTimeForDatabase());
  }

  protected void setBasicCreationProperties(BaseDateDb baseObject, UserAccount creator, OffsetDateTime dateTime) {
    baseObject.setCreator(creator);
    baseObject.setCreationDate(dateTime);
    setBasicModificationProperties(baseObject, creator, dateTime);
  }

  protected void setBasicModificationProperties(BaseDateDb baseObject, UserAccount creator) {
    //Truncate to microseconds, as the database doesn't support more
    setBasicModificationProperties(baseObject, creator, Utils.generateCurrentDateTimeForDatabase());
  }

  protected void setBasicModificationProperties(BaseDateDb baseObject, UserAccount creator, OffsetDateTime dateTime) {
    baseObject.setModifier(creator);
    baseObject.setModificationDate(dateTime);
  }

  /*
  protected abstract GenericDaoEs<E> getIndexDao();
  */

  protected void checkEqualModificationDate(OffsetDateTime date1, OffsetDateTime date2) throws OptimisticLockingException {
    LOGGER.debug("Compare date " + date1 + " with " + date2);
    if (date1 == null || date2 == null || !date1.toInstant().equals(date2.toInstant())) {
      throw new OptimisticLockingException("Object changed in the meantime: " + date1 + "  does not equal  " + date2);
    }
  }


  protected void checkAa(String method, R2D2Principal userAccount, Object... objects)
      throws R2d2TechnicalException, AuthorizationException {
    if (objects == null) {
      objects = new Object[0];
    }
    objects = Stream.concat(Arrays.stream(new Object[] {userAccount}), Arrays.stream(objects)).toArray();
    aaService.checkAuthorization(this.getClass().getCanonicalName(), method, objects);
  }


}
