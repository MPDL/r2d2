package de.mpg.mpdl.r2d2.service.impl;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.r2d2.db.DatasetRepository;
import de.mpg.mpdl.r2d2.db.DatasetVersionRepository;
import de.mpg.mpdl.r2d2.db.FileRepository;
import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.InvalidStateException;
import de.mpg.mpdl.r2d2.exceptions.NotFoundException;
import de.mpg.mpdl.r2d2.exceptions.OptimisticLockingException;
import de.mpg.mpdl.r2d2.exceptions.R2d2ApplicationException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.exceptions.ValidationException;
import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.Dataset.State;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.File.UploadState;
import de.mpg.mpdl.r2d2.model.FileChunk;
import de.mpg.mpdl.r2d2.search.dao.DatasetVersionDaoEs;
import de.mpg.mpdl.r2d2.search.dao.GenericDaoEs;
import de.mpg.mpdl.r2d2.service.DatasetVersionService;
import de.mpg.mpdl.r2d2.service.storage.SwiftObjectStoreRepository;

@Service
public class DatasetVersionServiceDbImpl extends GenericServiceDbImpl<DatasetVersion> implements DatasetVersionService {

  private static Logger LOGGER = LoggerFactory.getLogger(DatasetVersionServiceDbImpl.class);

  @Autowired
  private DatasetVersionRepository datasetVersionRepository;

  @Autowired
  private FileRepository fileRepository;

  @Autowired
  private DatasetVersionDaoEs datasetVersionIndexDao;

