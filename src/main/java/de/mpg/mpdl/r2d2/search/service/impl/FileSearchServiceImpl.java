package de.mpg.mpdl.r2d2.search.service.impl;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.VersionId;
import de.mpg.mpdl.r2d2.model.aa.Grant;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.model.aa.UserAccount.Role;
import de.mpg.mpdl.r2d2.search.dao.FileDaoEs;
import de.mpg.mpdl.r2d2.search.dao.GenericDaoEs;
import de.mpg.mpdl.r2d2.search.es.daoimpl.DatasetVersionDaoImpl;
import de.mpg.mpdl.r2d2.search.es.daoimpl.FileDaoImpl;
import de.mpg.mpdl.r2d2.search.model.DatasetVersionIto;
import de.mpg.mpdl.r2d2.search.model.FileIto;
import de.mpg.mpdl.r2d2.search.model.SearchQuery;
import de.mpg.mpdl.r2d2.search.model.SearchResult;
import de.mpg.mpdl.r2d2.search.service.FileSearchService;

@Service
public class FileSearchServiceImpl extends GenericSearchServiceImpl<FileIto> implements FileSearchService {


  @Autowired
  private FileDaoEs fileDao;

  public FileSearchServiceImpl() {
    super(FileIto.class);
  }

  @Override
  protected GenericDaoEs getIndexDao() {
    return fileDao;
  }

  @Override
  protected String getAaKey() {
    return "de.mpg.mpdl.r2d2.service.impl.FileUploadService";
  }

  @Override
  protected String getAaMethod() {
    return "get";
  }

  public SearchResult<FileIto> searchFilesForDataset(SearchQuery sq, VersionId datasetId, R2D2Principal principal)
      throws R2d2TechnicalException, AuthorizationException {

    SearchSourceBuilder ssb = buildSearchSourceBuilderFromSearchQuery(sq);

    BoolQueryBuilder bq = QueryBuilders.boolQuery();
    if (ssb.query() != null) {
      bq.must(ssb.query());
    }

    bq.filter(QueryBuilders.termQuery(FileDaoImpl.INDEX_FILE_DATASETS_VERSIONID, datasetId.getVersionId()));

    ssb.query(bq);

    SearchResponse resp = searchDetailed(ssb, false, principal);

    return buildSearchRetrieveResponseFromElasticSearchResponse(resp);

  }

  @Override
  protected QueryBuilder modifyQueryOnlyMine(QueryBuilder qb, R2D2Principal principal) {

    //Only return "my files" when logged in
    if (principal != null && principal.getUserAccount() != null) {


      BoolQueryBuilder myDatasetQuery = null;
      BoolQueryBuilder roleQuery = QueryBuilders.boolQuery();


      for (Grant grant : principal.getUserAccount().getGrants()) {
        if (Role.ADMIN.equals(grant.getRole())) {
          myDatasetQuery = null;
          break;
        } else if (Role.USER.equals(grant.getRole())) {


          roleQuery.should(QueryBuilders.termQuery(FileDaoImpl.INDEX_FILE_CREATOR_ID, principal.getUserAccount().getId().toString()));
          roleQuery
              .should(QueryBuilders.termQuery(FileDaoImpl.INDEX_FILE_DATASET_CREATOR_ID, principal.getUserAccount().getId().toString()));

        } else if (Role.DATAMANAGER.equals(grant.getRole())) {

          roleQuery.should(QueryBuilders.termQuery(FileDaoImpl.INDEX_FILE_DATASET_ID, grant.getDataset().toString()));

        }

      }

      if (roleQuery.hasClauses()) {
        myDatasetQuery = QueryBuilders.boolQuery();
        myDatasetQuery.must(roleQuery);
      }


      BoolQueryBuilder filterQuery = QueryBuilders.boolQuery();
      if (qb != null) {
        filterQuery.must(qb);
      }
      if(myDatasetQuery != null) {
        filterQuery.filter(myDatasetQuery);
      }

      qb = filterQuery;



    }

    return qb;


  }



}
