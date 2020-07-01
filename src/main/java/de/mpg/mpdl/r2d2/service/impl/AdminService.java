package de.mpg.mpdl.r2d2.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.r2d2.db.DatasetRepository;
import de.mpg.mpdl.r2d2.db.DatasetVersionRepository;
import de.mpg.mpdl.r2d2.db.FileRepository;
import de.mpg.mpdl.r2d2.db.UserAccountRepository;
import de.mpg.mpdl.r2d2.exceptions.NotFoundException;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.service.storage.SwiftObjectStoreRepository;

@Service
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminService {

  private static Logger LOGGER = LoggerFactory.getLogger(AdminService.class);

  @Autowired
  UserAccountRepository users;

  @Autowired
  DatasetVersionRepository datasets;

  @Autowired
  FileRepository files;

  @Autowired
  SwiftObjectStoreRepository objectStore;

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

  public void deleteUser(String id) {
    users.deleteById(UUID.fromString(id));
  }

  public List<DatasetVersion> listAllDatasets() {
    List<DatasetVersion> datasetList = new ArrayList<>();
    datasets.findAll().iterator().forEachRemaining(datasetList::add);
    return datasetList;
  }

  public DatasetVersion listDatasetById(String id) throws NotFoundException {
    return datasets.findById(UUID.fromString(id))
        .orElseThrow(() -> new NotFoundException(String.format("Dataset with id %s NOT FOUND", id)));
  }

  public void deleteDataset(String id) throws NotFoundException {
    DatasetVersion dataset =
        datasets.findById(UUID.fromString(id)).orElseThrow(() -> new NotFoundException(String.format("Dataset with id %s NOT FOUND", id)));
    List<File> files = dataset.getFiles();
    files.forEach(file -> {
      try {
        deleteContainer(file.getId().toString());
      } catch (NotFoundException e) {
        LOGGER.warn(String.format("File with id %s for dataset %s NOT FOUND.", file.getId().toString(), id));
      }
    });
    datasets.deleteById(UUID.fromString(id));
  }

  public List<String> listAllFiles() {
    List<String> fileIds = new ArrayList<>();
    files.findAll().iterator().forEachRemaining(file -> fileIds.add(file.getId().toString()));
    return fileIds;
  }

  public List<String> listAllContainers() {
    return objectStore.listAllContainers();
  }

  public Map<String, Object> clearObjectStore() {
    Map<String, Object> response = new LinkedHashMap<>();
    List<String> containers = listAllContainers();
    response.put("containers", containers.size());
    List<String> files = listAllFiles();
    response.put("files", files.size());
    containers.removeAll(files);
    response.put("2 be cleared", containers.size());
    /*
    containers.forEach(id -> {
    	try {
    		deleteContainer(id);
    	} catch (NotFoundException e) {
    		LOGGER.warn(String.format("Container with id %s NOT FOUND.", id));
    	}
    });
    */
    return response;
  }

  public List<Object> listContainerContent(String id) throws NotFoundException {
    return objectStore.listContainer(id);
  }

  public boolean deleteContainer(String id) throws NotFoundException {
    return objectStore.deleteContainer(id);
  }
}
