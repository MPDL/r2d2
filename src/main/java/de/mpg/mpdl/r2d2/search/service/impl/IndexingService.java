package de.mpg.mpdl.r2d2.search.service.impl;

import java.util.UUID;

import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.r2d2.db.DatasetVersionRepository;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.VersionId;
import de.mpg.mpdl.r2d2.search.dao.DatasetVersionDaoEs;
import de.mpg.mpdl.r2d2.search.dao.FileDaoEs;
import de.mpg.mpdl.r2d2.search.model.DatasetVersionIto;
import de.mpg.mpdl.r2d2.service.impl.DatasetVersionServiceDbImpl;
import de.mpg.mpdl.r2d2.util.DtoMapper;

@Service
public class IndexingService {
  private static Logger LOGGER = LoggerFactory.getLogger(IndexingService.class);

  @Autowired
  @Qualifier("PublicDatasetVersionDaoImpl")
  private DatasetVersionDaoEs datasetVersionIndexDao;

  @Autowired
  private DatasetVersionRepository datasetVersionRepository;

  @Autowired
  private DtoMapper mapper;

  @Autowired
  private FileDaoEs fileIndexDao;

  public void reindexDataset(UUID datasetId, boolean immediate) throws R2d2TechnicalException {

    DatasetVersion latestVersion = datasetVersionRepository.findLatestVersion(datasetId);
    Dataset dataset = latestVersion.getDataset();

    //LOGGER.info("ModDate Version before reindex: " + latestVersion.getModificationDate());
    //LOGGER.info("ModDate Dataset before reindex: " + latestVersion.getDataset().getModificationDate());

    // Delete old version, if exists
    VersionId oldVersion = new VersionId(dataset.getId(), dataset.getLatestVersion() - 1);
    if (oldVersion != null) {
      datasetVersionIndexDao.deleteImmediatly(oldVersion.toString());
    }

    // Reindex latest version
    DatasetVersionIto latestdvIto = mapper.convertToDatasetVersionIto(latestVersion);
    if (immediate) {
      datasetVersionIndexDao.createImmediately(latestVersion.getVersionId().toString(), latestdvIto);
    } else {
      datasetVersionIndexDao.create(latestVersion.getVersionId().toString(), latestdvIto);
    }

    //LOGGER.info("ModDate Version after reindex: " + latestdvIto.getModificationDate());
    //LOGGER.info("ModDate Dataset after reindex: " + latestdvIto.getDataset().getModificationDate());

    // Reindex latest public version if exists and not equal to latest version
    if (dataset.getLatestPublicVersion() != null && dataset.getLatestPublicVersion() != latestVersion.getVersionNumber()) {
      DatasetVersion latestPublicVersion = datasetVersionRepository.findById(dataset.getLatestPublicVersionId()).get();
      DatasetVersionIto latestPublicDvIto = mapper.convertToDatasetVersionIto(latestPublicVersion);
      if (immediate) {
        datasetVersionIndexDao.createImmediately(latestPublicVersion.getVersionId().toString(), latestPublicDvIto);
      } else {
        datasetVersionIndexDao.create(latestPublicVersion.getVersionId().toString(), latestPublicDvIto);
      }
    }

  }


  public void reindexFile(File f, boolean immediate) throws R2d2TechnicalException {
    if (immediate) {
      fileIndexDao.createImmediately(f.getId().toString(), mapper.convertToFileIto(f));
    } else {
      fileIndexDao.create(f.getId().toString(), mapper.convertToFileIto(f));
    }

  }

  public void deleteDataset(UUID datasetId) throws R2d2TechnicalException {
    datasetVersionIndexDao.deleteByQuery(QueryBuilders.termQuery("id", datasetId.toString()));

  }

  public void deleteFile(UUID fileId, boolean immediate) throws R2d2TechnicalException {
    if (immediate) {
      fileIndexDao.deleteImmediatly(fileId.toString());
    } else {
      fileIndexDao.delete(fileId.toString());
    }

  }

}
