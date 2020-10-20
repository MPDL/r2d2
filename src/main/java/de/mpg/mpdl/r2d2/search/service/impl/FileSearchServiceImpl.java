package de.mpg.mpdl.r2d2.search.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.r2d2.search.dao.FileDaoEs;
import de.mpg.mpdl.r2d2.search.dao.GenericDaoEs;
import de.mpg.mpdl.r2d2.search.model.DatasetVersionIto;
import de.mpg.mpdl.r2d2.search.model.FileIto;
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

}
