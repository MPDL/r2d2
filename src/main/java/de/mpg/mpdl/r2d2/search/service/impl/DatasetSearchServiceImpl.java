package de.mpg.mpdl.r2d2.search.service.impl;

import de.mpg.mpdl.r2d2.search.dao.DatasetVersionDaoEs;
import de.mpg.mpdl.r2d2.search.dao.GenericDaoEs;
import de.mpg.mpdl.r2d2.search.es.daoimpl.DatasetVersionDaoImpl;
import de.mpg.mpdl.r2d2.search.model.DatasetVersionIto;
import de.mpg.mpdl.r2d2.search.service.DatasetSearchService;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.aa.Grant;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.model.aa.UserAccount.Role;
import de.mpg.mpdl.r2d2.rest.controller.dto.DatasetVersionDto;

@Service
public class DatasetSearchServiceImpl extends GenericSearchServiceImpl<DatasetVersionIto> implements DatasetSearchService {

  @Autowired
  @Qualifier("PublicDatasetVersionDaoImpl")
  private DatasetVersionDaoEs datasetDao;

  public DatasetSearchServiceImpl() {
    super(DatasetVersionIto.class);
  }

  @Override
  protected GenericDaoEs<DatasetVersionIto> getIndexDao() {
    return datasetDao;
  }

  @Override
  protected String getAaKey() {
    return "de.mpg.mpdl.r2d2.service.impl.DatasetVersionServiceDbImpl";
  }

  @Override
  protected String getAaMethod() {
    return "get";
  }

  @Override
  public SearchResponse searchDetailed(SearchSourceBuilder ssb, long scrollTime, R2D2Principal principal)
      throws R2d2TechnicalException, AuthorizationException {

    //Only return "my datasets" when logged in
    if (principal != null && principal.getUserAccount() != null) {


      QueryBuilder myDatasetQuery = null;


      BoolQueryBuilder roleQuery = QueryBuilders.boolQuery();

      QueryBuilder isLatestVersionQuery = QueryBuilders.scriptQuery(new Script(
          "doc['" + DatasetVersionDaoImpl.INDEX_DATASET_LATEST_VERSION + "']==doc['" + DatasetVersionDaoImpl.INDEX_VERSION_NUMBER + "']"));

      for (Grant grant : principal.getUserAccount().getGrants()) {
        if (Role.ADMIN.equals(grant.getRole())) {
          myDatasetQuery = isLatestVersionQuery;
          break;
        } else if (Role.USER.equals(grant.getRole())) {


          roleQuery.should(
              QueryBuilders.termQuery(DatasetVersionDaoImpl.INDEX_DATASET_CREATOR_ID, principal.getUserAccount().getId().toString()));
          /*
          roleQuery.should(
              QueryBuilders.termQuery(DatasetVersionDaoImpl.INDEX_DATASET_DATAMANAGER_ID, principal.getUserAccount().getId().toString()));
          
           */


        } else if (Role.DATAMANAGER.equals(grant.getRole())) {

          roleQuery.should(QueryBuilders.termQuery(DatasetVersionDaoImpl.INDEX_DATASET_ID, grant.getDataset().toString()));

        }

      }

      if (roleQuery.hasClauses()) {
        BoolQueryBuilder userQuery = QueryBuilders.boolQuery();
        userQuery.must(roleQuery);
        userQuery.must(isLatestVersionQuery);
        myDatasetQuery = userQuery;
      }


      BoolQueryBuilder filterQuery = QueryBuilders.boolQuery();
      if (ssb.query() != null) {
        filterQuery.must(ssb.query());
      }
      filterQuery.filter(myDatasetQuery);
      ssb.query(filterQuery);



    }

    return super.searchDetailed(ssb, scrollTime, principal);

  }



}
