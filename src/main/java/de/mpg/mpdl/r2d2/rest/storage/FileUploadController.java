package de.mpg.mpdl.r2d2.rest.storage;

import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.R2d2ApplicationException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.exceptions.ValidationException;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.service.FileService;
import de.mpg.mpdl.r2d2.service.storage.FileStorageService;
import de.mpg.mpdl.r2d2.util.Utils;

@RestController
@RequestMapping("api/files")
public class FileUploadController {

  private FileStorageService storageService;
  private FileService fileService;

  @Autowired
  public FileUploadController(FileStorageService storage, FileService files) {
    this.storageService = storage;
    this.fileService = files;
  }

  @PostMapping("upload/{dataset}")
  public ResponseEntity<String> initializeUpload(@PathVariable("dataset") String dataset, @RequestHeader("Authorization") String token,
      @RequestHeader("X-File-Name") String fileName, @RequestHeader("X-File-Total-Chunks") int totalChunks, Principal prinz)
      throws R2d2ApplicationException, AuthorizationException, R2d2TechnicalException {
    String init = storageService.initializeUpload(dataset, fileName, totalChunks, Utils.toCustomPrincipal(prinz));
    return ResponseEntity.ok(init);
  }

  @PostMapping("upload/{dataset}/{file}")
  public ResponseEntity<File> uploadPart(@PathVariable("dataset") String dataset, @PathVariable("file") String file, @RequestHeader("Authorization") String token,
	      @RequestHeader("X-File-Chunk-Number") int part, HttpServletRequest request,  Principal prinz) throws R2d2ApplicationException {
	  File file2update = null;
	  boolean isMultipart = ServletFileUpload.isMultipartContent(request);
	    if (!isMultipart) {
	      throw new R2d2ApplicationException("PECH!");
	    }
	    ServletFileUpload upload = new ServletFileUpload();
	    try {
		    file2update = fileService.get(UUID.fromString(file), Utils.toCustomPrincipal(prinz));
	      FileItemIterator iterator = upload.getItemIterator(request);
	      while (iterator.hasNext()) {
	        FileItemStream item = iterator.next();
	        if (!item.isFormField()) {
	        	file2update = storageService.upload(file, part, item.openStream().readAllBytes(), "text/plain", Utils.toCustomPrincipal(prinz));
	        }
	      }
	    } catch (Exception e) {
	      throw new R2d2ApplicationException(e);
	    }
	    return ResponseEntity.ok(file2update);
  }

}
