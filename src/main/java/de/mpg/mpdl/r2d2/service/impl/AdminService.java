package de.mpg.mpdl.r2d2.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.elasticsearch.index.query.QueryBuilders;
import org.jclouds.openstack.swift.v1.domain.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.r2d2.db.DatasetRepository;
import de.mpg.mpdl.r2d2.db.DatasetVersionRepository;
import de.mpg.mpdl.r2d2.db.FileRepository;
import de.mpg.mpdl.r2d2.db.LocalUserAccountRepository;
import de.mpg.mpdl.r2d2.db.UserAccountRepository;
import de.mpg.mpdl.r2d2.exceptions.NotFoundException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.VersionId;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.search.dao.DatasetVersionDaoEs;
import de.mpg.mpdl.r2d2.search.dao.FileDaoEs;
import de.mpg.mpdl.r2d2.service.storage.ObjectStoreRepository;
import de.mpg.mpdl.r2d2.service.storage.S3ObjectStoreAwsSdkRepository;
import de.mpg.mpdl.r2d2.service.storage.SwiftObjectStoreRepository;

@Service
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminService {

  private static Logger LOGGER = LoggerFactory.getLogger(AdminService.class);

  @Autowired
  UserAccountRepository users;

  @Autowired
  LocalUserAccountRepository localUsers;

  @Autowired
  DatasetRepository datasets;

  @Autowired
  DatasetVersionRepository versions;

  @Autowired
  @Qualifier("PublicDatasetVersionDaoImpl")
  DatasetVersionDaoEs datasetVersionDaoEs;

  @Autowired
  FileDaoEs fileDaoEs;

  @Autowired
  FileRepository files;

  @Autowired
  ObjectStoreRepository objectStore;

  public String test() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication.getName() + authentication.getAuthorities();
  }

  public List<UserAccount> listAllUsers() {
    List<UserAccount> userList = new ArrayList<>();
    users.findAll().iterator().forEachRemaining(userList::add);
    return userList;
  }

  public UserAccount listUserById(String id) throws NotFoundException {
    return users.findById(UUID.fromString(id)).orElseThrow(() -> new NotFoundException(String.format("User with id %s NOT FOUND", id)));
  }

  public UserAccount updateUser(UserAccount user2update) {
    return users.save(user2update);
  }

  @Transactional
  public void deleteUser(String id) throws NotFoundException {
    UserAccount user =
        users.findById(UUID.fromString(id)).orElseThrow(() -> new NotFoundException(String.format("user with id %s NOT found!", id)));
    localUsers.deleteByUser(user);
    users.deleteById(UUID.fromString(id));
  }

  public List<DatasetVersion> listAllDatasets() {
    List<DatasetVersion> datasetList = new ArrayList<>();
    versions.findAll().iterator().forEachRemaining(datasetList::add);
    return datasetList;
  }

  public DatasetVersion listDatasetById(VersionId id) throws NotFoundException {
    return versions.findById(id).orElseThrow(() -> new NotFoundException(String.format("Dataset with id %s NOT FOUND", id)));
  }

  // @Transactional(rollbackFor = Throwable.class)
  public long deleteDataset(UUID id) throws NotFoundException, R2d2TechnicalException {
    Dataset dataset = datasets.findById(id).orElseThrow(() -> new NotFoundException(String.format("Dataset with id %s NOT FOUND", id)));

    List<DatasetVersion> versionList = datasets.listAllVersions(id);
    Set<File> fileSet = new HashSet<>();
    Pageable page = PageRequest.of(0, 25);
    versionList.forEach(v -> {
      files.findAllForVersion(v.getVersionId(), page).forEach(f -> fileSet.add(f));
      // versions.deleteById(v.getVersionId());
    });
    fileSet.forEach(f -> {
      files.deleteById(f.getId());
      try {
        this.deleteContainer(f.getId().toString());
        fileDaoEs.deleteByQuery(QueryBuilders.termQuery("id", f.getId().toString()));
      } catch (NotFoundException e) {
        LOGGER.warn(String.format("File with id %s for dataset %s NOT FOUND.", f.getId().toString(), id.toString()));
      } catch (R2d2TechnicalException e) {
        LOGGER.error(String.format("Error removing file with id %s from index.", f.getId().toString()));
      }
    });
    versionList.forEach(v -> {
      versions.deleteById(v.getVersionId());
    });
    datasets.deleteById(id);
    return datasetVersionDaoEs.deleteByQuery(QueryBuilders.termQuery("id", id.toString()));
  }

  public String deleteDatasetVersion(VersionId id) throws NotFoundException, R2d2TechnicalException {
    DatasetVersion version =
        versions.findById(id).orElseThrow(() -> new NotFoundException(String.format("Dataset with id %s NOT FOUND", id)));
    Pageable page = PageRequest.of(0, 25);
    Page<File> fileList = files.findAllForVersion(id, page);
    fileList.forEach(file -> {
      try {
        deleteContainer(file.getId().toString());
        fileDaoEs.deleteImmediatly(file.getId().toString());
      } catch (NotFoundException e) {
        LOGGER.warn(String.format("File with id %s for dataset %s NOT FOUND.", file.getId().toString(), id));
      } catch (R2d2TechnicalException e) {
        LOGGER.error(String.format("Error removing file with id %s from index.", file.getId().toString()));
      }
    });
    versions.deleteById(id);
    return datasetVersionDaoEs.deleteImmediatly(id.toString());
  }

  public List<String> listAllFiles() {
    List<String> fileIds = new ArrayList<>();
    files.findAll().iterator().forEachRemaining(file -> fileIds.add(file.getId().toString()));
    return fileIds;
  }

  public List<? extends Object> listAllContainers() {
    if (objectStore instanceof SwiftObjectStoreRepository) {
      return ((SwiftObjectStoreRepository) objectStore).listAllContainers();
    }
    if (objectStore instanceof S3ObjectStoreAwsSdkRepository) {
      try {
        return ((S3ObjectStoreAwsSdkRepository) objectStore).listAllContainers();
      } catch (NotFoundException | R2d2TechnicalException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return Collections.emptyList();
  }

  public Map<String, Object> clearObjectStore() {
    Map<String, Object> response = new LinkedHashMap<>();
    List<? extends Object> containers = listAllContainers();
    response.put("containers", containers.size());
    List<String> files = listAllFiles();
    response.put("files", files.size());
    containers.removeAll(files);
    response.put("2 be cleared", containers.size());
    /*
     * containers.forEach(id -> { try { deleteContainer(id); } catch
     * (NotFoundException e) {
     * LOGGER.warn(String.format("Container with id %s NOT FOUND.", id)); } });
     */
    return response;
  }

  public List<? extends Object> listContainerContent(String id) throws NotFoundException, R2d2TechnicalException {
    if (objectStore instanceof SwiftObjectStoreRepository) {
      return ((SwiftObjectStoreRepository) objectStore).listContainer(id);
    } else if (objectStore instanceof S3ObjectStoreAwsSdkRepository) {
      System.out.println("listContainerContent 4 " + id);
      return ((S3ObjectStoreAwsSdkRepository) objectStore).listContainer(id);
    }
    return Collections.emptyList();
  }

  public boolean deleteContainer(String id) throws NotFoundException {
    return objectStore.deleteContainer(id);
  }
}
