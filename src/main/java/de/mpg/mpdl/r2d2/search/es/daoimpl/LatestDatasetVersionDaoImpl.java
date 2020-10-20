package de.mpg.mpdl.r2d2.search.es.daoimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.search.dao.DatasetVersionDaoEs;
import de.mpg.mpdl.r2d2.search.model.DatasetVersionIto;

@Repository("LatestDatasetVersionDaoImpl")
public class LatestDatasetVersionDaoImpl extends DatasetVersionDaoImpl implements DatasetVersionDaoEs {



  public LatestDatasetVersionDaoImpl(@Autowired Environment env) {
    super(env.getProperty("index.dataset.latest.name"));
    //super(env.getProperty("index.dataset.name"), typeParameterClass);
  }



}
