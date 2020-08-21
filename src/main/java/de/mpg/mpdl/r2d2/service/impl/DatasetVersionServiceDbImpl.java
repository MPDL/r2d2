package de.mpg.mpdl.r2d2.service.impl;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.DatasetVersionMetadata;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.StagingFile;
import de.mpg.mpdl.r2d2.model.VersionId;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.search.dao.DatasetVersionDaoEs;
import de.mpg.mpdl.r2d2.search.dao.GenericDaoEs;
import de.mpg.mpdl.r2d2.service.DatasetVersionService;
import de.mpg.mpdl.r2d2.service.storage.SwiftObjectStoreRepository;
import de.mpg.mpdl.r2d2.service.util.FileDownloadWrapper;
import de.mpg.mpdl.r2d2.util.Utils;

@Service
public class DatasetVersionServiceDbImpl extends GenericServiceDbImpl<DatasetVersion> implements DatasetVersionService {

  private static Logger LOGGER = LoggerFactory.getLogger(DatasetVersionServiceDbImpl.class);

  @Autowired
  private DatasetVersionRepository datasetVersionRepository;

  @Autowired
  private DatasetRepository datasetRepository;

  @Autowired
  private FileRepository fileRepository;

  @Autowired
  private DatasetVersionDaoEs datasetVersionIndexDao;

  @Autowired
  private SwiftObjectStoreRepository objectStoreRepository;

  @PersistenceContext
  private EntityManager em;

  @Autowired
  private FileUploadService fileUploadService;

