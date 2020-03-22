package de.mpg.mpdl.r2d2.rest.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import de.mpg.mpdl.r2d2.exceptions.R2d2ApplicationException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.service.storage.StorageService;

@RestController
public class StorageController {

  private static final Logger logger = LoggerFactory.getLogger(StorageController.class);

  StorageService storageService;

  @Autowired
  public StorageController(StorageService service) {
    this.storageService = service;
  }

  @PostMapping("/up/{containerId}")
  public ResponseEntity<List<File>> upload(@PathVariable("containerId") String containerId, HttpServletRequest request)
      throws R2d2ApplicationException {
	  List<File> fileList = new ArrayList<>();
    boolean isMultipart = ServletFileUpload.isMultipartContent(request);
    if (!isMultipart) {
      throw new R2d2ApplicationException("PECH!");
    }
    File file = null;
    ServletFileUpload upload = new ServletFileUpload();
    try {
      FileItemIterator iterator = upload.getItemIterator(request);
      while (iterator.hasNext()) {
        FileItemStream item = iterator.next();
        if (!item.isFormField()) {
          file = storageService.store(containerId, item);
          fileList.add(file);
        }
      }
    } catch (FileUploadException | IOException | R2d2TechnicalException e) {
      throw new R2d2ApplicationException(e);
    }
    return ResponseEntity.ok(fileList);

  }

}
