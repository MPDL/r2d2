package de.mpg.mpdl.r2d2.search.es.daoimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.search.dao.StagingFileDaoEs;

@Repository
public class StagingFileDaoImpl extends ElasticSearchGenericDAOImpl<File> implements StagingFileDaoEs {

  private static final Class<File> typeParameterClass = File.class;

  private static final String[] SOURCE_EXCLUSIONS = new String[] {};

  @Autowired
  private Environment env;

  public StagingFileDaoImpl(Environment env) {
    super(env.getProperty("index.staging.name"), typeParameterClass);
  }

  @Override
  protected String[] getSourceExclusions() {
    return SOURCE_EXCLUSIONS;
  }

}
