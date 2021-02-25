package de.mpg.mpdl.r2d2.search.service.impl;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.Affiliation;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.search.dao.AffiliationDaoEs;
import de.mpg.mpdl.r2d2.search.dao.GenericDaoEs;
import de.mpg.mpdl.r2d2.search.model.SearchQuery;
import de.mpg.mpdl.r2d2.search.model.SearchResult;
import de.mpg.mpdl.r2d2.search.service.AffiliationSearchService;


@Service
public class AffiliationSearchServiceImpl extends GenericSearchServiceImpl<Affiliation> implements AffiliationSearchService {

  @Value("${index.affiliation.parent.field}")
  String parent_field;

  @Value("${index.affiliation.parent.value}")
  String parent_value;

  @Value("${index.affiliation.match.fields}")
  String[] matches;

  @Value("${index.affiliation.id.field}")
  String id_field;

  @Autowired
  AffiliationDaoEs affiliationDaoEs;

  public AffiliationSearchServiceImpl() {
    super(Affiliation.class);
  }

  @Override
  protected GenericDaoEs<Affiliation> getIndexDao() {
    return affiliationDaoEs;
  }

  @Override
  protected String getAaKey() {
    return "de.mpg.mpdl.r2d2.service.impl.FileUploadService";
  }

  @Override
  protected String getAaMethod() {
    return "upload";
  }

  public SearchResponse suggestOUs(String query) throws R2d2TechnicalException {
    TermQueryBuilder tqb = QueryBuilders.termQuery(parent_field, parent_value);
    MultiMatchQueryBuilder mmqb = QueryBuilders.multiMatchQuery(query, matches);
    BoolQueryBuilder bqb = QueryBuilders.boolQuery().should(tqb).must(mmqb);
    SearchSourceBuilder ssb = new SearchSourceBuilder();
    if (getIndexDao() != null) {
      ssb.query(bqb);
      return getIndexDao().searchDetailed(ssb, -1);
    }
    return null;
  }

  public SearchResponse ouDetails(String gridId) throws R2d2TechnicalException {
    TermQueryBuilder tqb = QueryBuilders.termQuery(id_field, gridId);
    BoolQueryBuilder bqb = QueryBuilders.boolQuery().must(tqb);
    SearchSourceBuilder ssb = new SearchSourceBuilder();
    if (getIndexDao() != null) {
      ssb.query(bqb);
      return getIndexDao().searchDetailed(ssb, -1);
    }
    return null;
  }

  @Override
  protected QueryBuilder modifyQueryOnlyMine(QueryBuilder qb, R2D2Principal p) {
    return null;
  }
}
