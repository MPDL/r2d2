package de.mpg.mpdl.r2d2.search.service.impl;

import de.mpg.mpdl.r2d2.search.dao.DatasetVersionDaoEs;
import de.mpg.mpdl.r2d2.search.dao.GenericDaoEs;
import de.mpg.mpdl.r2d2.search.model.DatasetVersionIto;
import de.mpg.mpdl.r2d2.search.service.DatasetSearchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
  protected boolean removeDatasetDuplicates() {
    return true;
  }

}
