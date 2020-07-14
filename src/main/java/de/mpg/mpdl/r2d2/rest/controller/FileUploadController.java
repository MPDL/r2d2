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
import org.springframework.web.bind.annotation.RestController;

import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.InvalidStateException;
import de.mpg.mpdl.r2d2.exceptions.NotFoundException;
import de.mpg.mpdl.r2d2.exceptions.OptimisticLockingException;
import de.mpg.mpdl.r2d2.exceptions.R2d2ApplicationException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.FileChunk;
import de.mpg.mpdl.r2d2.model.StagingFile;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.service.StagingFileService;
import de.mpg.mpdl.r2d2.service.impl.FileUploadService;
import de.mpg.mpdl.r2d2.util.Utils;

@RestController
@RequestMapping("files")
public class FileUploadController {

  @Autowired
  private StagingFileService stagingFileService;

  @GetMapping("")
  public ResponseEntity<?> list(HttpServletResponse httpResponse, @AuthenticationPrincipal R2D2Principal p)
      throws AuthorizationException, R2d2TechnicalException, IOException {

    List<StagingFile> resp = ((FileUploadService) stagingFileService).list();
    return new ResponseEntity<>(resp, HttpStatus.OK);
  }

  @PostMapping("")
  public ResponseEntity<StagingFile> newFile(@RequestHeader("X-File-Name") String fileName,
      @RequestHeader(name = "X-File-Total-Chunks", required = false) Integer totalChunks,
      @RequestHeader(name = "X-File-Total-Size") Long size, HttpServletRequest req, Principal prinz)
      throws R2d2ApplicationException, AuthorizationException, R2d2TechnicalException {

    InputStream is;

    try {
      is = req.getInputStream();
    } catch (IOException e) {
      throw new R2d2TechnicalException(e);
    }

    StagingFile f = new StagingFile();
    f.setFilename(fileName);
    if (size != null) {
      f.setSize(size);
    }

    if (totalChunks != null) {
      try {
        if (is.read() != -1) {
          throw new R2d2ApplicationException("Body must be empty. Upload chunks after this initialization");
        }
      } catch (IOException e) {
        throw new R2d2TechnicalException(e);
      }

      f.getStateInfo().setExpectedNumberOfChunks(totalChunks);
      f = stagingFileService.initNewFile(f, Utils.toCustomPrincipal(prinz));
    } else {
      f = stagingFileService.uploadSingleFile(f, is, Utils.toCustomPrincipal(prinz));
    }

    BodyBuilder responseBuilder = ResponseEntity.status(HttpStatus.CREATED);

    if (f.getChecksum() != null) {
      responseBuilder.header("etag", f.getChecksum());
    }

    return responseBuilder.body(f);
  }


  @PutMapping("/{fileId}")
  public ResponseEntity<FileChunk> uploadFileChunk(@PathVariable("fileId") String fileId, @RequestHeader("X-File-Chunk-Number") int part,
      @RequestHeader(name = "etag", required = false) String etag,
      @RequestHeader(name = "X-File-Chunk-Size", required = false) Long contentLength, HttpServletRequest req, Principal prinz)
      throws R2d2ApplicationException, AuthorizationException, R2d2TechnicalException {

    InputStream is;

    try {
      is = req.getInputStream();
    } catch (IOException e) {
      throw new R2d2TechnicalException(e);
    }

    FileChunk chunk = new FileChunk();
    chunk.setClientEtag(etag);
    chunk.setNumber(part);
    if (contentLength != null) {
      chunk.setSize(contentLength);
    }
    FileChunk resultChunk = stagingFileService.uploadFileChunk(UUID.fromString(fileId), chunk, is, Utils.toCustomPrincipal(prinz));

    ResponseEntity<FileChunk> re = ResponseEntity.status(HttpStatus.CREATED).header("etag", resultChunk.getServerEtag()).body(resultChunk);

    return re;
  }
  
  @DeleteMapping("/{fileId}")
  public ResponseEntity<?> delete(@PathVariable("fileId") String fileId, @AuthenticationPrincipal R2D2Principal p) throws R2d2TechnicalException, OptimisticLockingException, NotFoundException, InvalidStateException, AuthorizationException {
	  if (stagingFileService.delete(UUID.fromString(fileId), p)) {
		  Map<String, Boolean> map = Collections.singletonMap("Acknowledged", true);
		  return new ResponseEntity<>(map, HttpStatus.ACCEPTED);
	  }
	return null;
  }
}