  @Autowired
  private SwiftObjectStoreRepository objectStoreRepository;

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public DatasetVersion create(DatasetVersion datasetVersion, R2D2Principal principal)
      throws R2d2TechnicalException, ValidationException, AuthorizationException {

    // TODO authorization?

    DatasetVersion datasetVersionToCreate = buildDatasetVersionToCreate(datasetVersion, principal.getUserAccount(), 1, null);

    checkAa("create", principal, datasetVersionToCreate);
    // TODO validation

    try {
      datasetVersionToCreate = datasetVersionRepository.save(datasetVersionToCreate);
      datasetVersionIndexDao.createImmediately(datasetVersionToCreate.getId().toString(), datasetVersionToCreate);
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }

    return datasetVersionToCreate;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public DatasetVersion update(UUID id, DatasetVersion datasetVersion, R2D2Principal user) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException {

    return update(id, datasetVersion, user, false);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public DatasetVersion createNewVersion(UUID id, DatasetVersion datasetVersion, R2D2Principal user) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException {
    return update(id, datasetVersion, user, true);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void delete(UUID id, OffsetDateTime lastModificationDate, R2D2Principal user)
      throws R2d2TechnicalException, OptimisticLockingException, NotFoundException, InvalidStateException, AuthorizationException {

    DatasetVersion datsetVersion = get(id, user);
    checkEqualModificationDate(lastModificationDate, datsetVersion.getDataset().getModificationDate());
    //TODO Complete deletion if only one version
    //TODO check state

    //TODO Authorization
    datasetVersionRepository.deleteById(id);
    datasetVersionIndexDao.delete(id.toString());
    // TODO Auto-generated method stub

  }

  @Override
  @Transactional(readOnly = true)
  public DatasetVersion get(UUID id, R2D2Principal principal) throws R2d2TechnicalException, NotFoundException, AuthorizationException {


    DatasetVersion datasetVersion =
        datasetVersionRepository.findById(id).orElseThrow(() -> new NotFoundException("Dataset version with id " + id + " not found"));
    checkAa("get", principal, datasetVersion);

    return datasetVersion;

  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void publish(UUID id, OffsetDateTime lastModificationDate, R2D2Principal user)
      throws R2d2TechnicalException, OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException {
    // TODO Auto-generated method stub

  }



  @Override
  @Transactional(rollbackFor = Throwable.class)
  public File initNewFile(UUID datasetId, File file, R2D2Principal user) throws R2d2TechnicalException, OptimisticLockingException,
      ValidationException, NotFoundException, InvalidStateException, AuthorizationException {
    DatasetVersion dv = get(datasetId, user);

    //TODO Auth, check latest version

    file.setId(null);
    file.setCreator(user.getUserAccount());
    file.setModifier(user.getUserAccount());


    File f = fileRepository.save(file);
    dv.getFiles().add(f);
    datasetVersionRepository.save(dv);

    objectStoreRepository.createContainer(file.getId().toString());
    //datasetVersionIndexDao.updateImmediately(dv.getId().toString(), dv);
    return f;

  }


  @Override
  @Transactional(rollbackFor = Throwable.class)
  public FileChunk uploadFileChunk(UUID datasetId, UUID fileId, FileChunk chunk, InputStream fileStream, R2D2Principal user)
      throws R2d2TechnicalException, OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException,
      AuthorizationException {
    DatasetVersion dv = get(datasetId, user);

    File file = fileRepository.findById(fileId).orElseThrow(() -> new NotFoundException("File with id " + fileId + " not found"));
    //TODO Auth, check latest version

    String etag = objectStoreRepository.uploadChunk(file.getId().toString(), chunk, fileStream);

    chunk.setServerEtag(etag);

    file.getStateInfo().getChunks().add(chunk);


    if (file.getStateInfo().getExpectedNumberOfChunks() == file.getStateInfo().getChunks().size()) {
      file.setState(UploadState.COMPLETE);
      objectStoreRepository.createManifest(file);
    }

    file = fileRepository.save(file);

    //datasetVersionIndexDao.updateImmediately(dv.getId().toString(), dv);
    return chunk;

  }


  public InputStream getFileContent(UUID datasetId, UUID fileId, R2D2Principal user) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException {
    DatasetVersion dv = get(datasetId, user);
    File file = fileRepository.findById(fileId).orElseThrow(() -> new NotFoundException("File with id " + fileId + " not found"));
    //TODO Auth

    return objectStoreRepository.downloadFile(fileId.toString(), "content");


  }

  private DatasetVersion update(UUID id, DatasetVersion datasetVersion, R2D2Principal user, boolean createNewVersion)
      throws R2d2TechnicalException, OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException,
      AuthorizationException {

    DatasetVersion datasetVersionToBeUpdated = get(id, user);
    DatasetVersion latestVersion = datasetVersionRepository.getLatestVersion(datasetVersionToBeUpdated.getDataset().getId());

    if (!datasetVersionToBeUpdated.getId().equals(latestVersion.getId())) {
      throw new InvalidStateException("Only the latest dataset version can be updated. Given version: "
          + datasetVersionToBeUpdated.getVersionNumber() + "; Latest version: " + latestVersion.getVersionNumber());
    }

    // TODO authorization
    // TODO validation
    checkEqualModificationDate(datasetVersion.getDataset().getModificationDate(),
        datasetVersionToBeUpdated.getDataset().getModificationDate());

    if (createNewVersion) {
      if (!State.PUBLIC.equals(datasetVersionToBeUpdated.getState())) {
        throw new InvalidStateException("A new version can only be created if the state of the latest version is public.");
      }

      datasetVersionToBeUpdated = buildDatasetVersionToCreate(datasetVersion, user.getUserAccount(),
          datasetVersionToBeUpdated.getVersionNumber() + 1, datasetVersionToBeUpdated.getDataset());
    } else {
      datasetVersionToBeUpdated = updateDatasetVersion(datasetVersion, datasetVersionToBeUpdated, user.getUserAccount());
    }

    try {
      datasetVersionToBeUpdated = datasetVersionRepository.save(datasetVersionToBeUpdated);
      datasetVersionIndexDao.updateImmediately(datasetVersionToBeUpdated.getId().toString(), datasetVersionToBeUpdated);
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }

    return datasetVersionToBeUpdated;
  }

  private DatasetVersion updateDatasetVersion(DatasetVersion givenDatasetVersion, DatasetVersion latestVersion, UserAccount modifier) {

    latestVersion.setModifier(modifier);
    latestVersion.setMetadata(givenDatasetVersion.getMetadata());

    latestVersion.getDataset().setModifier(modifier);

    return latestVersion;
  }

  private DatasetVersion buildDatasetVersionToCreate(DatasetVersion givenDatasetVersion, UserAccount creator, int versionNumber,
      Dataset dataset) {

    DatasetVersion datasetVersionToCreate = new DatasetVersion();
    datasetVersionToCreate.setState(State.PRIVATE);
    datasetVersionToCreate.setMetadata(givenDatasetVersion.getMetadata());
    datasetVersionToCreate.setCreator(creator);
    datasetVersionToCreate.setModifier(creator);
    datasetVersionToCreate.setVersionNumber(versionNumber);

    Dataset datasetToCreate = dataset;
    if (datasetToCreate == null) {
      datasetToCreate = new Dataset();
      datasetToCreate.setState(State.PRIVATE);
      datasetToCreate.setCreator(creator);
    }

    datasetToCreate.setModifier(creator);
    datasetVersionToCreate.setDataset(datasetToCreate);

    return datasetVersionToCreate;

  }

  @Override
  protected GenericDaoEs<DatasetVersion> getIndexDao() {
    return datasetVersionIndexDao;
  }

}
