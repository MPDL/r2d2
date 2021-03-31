package de.mpg.mpdl.r2d2.service.impl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import de.mpg.mpdl.r2d2.model.*;
import de.mpg.mpdl.r2d2.service.doi.DoiRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.r2d2.db.AuditRepository;
import de.mpg.mpdl.r2d2.db.DatasetRepository;
import de.mpg.mpdl.r2d2.db.DatasetVersionRepository;
import de.mpg.mpdl.r2d2.db.FileRepository;
import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.InvalidStateException;
import de.mpg.mpdl.r2d2.exceptions.NotFoundException;
import de.mpg.mpdl.r2d2.exceptions.OptimisticLockingException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.exceptions.ValidationException;
import de.mpg.mpdl.r2d2.model.Audit.Action;
import de.mpg.mpdl.r2d2.model.Dataset.State;
import de.mpg.mpdl.r2d2.model.File.UploadState;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.model.validation.PublishConstraintGroup;
import de.mpg.mpdl.r2d2.model.validation.SaveConstraintGroup;
import de.mpg.mpdl.r2d2.search.service.impl.IndexingService;
import de.mpg.mpdl.r2d2.service.DatasetVersionService;
import de.mpg.mpdl.r2d2.service.storage.SwiftObjectStoreRepository;
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
  private AuditRepository auditRepository;

  @Autowired
  private SwiftObjectStoreRepository objectStoreRepository;

  @PersistenceContext
  private EntityManager em;

  @Autowired
  private FileUploadService fileUploadService;

  @Autowired
  private IndexingService indexingService;

  @Autowired
  private Validator beanValidator;

  //TODO: Use DataciteDoiRepositoryImpl in prod (instead of DummyDoiRepositoryImpl)
  @Autowired
  @Qualifier("DummyDoiRepositoryImpl")
  private DoiRepository doiRepository;

  public DatasetVersionServiceDbImpl() {
    super(DatasetVersion.class);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public DatasetVersion create(DatasetVersion datasetVersion, R2D2Principal user)
      throws R2d2TechnicalException, ValidationException, AuthorizationException, InvalidStateException {

    DatasetVersion datasetVersionToCreate = buildDatasetVersionToCreate(datasetVersion, user.getUserAccount(), 1, null);

    checkAa("create", user, datasetVersionToCreate);

    validate(datasetVersionToCreate, SaveConstraintGroup.class);

    // setBasicCreationProperties(datasetVersionToCreate, user.getUserAccount());
    // TODO validation

    // datasetVersionToCreate.setFiles(handleFiles(datasetVersion, null, principal));

    //LOGGER.info("ModDate Version before DB save: " + datasetVersionToCreate.getModificationDate());
    //LOGGER.info("ModDate Dataset before DB save: " + datasetVersionToCreate.getDataset().getModificationDate());


    generateAndSetDraftDoi(datasetVersionToCreate);

    try {
      datasetVersionToCreate = datasetVersionRepository.saveAndFlush(datasetVersionToCreate);
      datasetVersionToCreate.getDataset().setLatestVersion(datasetVersionToCreate.getVersionNumber());

    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }

    //LOGGER.info("ModDate Version after DB save: " + datasetVersionToCreate.getModificationDate());
    //LOGGER.info("ModDate Dataset after DB save: " + datasetVersionToCreate.getDataset().getModificationDate());


    auditRepository.save(new Audit(Action.CREATE, datasetVersionToCreate.getVersionId(), user.getUserAccount()));
    indexingService.reindexDataset(datasetVersionToCreate.getId(), true);


    return datasetVersionToCreate;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public DatasetVersion update(UUID id, DatasetVersion datasetVersion, R2D2Principal user) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException {

    DatasetVersion latestVersion = datasetVersionRepository.findLatestVersion(id);
    DatasetVersion datasetVersionToBeUpdated;

    checkAa("update", user, latestVersion);
    // TODO validation



    checkEqualModificationDate(datasetVersion.getModificationDate(), latestVersion.getModificationDate());


    datasetVersionToBeUpdated = createNewVersion(latestVersion, user);
    datasetVersionToBeUpdated.setMetadata(datasetVersion.getMetadata());
    validate(datasetVersion, SaveConstraintGroup.class);

    if (State.PUBLIC.equals(latestVersion.getState())) {
      //FIXME: New created Draft Doi overrides doi, independent on whether the doi was updated or not.
      generateAndSetDraftDoi(datasetVersionToBeUpdated);
    }

    try {
      datasetVersionToBeUpdated = datasetVersionRepository.saveAndFlush(datasetVersionToBeUpdated);
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }

    auditRepository.save(new Audit(Action.UPDATE, datasetVersionToBeUpdated.getVersionId(), user.getUserAccount()));
    indexingService.reindexDataset(datasetVersionToBeUpdated.getId(), true);

    return datasetVersionToBeUpdated;
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

    DatasetVersion latestVersion = getLatest(id, user);
    checkEqualModificationDate(lastModificationDate, latestVersion.getModificationDate());

    checkAa("delete", user, latestVersion);
    // TODO Complete deletion if only one version
    // TODO check state

    datasetRepository.deleteById(id);
    auditRepository.save(new Audit(Action.DELETE, latestVersion.getVersionId(), user.getUserAccount()));
    indexingService.deleteDataset(id);

    //TODO change file relations and states

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

    DatasetVersion dvToReturn = null;
    if (id.getVersionNumber() == null) {
      dvToReturn = getLatest(id.getId(), principal);

    } else {
      DatasetVersion datasetVersion =
          datasetVersionRepository.findById(id).orElseThrow(() -> new NotFoundException("Dataset version with id " + id + " not found"));
      checkAa("get", principal, datasetVersion);
      dvToReturn = datasetVersion;
    }

    LOGGER.info("ModDate Version after get: " + dvToReturn.getModificationDate());
    LOGGER.info("ModDate Dataset after get: " + dvToReturn.getDataset().getModificationDate());
    return dvToReturn;

  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public DatasetVersion publish(UUID id, OffsetDateTime lastModificationDate, R2D2Principal user) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException {

    // DatasetVersion datasetVersionToBeUpdated = get(id, user);
    DatasetVersion latestVersion = datasetVersionRepository.findLatestVersion(id);

    checkAa("publish", user, latestVersion);
    checkEqualModificationDate(lastModificationDate, latestVersion.getModificationDate());

    if (!State.PRIVATE.equals(latestVersion.getState())) {
      throw new InvalidStateException("Dataset in state " + latestVersion.getState() + "cannot be published.");
    }

    latestVersion.setState(State.PUBLIC);
    latestVersion.getDataset().setState(State.PUBLIC);
    latestVersion.getDataset().setLatestPublicVersion(latestVersion.getVersionNumber());
    latestVersion.setPublicationDate(OffsetDateTime.now());

    setBasicModificationProperties(latestVersion, user.getUserAccount());

    validate(latestVersion, SaveConstraintGroup.class, PublishConstraintGroup.class);

    generateFindableDoi(latestVersion);

    //set all files to Public
    List<File> filesOfDatasetVersion = fileRepository.findAllForVersion(latestVersion.getVersionId());
    for (File f : filesOfDatasetVersion) {
      if (!UploadState.PUBLIC.equals(f.getState())) {
        f.setState(UploadState.PUBLIC);
        indexingService.reindexFile(f, true);

      }

    }

    try {
      latestVersion = datasetVersionRepository.saveAndFlush(latestVersion);
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }
    auditRepository.save(new Audit(Action.PUBLISH, latestVersion.getVersionId(), user.getUserAccount()));
    indexingService.reindexDataset(latestVersion.getId(), true);

    return latestVersion;

  }


  public DatasetVersion withdraw(UUID id, OffsetDateTime lastModificationDate, String comment, R2D2Principal user)
      throws R2d2TechnicalException, OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException,
      AuthorizationException {


    DatasetVersion latestVersion = datasetVersionRepository.findLatestVersion(id);

    checkAa("withdraw", user, latestVersion);
    checkEqualModificationDate(lastModificationDate, latestVersion.getModificationDate());

    if (!State.PUBLIC.equals(latestVersion.getDataset().getState())) {
      throw new InvalidStateException("Dataset in state " + latestVersion.getState() + "cannot be withdrawn.");
    }

    latestVersion.getDataset().setState(State.WITHDRAWN);
    latestVersion.getDataset().setWithdrawComment(comment);

    setBasicModificationProperties(latestVersion, user.getUserAccount());

    for (DatasetVersion dv : datasetVersionRepository.findAllByDatasetId(latestVersion.getId())) {
      dv.setState(State.WITHDRAWN);
    }

    //set all files to Attached 
    //TODO use ATTACHED here or another state for withdrawn or delete files?
    List<File> filesOfDataset = fileRepository.findAllForDataset(latestVersion.getId());
    for (File f : filesOfDataset) {
      f.setState(UploadState.ATTACHED);
      indexingService.reindexFile(f, true);
    }

    try {
      latestVersion = datasetVersionRepository.saveAndFlush(latestVersion);
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }
    auditRepository.save(new Audit(Action.WITHDRAW, latestVersion.getVersionId(), user.getUserAccount()));
    indexingService.reindexDataset(latestVersion.getId(), true);

    return latestVersion;


  }


  public Page<File> listFiles(VersionId datasetId, Pageable pageable, R2D2Principal user)
      throws AuthorizationException, R2d2TechnicalException, NotFoundException {
    //AA via get



    DatasetVersion dv = get(datasetId, user);
    Page<File> list = fileRepository.findAllForVersion(dv.getVersionId(), pageable);
    return list;
  }

  public File getFileForDataset(VersionId datasetId, UUID fileId, R2D2Principal user)
      throws AuthorizationException, R2d2TechnicalException, NotFoundException {
    //AA via get
    DatasetVersion dv = get(datasetId, user);
    File f = fileRepository.findById(fileId).orElseThrow(() -> new NotFoundException("File with id " + fileId + " not found"));

    if (!f.getDatasets().stream().anyMatch(i -> i.getVersionId().equals(dv.getVersionId()))) {
      throw new NotFoundException("File with id " + fileId + " not part of dataset " + dv.getVersionId());
    }
    return f;
  }


  /*
  public FileDownloadWrapper getFileContent(VersionId versionId, UUID fileId, R2D2Principal user) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException {
    DatasetVersion dv = get(versionId, user);
    File file = fileRepository.findById(fileId).orElseThrow(() -> new NotFoundException("File with id " + fileId + " not found"));
    checkAa("download", user, dv);
  
    FileDownloadWrapper fd = new FileDownloadWrapper(file, objectStoreRepository);
  
    return fd;
  
  }
  */


  private void attachFiles(DatasetVersion nextVersion, DatasetVersion currentVersion) {

    Page<File> existingFiles = fileRepository.findAllForVersion(currentVersion.getVersionId(), PageRequest.of(0, 25));
    if (!existingFiles.isEmpty()) {
      existingFiles.forEach(file -> {
        try {
          file.getDatasets().add(nextVersion);
          fileRepository.saveAndFlush(file);
          indexingService.reindexFile(file, true);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      });
    }

  }

  /*
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
          File stagingfile = fileUploadService.get(UUID.fromString(file.getStorageLocation()), principal);
   
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
   */

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



  @Override
  @Transactional(rollbackFor = Throwable.class)
  public File addFile(UUID id, UUID fileId, OffsetDateTime lastModificationDate, R2D2Principal user) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException {

    List<UUID> filesToAdd = new ArrayList<UUID>();
    filesToAdd.add(fileId);
    return addOrRemoveFile(id, filesToAdd, Collections.emptyList(), lastModificationDate, user).get(0);

  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public File removeFile(UUID id, UUID fileId, OffsetDateTime lastModificationDate, R2D2Principal user) throws R2d2TechnicalException,
      OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException, AuthorizationException {

    List<UUID> filesToRemove = new ArrayList<UUID>();
    filesToRemove.add(fileId);
    return addOrRemoveFile(id, Collections.emptyList(), filesToRemove, lastModificationDate, user).get(0);

  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public List<File> updateFiles(UUID id, List<UUID> fileIds, OffsetDateTime lastModificationDate, R2D2Principal user)
      throws R2d2TechnicalException, OptimisticLockingException, ValidationException, NotFoundException, InvalidStateException,
      AuthorizationException {
    DatasetVersion latestVersion = datasetVersionRepository.findLatestVersion(id);

    em.detach(latestVersion);

    List<UUID> currentFileIds = fileRepository.findAllIdsForVersion(latestVersion.getVersionId());

    List<UUID> filesToRemove = new ArrayList<UUID>(currentFileIds);
    List<UUID> filesToAdd = new ArrayList<UUID>();

    for (UUID fileId : fileIds) {
      //File is already there. Remove from remove-list
      if (currentFileIds.contains(fileId)) {
        filesToRemove.remove(fileId);
      } else {
        //File is not there yet. Add to add-list
        filesToAdd.add(fileId);
      }
    }



    return addOrRemoveFile(id, filesToAdd, filesToRemove, lastModificationDate, user);

  }

  //TODO: Refactor/Merge this method with the create() and buildDatasetVersionToCreate() methods to avoid duplicate code!?
  //Refactor/Remove the else case which does NOT create a new DatasetVersion.
  private DatasetVersion createNewVersion(DatasetVersion latestVersion, R2D2Principal user) throws ValidationException {

    // create new version
    if (State.PUBLIC.equals(latestVersion.getState())) {
      LOGGER.info("Creating new version of dataset " + latestVersion.getVersionId());
      DatasetVersion resultedDataset = buildDatasetVersionToCreate(latestVersion, user.getUserAccount(),
          latestVersion.getVersionNumber() + 1, latestVersion.getDataset());
      attachFiles(resultedDataset, latestVersion);
      resultedDataset.getDataset().setLatestVersion(resultedDataset.getVersionNumber());
      // setBasicCreationProperties(result, user.getUserAccount());
      setBasicCreationProperties(resultedDataset, user.getUserAccount());
      resultedDataset = datasetVersionRepository.save(resultedDataset);
      auditRepository.save(new Audit(Action.CREATE, resultedDataset.getVersionId(), user.getUserAccount()));
      return resultedDataset;

    } else {
      setBasicModificationProperties(latestVersion, user.getUserAccount());
      return latestVersion;
    }
  }


  private List<File> addOrRemoveFile(UUID id, List<UUID> fileIdsToAdd, List<UUID> fileIdsToRemove, OffsetDateTime lastModificationDate,
      R2D2Principal user) throws R2d2TechnicalException, OptimisticLockingException, ValidationException, NotFoundException,
      InvalidStateException, AuthorizationException {

    DatasetVersion latestVersion = datasetVersionRepository.findLatestVersion(id);

    DatasetVersion resultedDataset = null;

    checkAa("update", user, latestVersion);
    //AA of file via FileService.getFile(), see below
    //TODO Auditing files

    checkEqualModificationDate(lastModificationDate, latestVersion.getModificationDate());

    // create new version if necessary
    resultedDataset = createNewVersion(latestVersion, user);
    if (State.PUBLIC.equals(latestVersion.getState())) {
      generateAndSetDraftDoi(resultedDataset);
    }

    List<File> processedFiles = new ArrayList<File>();

    for (UUID fileIdToAdd : fileIdsToAdd) {
      LOGGER.info("Trying to add file with id " + fileIdToAdd + " to dataset version " + resultedDataset.getVersionId());
      File file = fileUploadService.get(fileIdToAdd, user);
      processedFiles.add(file);
      setBasicModificationProperties(file, user.getUserAccount());
      DatasetVersion readableDataset = resultedDataset;
      if (!file.getDatasets().stream().anyMatch(i -> i.getVersionId().equals(readableDataset.getVersionId()))) {
        switch (file.getState()) {
          case COMPLETE: {
            file.setState(UploadState.ATTACHED);
            file.getDatasets().add(resultedDataset);
            indexingService.reindexFile(file, true);
            break;
          }
          case PUBLIC: {
            file.getDatasets().add(resultedDataset);
            indexingService.reindexFile(file, true);
            break;
          }
          default: {
            throw new ValidationException("Cannot add file. Invalid state " + file.getState() + " of file with id " + fileIdToAdd);
          }
        }
      } else {
        LOGGER.warn("File with id " + fileIdToAdd + " already in dataset version " + resultedDataset.getVersionId());
      }
    }

    for (UUID fileIdToRemove : fileIdsToRemove) {
      LOGGER.info("Trying to remove file with id " + fileIdToRemove + " from dataset version " + resultedDataset.getVersionId());
      File file = fileUploadService.get(fileIdToRemove, user);
      processedFiles.add(file);
      setBasicModificationProperties(file, user.getUserAccount());
      DatasetVersion readableDataset = resultedDataset;
      if (file.getDatasets().stream().anyMatch(i -> i.getVersionId().equals(readableDataset.getVersionId()))) {
        switch (file.getState()) {
          case ATTACHED: {
            file.setState(UploadState.COMPLETE);
            file.getDatasets().remove(resultedDataset);
            indexingService.reindexFile(file, true);
            break;
          }
          case PUBLIC: {
            file.getDatasets().remove(resultedDataset);
            if (file.getDatasets().isEmpty()) {
              file.setState(UploadState.COMPLETE);
            }
            indexingService.reindexFile(file, true);
            break;
          }
          default: {
            throw new ValidationException("Cannot remove file. Invalid state " + file.getState() + " of file with id " + fileIdToRemove);
          }
        }
      } else {
        LOGGER.warn(
            "Ignoring removal of file. File with id " + fileIdToRemove + " not found in dataset version " + resultedDataset.getVersionId());
      }
    }

    try {
      resultedDataset = datasetVersionRepository.saveAndFlush(resultedDataset);
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }

    indexingService.reindexDataset(resultedDataset.getId(), true);

    return processedFiles;
  }


  private void validate(DatasetVersion dv, Class<?>... validationGroups) throws ValidationException {
    Set<ConstraintViolation<DatasetVersion>> validationReports = beanValidator.validate(dv, validationGroups);

    if (!validationReports.isEmpty()) {
      StringBuilder sb = new StringBuilder();
      for (ConstraintViolation<DatasetVersion> cv : validationReports) {
        sb.append(cv.getPropertyPath().toString() + ": " + cv.getMessage() + "\n");
      }
      ValidationException ve = new ValidationException("Dataset is not valid.\n" + sb.toString());
      throw ve;
    }

  }

  //TODO: Implement Doi workflow for cases: delete & withdraw

  //TODO: Add the doi creation into createNewVersion() or buildDatasetVersionToCreate(), instead of calling this method in service methods.
  private void generateAndSetDraftDoi(DatasetVersion datasetVersion) {
    try {
      String doi = doiRepository.createDraftDoi(datasetVersion);
      datasetVersion.getMetadata().setDoi(doi);
    } catch (R2d2TechnicalException e) {
      LOGGER.error("Draft Doi could not be created.", e);
      //TODO: Implement workflow if Doi creation fails
    }
  }

  private void generateFindableDoi(DatasetVersion datasetVersion) {
    try {
      doiRepository.updateToFindableDoi(datasetVersion);
    } catch (R2d2TechnicalException e) {
      LOGGER.error("Draft Doi could not be published.", e);
      //TODO: Implement workflow if Doi publication fails
    }
  }

}
