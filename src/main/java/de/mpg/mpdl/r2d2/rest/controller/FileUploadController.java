package de.mpg.mpdl.r2d2.rest.controller;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.InvalidStateException;
import de.mpg.mpdl.r2d2.exceptions.NotFoundException;
import de.mpg.mpdl.r2d2.exceptions.OptimisticLockingException;
import de.mpg.mpdl.r2d2.exceptions.R2d2ApplicationException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.exceptions.ValidationException;
import de.mpg.mpdl.r2d2.model.FileChunk;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.service.FileService;
import de.mpg.mpdl.r2d2.service.impl.FileUploadService;
import de.mpg.mpdl.r2d2.util.Utils;

@RestController
@RequestMapping("files")
public class FileUploadController {

  @Autowired
  private FileService stagingFileService;

  @GetMapping("")
  public ResponseEntity<?> list(@AuthenticationPrincipal R2D2Principal p)
      throws AuthorizationException, R2d2TechnicalException, IOException {

    List<File> resp = ((FileUploadService) stagingFileService).list();
    return new ResponseEntity<>(resp, HttpStatus.OK);
  }

  @GetMapping("/{fileId}")
  public ResponseEntity<?> get(@PathVariable("fileId") String fileId, @AuthenticationPrincipal R2D2Principal p)
      throws AuthorizationException, R2d2TechnicalException, IOException, NotFoundException {

    File resp = ((FileUploadService) stagingFileService).list(UUID.fromString(fileId));
    return new ResponseEntity<>(resp, HttpStatus.OK);
  }

  @PostMapping("")
  public ResponseEntity<File> newSingleFileUpload(@RequestHeader("File-Name") String fileName,
      @RequestHeader("Content-Type") String contentType, @RequestHeader(name = "Content-MD5", required = false) String etag,
      HttpServletRequest request, @AuthenticationPrincipal R2D2Principal p)
      throws R2d2ApplicationException, AuthorizationException, R2d2TechnicalException {
    InputStream is;

    try {
      is = request.getInputStream();
    } catch (IOException e) {
      throw new R2d2TechnicalException(e);
    }

    File f = new File();
    f.setFilename(fileName);
    f.setFormat(contentType);
    if (etag != null) {
      f.setChecksum(etag);
    }

    f = stagingFileService.uploadSingleFile(f, is, p);

    BodyBuilder responseBuilder = ResponseEntity.status(HttpStatus.CREATED);

    if (f.getChecksum() != null) {
      responseBuilder.header("ETag", f.getChecksum());
    }

    return responseBuilder.body(f);

  }


  @PostMapping("/multipart")
  public ResponseEntity<File> newChunkedFileUpload(@RequestHeader("File-Name") String fileName,
      @RequestHeader("Content-Type") String contentType, @AuthenticationPrincipal R2D2Principal p)
      throws R2d2ApplicationException, AuthorizationException, R2d2TechnicalException {

    File f = new File();
    f.setFilename(fileName);
    f.setFormat(contentType);

    f = stagingFileService.initNewFile(f, p);

    BodyBuilder responseBuilder = ResponseEntity.status(HttpStatus.CREATED);

    return responseBuilder.body(f);
  }


  @PutMapping("/multipart/{fileId}")
  public ResponseEntity<FileChunk> uploadFileChunk(@PathVariable("fileId") String fileId, @RequestParam("part") int part,
      @RequestHeader(name = "Content-MD5", required = false) String etag, HttpServletRequest req, @AuthenticationPrincipal R2D2Principal p)
      throws R2d2ApplicationException, AuthorizationException, R2d2TechnicalException {

    InputStream is;

    try {
      is = req.getInputStream();
    } catch (IOException e) {
      throw new R2d2TechnicalException(e);
    }

    FileChunk chunk = new FileChunk();
    if (etag != null) {
      chunk.setClientEtag(etag);
    }
    chunk.setNumber(part);

    FileChunk resultChunk = stagingFileService.uploadFileChunk(UUID.fromString(fileId), chunk, is, p);

    ResponseEntity<FileChunk> re = ResponseEntity.status(HttpStatus.CREATED).header("ETag", resultChunk.getServerEtag()).body(resultChunk);

    return re;
  }

  @PostMapping("/multipart/{fileId}")
  public ResponseEntity<?> finishChunkedFileUpload(@PathVariable("fileId") String fileId, @RequestParam("parts") int parts,
      @AuthenticationPrincipal R2D2Principal p) throws R2d2TechnicalException, OptimisticLockingException, NotFoundException,
      InvalidStateException, AuthorizationException, ValidationException {

    if (parts == 0) {
      if (stagingFileService.delete(UUID.fromString(fileId), p)) {
        Map<String, Boolean> map = Collections.singletonMap("Acknowledged", true);
        return new ResponseEntity<>(map, HttpStatus.ACCEPTED);
      }
    }

    File sf = stagingFileService.completeChunkedUpload(UUID.fromString(fileId), parts, p);

    BodyBuilder responseBuilder = ResponseEntity.status(HttpStatus.CREATED);

    if (sf.getChecksum() != null) {
      responseBuilder.header("ETag", sf.getChecksum());
    }

    return responseBuilder.body(sf);
  }

  @DeleteMapping("/{fileId}")
  public ResponseEntity<?> delete(@PathVariable("fileId") String fileId, @AuthenticationPrincipal R2D2Principal p)
      throws R2d2TechnicalException, OptimisticLockingException, NotFoundException, InvalidStateException, AuthorizationException {
    if (stagingFileService.delete(UUID.fromString(fileId), p)) {
      Map<String, Boolean> map = Collections.singletonMap("Acknowledged", true);
      return new ResponseEntity<>(map, HttpStatus.ACCEPTED);
    }
    return null;
  }
}
