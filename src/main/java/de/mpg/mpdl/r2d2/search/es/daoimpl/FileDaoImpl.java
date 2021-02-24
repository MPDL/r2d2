package de.mpg.mpdl.r2d2.search.es.daoimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.search.dao.FileDaoEs;
import de.mpg.mpdl.r2d2.search.model.FileIto;

@Repository
public class FileDaoImpl extends ElasticSearchGenericDAOImpl<FileIto> implements FileDaoEs {


  public static final String INDEX_FILE_ID = "id";
  public static final String INDEX_STATE = "state";
  public static final String INDEX_FILE_CREATOR_ID = "creator";
  public static final String INDEX_FILE_DATASET_CREATOR_ID = "internal.creator";
  public static final String INDEX_FILE_DATASET_ID = "internal.id";

  private static final Class<FileIto> typeParameterClass = FileIto.class;

  private static final String[] SOURCE_EXCLUSIONS = new String[] {"internal"};


  @Autowired
  private Environment env;

  public FileDaoImpl(Environment env) {
    super(env.getProperty("index.file.name"), typeParameterClass);
  }

  @Override
  protected String[] getSourceExclusions() {
    return SOURCE_EXCLUSIONS;
  }

}
