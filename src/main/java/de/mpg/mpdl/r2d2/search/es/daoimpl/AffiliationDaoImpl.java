package de.mpg.mpdl.r2d2.search.es.daoimpl;

import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.Affiliation;
import de.mpg.mpdl.r2d2.search.dao.AffiliationDaoEs;
import de.mpg.mpdl.r2d2.search.model.FileIto;
import de.mpg.mpdl.r2d2.search.model.SearchQuery;
import de.mpg.mpdl.r2d2.search.model.SearchResult;
import de.mpg.mpdl.r2d2.search.util.ElasticSearchIndexField;

@Repository
public class AffiliationDaoImpl extends ElasticSearchGenericDAOImpl<Affiliation> implements AffiliationDaoEs {

  private static final Class<Affiliation> typeParameterClass = Affiliation.class;

  private static final String[] SOURCE_EXCLUSIONS = new String[] {};

  @Autowired
  private Environment env;

  public AffiliationDaoImpl(Environment env) {
    super(env.getProperty("index.affiliation.name"), typeParameterClass);
  }

  @Override
  protected String[] getSourceExclusions() {
    return SOURCE_EXCLUSIONS;
  }

}
