package de.mpg.mpdl.r2d2.rest.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.NotFoundException;
import de.mpg.mpdl.r2d2.exceptions.R2d2ApplicationException;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.VersionId;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.service.impl.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {

  @Autowired
  AdminService service;

  @GetMapping(value = "/test")
  public ResponseEntity<?> testTheController(HttpServletRequest request) throws AuthorizationException {
    String testInfo = service.test();
    List<String> all = service.listAllFiles();
    return new ResponseEntity<>(all, HttpStatus.OK);
  }

  @GetMapping(value = "/users")
  public ResponseEntity<?> listAllUsers(HttpServletRequest request) throws AuthorizationException {
    List<UserAccount> list = service.listAllUsers();
    return new ResponseEntity<>(list, HttpStatus.OK);
  }

  @GetMapping(value = "/users/{id}")
  public ResponseEntity<?> listUserById(@PathVariable("id") String uuid, HttpServletRequest request)
      throws AuthorizationException, NotFoundException {
    UserAccount user = service.listUserById(uuid);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @PatchMapping(value = "/users/{id}")
  public ResponseEntity<?> updateUser(@PathVariable("id") String uuid, @RequestBody UserAccount user2update) throws AuthorizationException {
    UserAccount user = service.updateUser(user2update);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @DeleteMapping(value = "/users/{id}")
  public ResponseEntity<?> deleteUser(@PathVariable("id") String uuid) throws AuthorizationException {
    service.deleteUser(uuid);
    return new ResponseEntity<>(HttpStatus.GONE);
  }

  @GetMapping(value = "/store")
  public ResponseEntity<?> listAllContainers() throws AuthorizationException {
	  return new ResponseEntity<>(service.listAllContainers(), HttpStatus.OK);
  }
  
  @GetMapping(value = "/store/clear")
  public ResponseEntity<?> clearObjectStore() throws AuthorizationException, NotFoundException {
    Map<String, Object> details = service.clearObjectStore();
    return new ResponseEntity<>(details, HttpStatus.OK);
  }

  @GetMapping(value = "/store/{id}")
  public ResponseEntity<?> listObjectStoreContainer(@PathVariable("id") String id) throws AuthorizationException, NotFoundException {
    List<Object> details = service.listContainerContent(id);
    return new ResponseEntity<>(details, HttpStatus.OK);
  }

  @DeleteMapping(value = "/store/{id}")
  public ResponseEntity<?> deleteContainer(@PathVariable("id") String uuid) throws AuthorizationException, NotFoundException {
    boolean acknowledged = service.deleteContainer(uuid);
    return new ResponseEntity<>(Collections.singletonMap("acknowledged", acknowledged), HttpStatus.GONE);
  }

  @GetMapping(value = "/datasets")
  public ResponseEntity<?> listAllDatasets(HttpServletRequest request) throws AuthorizationException {
    List<DatasetVersion> list = service.listAllDatasets();
    return new ResponseEntity<>(list, HttpStatus.OK);
  }

  @GetMapping(value = "/datasets/{id}/{versionNumber}")
  public ResponseEntity<?> listDataasetById(@PathVariable("id") String uuid, @PathVariable("versionNumber") Integer versionNumber,
      HttpServletRequest request) throws AuthorizationException, NotFoundException {
    DatasetVersion dataset = service.listDatasetById(new VersionId(UUID.fromString(uuid), versionNumber));
    return new ResponseEntity<>(dataset, HttpStatus.OK);
  }

  @DeleteMapping(value = "/datasets/{id}/{versionNumber}")
  public ResponseEntity<?> deleteDataset(@PathVariable("id") String uuid, @PathVariable("versionNumber") Integer versionNumber)
      throws AuthorizationException, NotFoundException {
    service.deleteDataset(new VersionId(UUID.fromString(uuid), versionNumber));
    return new ResponseEntity<>(HttpStatus.GONE);
  }
}
