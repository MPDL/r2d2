package de.mpg.mpdl.r2d2.rest.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import de.mpg.mpdl.r2d2.model.FileUploadStatus;
import de.mpg.mpdl.r2d2.model.VersionId;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.rest.controller.dto.DatasetVersionDto;
import de.mpg.mpdl.r2d2.rest.controller.dto.FileDto;
import de.mpg.mpdl.r2d2.search.model.FileIto;
import de.mpg.mpdl.r2d2.search.model.SearchQuery;
import de.mpg.mpdl.r2d2.search.model.SearchResult;
import de.mpg.mpdl.r2d2.search.service.FileSearchService;
import de.mpg.mpdl.r2d2.service.FileService;
import de.mpg.mpdl.r2d2.service.impl.FileUploadService;
import de.mpg.mpdl.r2d2.service.util.FileDownloadWrapper;
import de.mpg.mpdl.r2d2.util.DtoMapper;
import de.mpg.mpdl.r2d2.util.Utils;

@RestController
@RequestMapping("files")
public class FileUploadController {

  @Autowired
  private FileService fileService;

  @Autowired
  private FileSearchService fileSearchService;

  @Autowired
  private DtoMapper dtoMapper;

  /*
  @GetMapping("")
  public ResponseEntity<List<FileDto>> list(Pageable pageable, @AuthenticationPrincipal R2D2Principal p)
      throws AuthorizationException, R2d2TechnicalException, IOException, NotFoundException {
  
    Page<File> files = fileService.list(pageable, p);
    List<FileDto> list = files.map(f -> dtoMapper.convertToFileDto(f)).toList();
    return new ResponseEntity<List<FileDto>>(list, HttpStatus.OK);
  }
  */

  @GetMapping("")
  public ResponseEntity<SearchResult<FileDto>> search(@RequestParam(name = "q", required = false) String query,
      @RequestParam(name = "scroll", required = false) String scrollTimeValue, @RequestParam(name = "from", required = false) Integer from,
      @RequestParam(name = "size", required = false) Integer size, HttpServletResponse httpResponse,
      @AuthenticationPrincipal R2D2Principal p) throws AuthorizationException, R2d2TechnicalException, IOException {

    SearchQuery sq = new SearchQuery();
    sq.setQuery(query);
    if (from != null) {
      sq.setFrom(from);
    }
    if (size != null) {
      sq.setSize(size);
    }

    SearchResult<FileIto> resp = fileSearchService.search(sq, p);

    return new ResponseEntity<SearchResult<FileDto>>(dtoMapper.convertToFileSearchResultDto(resp), HttpStatus.OK);
  }


  @GetMapping("/{fileId}")
  public ResponseEntity<FileDto> get(@PathVariable("fileId") String fileId, @AuthenticationPrincipal R2D2Principal p)
      throws AuthorizationException, R2d2TechnicalException, IOException, NotFoundException {

    File resp = fileService.get(UUID.fromString(fileId), p);
    FileDto dto = dtoMapper.convertToFileDto(resp);
    return new ResponseEntity<FileDto>(dto, HttpStatus.OK);
  }

  @GetMapping("/{fileId}/uploadstate")
  public ResponseEntity<FileUploadStatus> getUploadState(@PathVariable("fileId") String fileId, @AuthenticationPrincipal R2D2Principal p)
      throws AuthorizationException, R2d2TechnicalException, IOException, NotFoundException {
    File resp = fileService.get(UUID.fromString(fileId), p);
    FileUploadStatus state = resp.getStateInfo();
    return new ResponseEntity<FileUploadStatus>(state, HttpStatus.OK);
  }

  @PostMapping("")
  public ResponseEntity<FileDto> newSingleFileUpload(@RequestHeader("File-Name") String fileName,
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

    // TODO format is not set, please check
    f.setFormat(contentType);
    if (etag != null) {
      f.setChecksum(etag);
    }

    f = fileService.uploadSingleFile(f, is, p);

    BodyBuilder responseBuilder = ResponseEntity.status(HttpStatus.CREATED);

    if (f.getChecksum() != null) {
      responseBuilder.header("ETag", f.getChecksum());
    }

    return responseBuilder.body(dtoMapper.convertToFileDto(f));

  }

  @PostMapping("/multipart")
  public ResponseEntity<FileDto> newChunkedFileUpload(@RequestHeader("File-Name") String fileName,
      @RequestHeader("Content-Type") String contentType, @AuthenticationPrincipal R2D2Principal p)
      throws R2d2ApplicationException, AuthorizationException, R2d2TechnicalException {

    File f = new File();
    f.setFilename(fileName);
    f.setFormat(contentType);

    f = fileService.initNewFile(f, p);

    BodyBuilder responseBuilder = ResponseEntity.status(HttpStatus.CREATED);

    return responseBuilder.body(dtoMapper.convertToFileDto(f));
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

    FileChunk resultChunk = fileService.uploadFileChunk(UUID.fromString(fileId), chunk, is, p);

    ResponseEntity<FileChunk> re = ResponseEntity.status(HttpStatus.CREATED).header("ETag", resultChunk.getServerEtag()).body(resultChunk);

    return re;
  }

  @PostMapping("/multipart/{fileId}")
  // Send optional etag Content-MD5 instead of parts
  public ResponseEntity<FileDto> finishChunkedFileUpload(@PathVariable("fileId") String fileId, @RequestParam("parts") int parts,
      @AuthenticationPrincipal R2D2Principal p) throws R2d2TechnicalException, OptimisticLockingException, NotFoundException,
      InvalidStateException, AuthorizationException, ValidationException {

    if (parts == 0) {
      fileService.delete(UUID.fromString(fileId), p);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    File sf = fileService.completeChunkedUpload(UUID.fromString(fileId), parts, p);

    BodyBuilder responseBuilder = ResponseEntity.status(HttpStatus.CREATED);

    if (sf.getChecksum() != null) {
      responseBuilder.header("ETag", sf.getChecksum());
    }

    return responseBuilder.body(dtoMapper.convertToFileDto(sf));
  }

  @DeleteMapping("/{fileId}")
  public ResponseEntity<?> delete(@PathVariable("fileId") String fileId, @AuthenticationPrincipal R2D2Principal p)
      throws R2d2TechnicalException, OptimisticLockingException, NotFoundException, InvalidStateException, AuthorizationException {
    fileService.delete(UUID.fromString(fileId), p);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping("/{fileId}/content")
  public ResponseEntity<?> download(@PathVariable("fileId") String fileId,
      @RequestParam(value = "download", required = false, defaultValue = "false") boolean forceDownload, HttpServletResponse response,
      @AuthenticationPrincipal R2D2Principal p) throws R2d2ApplicationException, AuthorizationException, R2d2TechnicalException {

    FileDownloadWrapper fd = fileService.getFileContent(UUID.fromString(fileId), p);
    try {
      String contentDispositionType = "inline";
      if (forceDownload) {
        contentDispositionType = "attachment";
      }

      response.setContentType(fd.getFile().getFormat());

      // Add filename and RFC 5987 encoded filename as content disposition headers
      response.setHeader("Content-Disposition", contentDispositionType + "; "
      // Leave only utf-8 encoded filename, as normal filename could lead to encoding
      // problems in Apache
      // + "filename=\"" + fileVOWrapper.getFileVO().getName() + "\"; "
          + "filename*=UTF-8''"
          + URLEncoder.encode(fd.getFile().getFilename(), StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20"));

      return new ResponseEntity<InputStreamResource>(new InputStreamResource(fd.readFile()), HttpStatus.OK);
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }
  }
}
