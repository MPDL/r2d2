package de.mpg.mpdl.r2d2.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.stream.Stream;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Basic;

import de.mpg.mpdl.r2d2.aa.AuthorizationService;
import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.OptimisticLockingException;
import de.mpg.mpdl.r2d2.exceptions.R2d2ApplicationException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.BaseDb;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.model.aa.UserAccountRO;
import de.mpg.mpdl.r2d2.search.dao.GenericDaoEs;
import de.mpg.mpdl.r2d2.util.Utils;

public abstract class GenericServiceDbImpl<E> {


  @Autowired
  private AuthorizationService aaService;


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



  protected void checkEqualModificationDate(OffsetDateTime date1, OffsetDateTime date2) throws OptimisticLockingException {
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

  
  protected void setBasicCreationProperties(BaseDb baseObject, UserAccount creator)
  {
    //Truncate to microseconds, as the database doesn't support more
    setBasicCreationProperties(baseObject, creator, Utils.generateCurrentDateTimeForDatabase());
  }
  
  protected void setBasicCreationProperties(BaseDb baseObject, UserAccount creator, OffsetDateTime dateTime)
  {
    baseObject.setCreator(new UserAccountRO(creator));
    baseObject.setCreationDate(dateTime);
    setBasicModificationProperties(baseObject, creator, dateTime);
  }
  
  protected void setBasicModificationProperties(BaseDb baseObject, UserAccount creator)
  {
    //Truncate to microseconds, as the database doesn't support more
    setBasicModificationProperties(baseObject, creator,  Utils.generateCurrentDateTimeForDatabase());
  }
  
  protected void setBasicModificationProperties(BaseDb baseObject, UserAccount creator, OffsetDateTime dateTime)
  {
    baseObject.setModifier(new UserAccountRO(creator));
    baseObject.setModificationDate(dateTime);
  }
  
  protected abstract GenericDaoEs<E> getIndexDao();

}
