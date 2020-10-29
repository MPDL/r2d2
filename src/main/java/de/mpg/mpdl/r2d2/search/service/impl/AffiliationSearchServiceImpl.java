package de.mpg.mpdl.r2d2.search.service.impl;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
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
public class AffiliationSearchServiceImpl extends GenericSearchServiceImpl<Affiliation>
		implements AffiliationSearchService {

	public final static String PARENT_FIELD = "relationships.label.keyword";
	public final static String PARENT_VALUE = "Max Planck Society";
	public final static String[] MULTI_MATCH_FIELDS = { "name.auto", "acronyms.auto", "labels.label.auto" };

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

	// unauthorized search required 4 user registration
	public SearchResponse searchDetailed(SearchSourceBuilder ssb, long scrollTime) throws R2d2TechnicalException {

		if (getIndexDao() != null) {
			QueryBuilder qb = ssb.query();
			ssb.query(qb);
			return getIndexDao().searchDetailed(ssb, scrollTime);
		}
		return null;
	}

	public SearchResponse suggestOUs(String query) throws R2d2TechnicalException {
		TermQueryBuilder tqb = QueryBuilders.termQuery(AffiliationSearchServiceImpl.PARENT_FIELD,
				AffiliationSearchServiceImpl.PARENT_VALUE);
		MultiMatchQueryBuilder mmqb = QueryBuilders.multiMatchQuery(query,
				AffiliationSearchServiceImpl.MULTI_MATCH_FIELDS);
		BoolQueryBuilder bqb = QueryBuilders.boolQuery().should(tqb).must(mmqb);
		SearchSourceBuilder ssb = new SearchSourceBuilder();
		if (getIndexDao() != null) {
			ssb.query(bqb);
			return getIndexDao().searchDetailed(ssb, -1);	
		}
		return null;
	}
	
	public SearchResponse ouDetails(String gridId) throws R2d2TechnicalException {
		TermQueryBuilder tqb = QueryBuilders.termQuery("id.keyword",gridId);
		BoolQueryBuilder bqb = QueryBuilders.boolQuery().must(tqb);
		SearchSourceBuilder ssb = new SearchSourceBuilder();
		if (getIndexDao() != null) {
			ssb.query(bqb);
			return getIndexDao().searchDetailed(ssb, -1);	
		}
		return null;
	}
}