  public DatasetVersionServiceDbImpl() {
    super(DatasetVersion.class);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public DatasetVersion create(DatasetVersion datasetVersion, R2D2Principal principal)
      throws R2d2TechnicalException, ValidationException, AuthorizationException, InvalidStateException {

    DatasetVersion datasetVersionToCreate = buildDatasetVersionToCreate(datasetVersion, principal.getUserAccount(), 1, null);

    checkAa("create", principal, datasetVersionToCreate);
    // TODO validation

    datasetVersionToCreate.setFiles(handleFiles(datasetVersion, null, principal));

    try {
      datasetVersionToCreate = datasetVersionRepository.saveAndFlush(datasetVersionToCreate);
      datasetVersionToCreate.getDataset().setLatestVersion(datasetVersionToCreate.getVersionNumber());

    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }

    reindex(datasetVersionToCreate);

    return datasetVersionToCreate;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public DatasetVersion update(UUID id, DatasetVersion datasetVersion, R2D2Principal user, boolean metadata) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException {

    return update1(id, datasetVersion, user, metadata);
  }

  /*
   * @Override
   * 
   * @Transactional(rollbackFor = Throwable.class) public DatasetVersion
   * createNewVersion(UUID id, DatasetVersion datasetVersion, R2D2Principal user)
   * throws R2d2TechnicalException, OptimisticLockingException,
   * ValidationException, NotFoundException, InvalidStateException,
   * AuthorizationException { return update(id, datasetVersion, user, true); }
   */

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void delete(UUID id, OffsetDateTime lastModificationDate, R2D2Principal user)
      throws R2d2TechnicalException, OptimisticLockingException, NotFoundException, InvalidStateException, AuthorizationException {

    DatasetVersion datsetVersion = getLatest(id, user);
    checkEqualModificationDate(lastModificationDate, datsetVersion.getDataset().getModificationDate());
    // TODO Complete deletion if only one version
    // TODO check state

    // TODO delete from version list in dataset
    // TODO Authorization
    datasetRepository.deleteById(id);
    datasetVersionIndexDao.delete(id.toString());
    // TODO delete dataset object, not only version?

  }

  @Override
  // @Transactional(readOnly = true)
  public DatasetVersion getLatest(UUID id, R2D2Principal principal)
      throws R2d2TechnicalException, NotFoundException, AuthorizationException {

    DatasetVersion requestedVersion = null;
    DatasetVersion latestVersion = datasetVersionRepository.findLatestVersion(id);

    if (latestVersion == null) {
      throw new NotFoundException("Dataset with id " + id + " not found");
    }

    if (principal == null) {
      requestedVersion = datasetVersionRepository.findLatestPublicVersion(id);
    } else {

      try {
        checkAa("get", principal, latestVersion);
        requestedVersion = latestVersion;
      } catch (AuthorizationException e) {
        requestedVersion = datasetVersionRepository.findLatestPublicVersion(id);
      }
    }

    if (requestedVersion == null) {
      throw new AuthorizationException("No public version of dataset " + id + " is available");
    }

    return requestedVersion;

  }

  @Override
  // @Transactional(readOnly = true)
  public DatasetVersion get(VersionId id, R2D2Principal principal)
      throws R2d2TechnicalException, NotFoundException, AuthorizationException {

    DatasetVersion datasetVersion =
        datasetVersionRepository.findById(id).orElseThrow(() -> new NotFoundException("Dataset version with id " + id + " not found"));

    checkAa("get", principal, datasetVersion);

    return datasetVersion;

  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public DatasetVersion publish(UUID id, OffsetDateTime lastModificationDate, R2D2Principal user) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException {

    // DatasetVersion datasetVersionToBeUpdated = get(id, user);
    DatasetVersion latestVersion = datasetVersionRepository.findLatestVersion(id);

    checkAa("publish", user, latestVersion);
    checkEqualModificationDate(lastModificationDate, latestVersion.getDataset().getModificationDate());

    /*
     * if (!id.equals(latestVersion.getId())) { throw new
     * InvalidStateException("Only the latest dataset version can be published. Given version: "
     * + datasetVersionToBeUpdated.getVersionNumber() + "; Latest version: " +
     * latestVersion.getVersionNumber()); }
     */

    if (!State.PRIVATE.equals(latestVersion.getState())) {
      throw new InvalidStateException("Dataset in state " + latestVersion.getState() + "cannot be published.");
    }

    latestVersion.setState(State.PUBLIC);
    latestVersion.getDataset().setState(State.PUBLIC);
    setBasicModificationProperties(latestVersion, user.getUserAccount());
    latestVersion.getDataset().setLatestPublicVersion(latestVersion.getVersionNumber());

    try {
      latestVersion = datasetVersionRepository.saveAndFlush(latestVersion);
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }
    reindex(latestVersion);

    return latestVersion;

  }

  /*
   * @Override
   * 
   * @Transactional(rollbackFor = Throwable.class) public File initNewFile(UUID
   * datasetId, File file, R2D2Principal user) throws R2d2TechnicalException,
   * OptimisticLockingException, ValidationException, NotFoundException,
   * InvalidStateException, AuthorizationException { DatasetVersion dv =
   * getLatest(datasetId, user);
   * 
   * checkAa("upload", user, dv);
   * 
   * file.setId(null);
   * 
   * setBasicCreationProperties(file, user.getUserAccount());
   * 
   * File f = fileRepository.save(file); dv.getFiles().add(f);
   * datasetVersionRepository.save(dv);
   * 
   * objectStoreRepository.createContainer(file.getId().toString());
   * //datasetVersionIndexDao.updateImmediately(dv.getId().toString(), dv); return
   * f;
   * 
   * }
   * 
   * 
   * @Override
   * 
   * @Transactional(rollbackFor = Throwable.class) public File
   * uploadSingleFile(UUID datasetId, File file, InputStream fileStream,
   * R2D2Principal user) throws R2d2TechnicalException,
   * OptimisticLockingException, ValidationException, NotFoundException,
   * InvalidStateException, AuthorizationException { DatasetVersion dv =
   * getLatest(datasetId, user);
   * 
   * checkAa("upload", user, dv);
   * 
   * file.setId(null); setBasicCreationProperties(file, user.getUserAccount());
   * 
   * 
   * File f = fileRepository.save(file); dv.getFiles().add(f);
   * datasetVersionRepository.save(dv);
   * 
   * 
   * objectStoreRepository.createContainer(file.getId().toString()); String etag =
   * objectStoreRepository.uploadFile(file, fileStream);
   * 
   * f.setChecksum(etag); f.setState(UploadState.COMPLETE);
   * 
   * //TODO post-processing //TODO indexing //reindex(dv); return f;
   * 
   * }
   * 
   * 
   * 
   * @Override
   * 
   * @Transactional(rollbackFor = Throwable.class) public FileChunk
   * uploadFileChunk(UUID datasetId, UUID fileId, FileChunk chunk, InputStream
   * fileStream, R2D2Principal user) throws R2d2TechnicalException,
   * OptimisticLockingException, ValidationException, NotFoundException,
   * InvalidStateException, AuthorizationException { DatasetVersion dv =
   * getLatest(datasetId, user);
   * 
   * checkAa("upload", user, dv);
   * 
   * File file = fileRepository.findById(fileId).orElseThrow(() -> new
   * NotFoundException("File with id " + fileId + " not found")); //TODO Auth,
   * check latest version
   * 
   * String etag = objectStoreRepository.uploadChunk(file, chunk, fileStream);
   * 
   * chunk.setServerEtag(etag);
   * 
   * file.getStateInfo().getChunks().add(chunk);
   * 
   * 
   * //LastChunk if (file.getStateInfo().getExpectedNumberOfChunks() ==
   * file.getStateInfo().getChunks().size()) {
   * file.setState(UploadState.COMPLETE);
   * objectStoreRepository.createManifest(file);
   * 
   * //TODO post-processing async? //detect mime-type? //create thumbnail
   * depending on mime-type and save somewhere //read zip structure if zip file
   * and save somewhere? //get final checksum from server and save? }
   * 
   * file = fileRepository.save(file);
   * 
   * //datasetVersionIndexDao.createImmediately(dv.getId().toString(), dv); return
   * chunk;
   * 
   * }
   */

  public FileDownloadWrapper getFileContent(VersionId versionId, UUID fileId, R2D2Principal user) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException {
    DatasetVersion dv = get(versionId, user);
    File file = fileRepository.findById(fileId).orElseThrow(() -> new NotFoundException("File with id " + fileId + " not found"));
    checkAa("download", user, dv);

    FileDownloadWrapper fd = new FileDownloadWrapper(file, objectStoreRepository);

    return fd;

  }

  private DatasetVersion update1(UUID id, DatasetVersion datasetVersion, R2D2Principal user, boolean metadata)
      throws R2d2TechnicalException, OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException,
      AuthorizationException {

    DatasetVersion latestVersion = datasetVersionRepository.findLatestVersion(id);
    DatasetVersion datasetVersionToBeUpdated;

    checkAa("update", user, latestVersion);
    // TODO validation
    checkEqualModificationDate(datasetVersion.getModificationDate(), latestVersion.getDataset().getModificationDate());

    // create new versioin
    if (State.PUBLIC.equals(latestVersion.getState())) {
      /*
       * if (!State.PUBLIC.equals(datasetVersionToBeUpdated.getState())) { throw new
       * InvalidStateException("A new version can only be created if the state of the latest version is public."
       * ); }
       */

      // em.detach(datasetVersionToBeUpdated);
      // em.detach(latestVersion);
      datasetVersionToBeUpdated = buildDatasetVersionToCreate(datasetVersion, user.getUserAccount(), latestVersion.getVersionNumber() + 1,
          latestVersion.getDataset());
      if (metadata) {
        datasetVersionToBeUpdated.setFiles(latestVersion.getFiles());
      }
      setBasicCreationProperties(datasetVersionToBeUpdated, user.getUserAccount());
      datasetVersionToBeUpdated.getDataset().setLatestVersion(datasetVersionToBeUpdated.getVersionNumber());

    } else {
      datasetVersionToBeUpdated = latestVersion;
      datasetVersionToBeUpdated.setMetadata(datasetVersion.getMetadata());
      setBasicModificationProperties(datasetVersionToBeUpdated, user.getUserAccount());

    }

    if (!metadata) {
      datasetVersionToBeUpdated.setFiles(handleFiles(datasetVersion, latestVersion, user));
    }

    try {
      datasetVersionToBeUpdated = datasetVersionRepository.saveAndFlush(datasetVersionToBeUpdated);
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }

    reindex(datasetVersionToBeUpdated);

    return datasetVersionToBeUpdated;

  }

  private Set<File> handleFiles(DatasetVersion newDataset, DatasetVersion latestDataset, R2D2Principal principal)
      throws R2d2TechnicalException, ValidationException, InvalidStateException, AuthorizationException {

    Set<File> updatedFileList = new HashSet();

    Map<UUID, File> currentFiles = new HashMap<UUID, File>();
    if (latestDataset != null) {
      for (File file : latestDataset.getFiles()) {
        currentFiles.put(file.getId(), file);
      }
    }

    for (File file : newDataset.getFiles()) {

      File currentFile;

      if (file.getId() != null) {

        // Check if this file exists for the given item id
        String errorMessage = "File with id [" + file.getId()
            + "] does not exist or is part of another dataset. Please remove identifier to create as new file";

        List<UUID> datasets = datasetVersionRepository.findItemsForFile(file.getId());
        if (latestDataset == null || datasets == null || datasets.isEmpty() || !datasets.contains(latestDataset.getId())) {
          throw new InvalidStateException(errorMessage);
        }
        // Already existing file
        currentFile = fileRepository.findById(file.getId()).get();

      } else {

        // New file or locator
        currentFile = new File();

        if (file.getStorageLocation() == null || file.getStorageLocation().trim().isEmpty()) {
          throw new ValidationException("A file storage loation has to be provided containing the identifier of the staged file.");
        }

        // New real file

        try {
          StagingFile stagingfile = fileUploadService.get(UUID.fromString(file.getStorageLocation()), principal);

          setBasicCreationProperties(currentFile, principal.getUserAccount());
          currentFile.setId(stagingfile.getId());
          currentFile.setFilename(stagingfile.getFilename());
          currentFile.setChecksum(stagingfile.getChecksum());
          currentFile.setFormat(stagingfile.getFormat());
          currentFile.setSize(stagingfile.getSize());
          currentFile.setStorageLocation(stagingfile.getId().toString());
        } catch (NotFoundException e) {
          throw new ValidationException("File not found.", e);
        }
      }

      updatedFileList.add(currentFile);
    }

    // TODO
    // Delete files which are left in currentFiles Map if they are not part of an
    // released item

    return updatedFileList;
  }

  private DatasetVersion buildDatasetVersionToCreate(DatasetVersion givenDatasetVersion, UserAccount creator, int versionNumber,
      Dataset dataset) throws ValidationException {

    if (Objects.isNull(givenDatasetVersion.getMetadata().getTitle())) {
      throw new ValidationException("Title is required!");
    }
    DatasetVersion datasetVersionToCreate = new DatasetVersion();

    datasetVersionToCreate.setState(State.PRIVATE);
    datasetVersionToCreate.setMetadata(givenDatasetVersion.getMetadata());
    datasetVersionToCreate.setVersionNumber(versionNumber);

    Dataset datasetToCreate = dataset;
    if (datasetToCreate == null) {
      datasetToCreate = new Dataset();
      datasetToCreate.setState(State.PRIVATE);

    }
    datasetVersionToCreate.setDataset(datasetToCreate);
    setBasicCreationProperties(datasetVersionToCreate, creator);

    return datasetVersionToCreate;

  }

  @Override
  protected GenericDaoEs<DatasetVersion> getIndexDao() {
    return datasetVersionIndexDao;
  }

  protected void setBasicCreationProperties(DatasetVersion baseObject, UserAccount creator) {
    OffsetDateTime dateTime = Utils.generateCurrentDateTimeForDatabase();
    super.setBasicCreationProperties(baseObject, creator, dateTime);
    if (baseObject.getDataset().getId() == null) {
      super.setBasicCreationProperties(baseObject.getDataset(), creator, dateTime);
    } else {
      super.setBasicModificationProperties(baseObject.getDataset(), creator, dateTime);
    }

  }

  protected void setBasicModificationProperties(DatasetVersion baseObject, UserAccount creator) {
    OffsetDateTime dateTime = Utils.generateCurrentDateTimeForDatabase();
    super.setBasicModificationProperties(baseObject, creator, dateTime);
    super.setBasicModificationProperties(baseObject.getDataset(), creator, dateTime);

  }

  public void reindex(DatasetVersion dv) throws R2d2TechnicalException {
    this.reindex(dv.getDataset(), true);
  }

  private void reindex(Dataset dataset, boolean immediate) throws R2d2TechnicalException {

    // Delete old version, if exists
    VersionId oldVersion = new VersionId(dataset.getId(), dataset.getLatestVersion() - 1);
    if (oldVersion != null) {
      datasetVersionIndexDao.deleteImmediatly(oldVersion.toString());
    }

    // Reindex latest version
    DatasetVersion latestVersion = datasetVersionRepository.findById(dataset.getLatestVersionId()).get();
    if (immediate) {
      datasetVersionIndexDao.createImmediately(latestVersion.getVersionId().toString(), latestVersion);
    } else {
      datasetVersionIndexDao.create(latestVersion.getVersionId().toString(), latestVersion);
    }

    // Reindex latest public version if exists and not equal to latest version
    if (dataset.getLatestPublicVersion() != null && dataset.getLatestPublicVersion() != latestVersion.getVersionNumber()) {
      DatasetVersion latestPublicVersion = datasetVersionRepository.findById(dataset.getLatestPublicVersionId()).get();
      if (immediate) {
        datasetVersionIndexDao.createImmediately(latestPublicVersion.getVersionId().toString(), latestPublicVersion);
      } else {
        datasetVersionIndexDao.create(latestPublicVersion.getVersionId().toString(), latestPublicVersion);
      }
    }

  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public DatasetVersion addOrRemoveFile(UUID id, UUID fileId, OffsetDateTime lastModificationDate, R2D2Principal user, String action)
      throws R2d2TechnicalException, OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException,
      AuthorizationException {

    DatasetVersion latestVersion = datasetVersionRepository.findLatestVersion(id);
    DatasetVersion result;
    // File file;

    checkAa("update", user, latestVersion);
    // TODO validation
    checkEqualModificationDate(lastModificationDate, latestVersion.getDataset().getModificationDate());

    // create new versioin
    if (State.PUBLIC.equals(latestVersion.getState())) {
      result = buildDatasetVersionToCreate(latestVersion, user.getUserAccount(), latestVersion.getVersionNumber() + 1,
          latestVersion.getDataset());
      result.setFiles(latestVersion.getFiles());
      result.getDataset().setLatestVersion(result.getVersionNumber());
      setBasicCreationProperties(result, user.getUserAccount());
      result = datasetVersionRepository.save(result);

    } else {
      result = latestVersion;
    }

    switch (action) {
      case "add":
        StagingFile file2add = fileUploadService.get(fileId, user);
        File file = new File();
        file = stagingToFile(file2add, file, user);
        result.getFiles().add(file);
        break;
      case "remove":
        File file2remove = fileRepository.findById(fileId)
            .orElseThrow(() -> new NotFoundException(String.format("File with id %s NOT FOUND", fileId.toString())));
        result.getFiles().remove(file2remove);
        break;
      default:
        break;
    }
    try {
      result = datasetVersionRepository.saveAndFlush(result);
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }
    reindex(result);

    return result;
  }

  private File stagingToFile(StagingFile sf, File file, R2D2Principal user) {
    setBasicCreationProperties(file, user.getUserAccount());
    file.setId(sf.getId());
    file.setFilename(sf.getFilename());
    file.setChecksum(sf.getChecksum());
    file.setFormat(sf.getFormat());
    file.setSize(sf.getSize());
    file.setStorageLocation(sf.getId().toString());
    return file;
  }

}
