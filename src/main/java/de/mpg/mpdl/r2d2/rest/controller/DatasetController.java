package de.mpg.mpdl.r2d2.rest.controller;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Collections;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.search.SearchModule;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.R2d2ApplicationException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.FileChunk;
import de.mpg.mpdl.r2d2.service.DatasetVersionService;
import de.mpg.mpdl.r2d2.util.Utils;

@RestController
@RequestMapping("api/datasets")
public class DatasetController {

  private static final Logger Logger = LoggerFactory.getLogger(DatasetController.class);

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private DatasetVersionService datasetVersionService;

  @GetMapping(path = "/dataset/{id}")
  public DatasetVersion getDataset(@PathVariable("id") String uuid, Principal p) throws R2d2TechnicalException, R2d2ApplicationException {

    return datasetVersionService.get(UUID.fromString(uuid), Utils.toCustomPrincipal(p));

  }


  @PostMapping("/dataset/{id}/files")
  public ResponseEntity<File> addEmptyFile(@PathVariable("id") String id, @RequestHeader("X-File-Name") String fileName,
      @RequestHeader("X-File-Total-Chunks") int totalChunks, @RequestHeader(name = "X-File-Total-Size", required = false) Long size,
      Principal prinz) throws R2d2ApplicationException, AuthorizationException, R2d2TechnicalException {

    File f = new File();
    f.setFilename(fileName);
    if (size != null) {
      f.setSize(size);
    }

    f.getStateInfo().setExpectedNumberOfChunks(totalChunks);
    f = datasetVersionService.addEmptyFile(UUID.fromString(id), f, Utils.toCustomPrincipal(prinz));

    return new ResponseEntity<File>(f, HttpStatus.CREATED);
  }

  @PostMapping("/dataset/{id}/files/{fileId}")
  public ResponseEntity<FileChunk> uploadChunk(@PathVariable("id") String id, @PathVariable("fileId") String fileId,
      @RequestHeader("X-File-Chunk-Number") int part, @RequestHeader(name= "etag", required=false) String etag,  @RequestHeader(name= "Content-Length", required=false) Long contentLength, HttpServletRequest req, Principal prinz)
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
    if(contentLength!=null)
    {
      chunk.setSize(contentLength);
    }
    FileChunk resultChunk = datasetVersionService.uploadChunk(UUID.fromString(id), UUID.fromString(fileId), chunk, is, Utils.toCustomPrincipal(prinz));

    ResponseEntity<FileChunk> re = new ResponseEntity<FileChunk>(resultChunk, HttpStatus.CREATED);
    re.getHeaders().add("etag", resultChunk.getServerEtag());
    
    
    return re;
  }

  
  @GetMapping("/dataset/{id}/files/{fileId}")
  public ResponseEntity<?> download(@PathVariable("id") String datasetId, @PathVariable("fileId") String fileId,
       Principal prinz) throws R2d2ApplicationException, AuthorizationException, R2d2TechnicalException {
    InputStreamResource inputStreamResource =
        new InputStreamResource(datasetVersionService.getFileContent(UUID.fromString(datasetId), UUID.fromString(fileId), Utils.toCustomPrincipal(prinz)));
    return new ResponseEntity<InputStreamResource>(inputStreamResource, HttpStatus.OK);
  }

  @RequestMapping(value = "/elasticsearch", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> searchDetailed(@RequestBody JsonNode searchSource,
      @RequestParam(name = "scroll", required = false) String scrollTimeValue, HttpServletResponse httpResponse, Principal p)
      throws AuthorizationException, R2d2TechnicalException, IOException {

    String searchSourceText = objectMapper.writeValueAsString(searchSource);
    long scrollTime = -1;

    if (scrollTimeValue != null) {

      scrollTime = TimeValue.parseTimeValue(scrollTimeValue, "test").millis();
    }

    SearchSourceBuilder ssb = Utils.parseJsonToSearchSourceBuilder(searchSourceText);

    SearchResponse resp = datasetVersionService.searchDetailed(ssb, scrollTime, Utils.toCustomPrincipal(p));


    httpResponse.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    /*
    XContentBuilder builder = XContentFactory.jsonBuilder();
    resp.toXContent(builder, ToXContent.EMPTY_PARAMS);
    */


    return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
  }

}
