package de.mpg.mpdl.r2d2.rest.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.NotFoundException;
import de.mpg.mpdl.r2d2.exceptions.R2d2ApplicationException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.VersionId;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.model.search.SearchQuery;
import de.mpg.mpdl.r2d2.model.search.SearchResult;
import de.mpg.mpdl.r2d2.rest.controller.dto.DatasetVersionDto;
import de.mpg.mpdl.r2d2.rest.controller.dto.FileDto;
import de.mpg.mpdl.r2d2.rest.controller.dto.DtoMapper;
import de.mpg.mpdl.r2d2.service.DatasetVersionService;
import de.mpg.mpdl.r2d2.service.FileService;
import de.mpg.mpdl.r2d2.service.impl.FileUploadService;
import de.mpg.mpdl.r2d2.service.util.FileDownloadWrapper;
import de.mpg.mpdl.r2d2.util.Utils;

@RestController
@RequestMapping("datasets")
public class DatasetController {

  private static final Logger LOGGER = LoggerFactory.getLogger(DatasetController.class);

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private DatasetVersionService datasetVersionService;

  @Autowired
  private FileService fileService;

  @Autowired
  private DtoMapper dtoMapper;

  @PostMapping(path = "")
  public ResponseEntity<DatasetVersionDto> createDataset(@RequestBody DatasetVersionDto givenDatasetVersion, Principal prinz)
      throws R2d2TechnicalException, R2d2ApplicationException {

    DatasetVersion createdDv =
        datasetVersionService.create(dtoMapper.convertToDatasetVersion(givenDatasetVersion), Utils.toCustomPrincipal(prinz));
    return new ResponseEntity<DatasetVersionDto>(dtoMapper.convertToDatasetVersionDto(createdDv), HttpStatus.CREATED);
  }


  @PutMapping(path = "/{id}/metadata")
  public ResponseEntity<DatasetVersionDto> updateDatasetMetadata(@PathVariable("id") UUID id,
      @RequestBody DatasetVersionDto givenDatasetVersion, Principal prinz) throws R2d2TechnicalException, R2d2ApplicationException {

    DatasetVersion createdDv = null;

    createdDv = datasetVersionService.update(id, dtoMapper.convertToDatasetVersion(givenDatasetVersion), Utils.toCustomPrincipal(prinz));
    return new ResponseEntity<DatasetVersionDto>(dtoMapper.convertToDatasetVersionDto(createdDv), HttpStatus.OK);
  }


  @PutMapping(path = "/{id}/state")
  public ResponseEntity<DatasetVersionDto> changeDatasetState(@PathVariable("id") String id, @RequestBody DatasetVersionDto datasetVersion,
      Principal prinz) throws R2d2TechnicalException, R2d2ApplicationException {


    DatasetVersionDto dvToReturn = null;
    switch (datasetVersion.getState()) {

      case PUBLIC: {
        DatasetVersion publishedDv =
            datasetVersionService.publish(UUID.fromString(id), datasetVersion.getModificationDate(), Utils.toCustomPrincipal(prinz));
        return new ResponseEntity<DatasetVersionDto>(dtoMapper.convertToDatasetVersionDto(publishedDv), HttpStatus.OK);

      }
      case WITHDRAWN: {
        //TODO
        //DatasetVersion publishedDv = datasetVersionService.withdraw(UUID.fromString(id), datasetVersion.getModificationDate(), Utils.toCustomPrincipal(prinz));
        //return new ResponseEntity<DatasetVersionDto>(dtoMapper.convertToDatasetVersionDto(publishedDv), HttpStatus.OK);
        break;
      }
      default: {
        throw new R2d2ApplicationException("Unknown state: " + datasetVersion.getState());
      }
    }

    return null;
  }


  @GetMapping(path = "/{id}")
  public DatasetVersionDto getDataset(@PathVariable("id") String id, @RequestParam(name = "v", required = false) Integer versionNumber,
      Principal p) throws R2d2TechnicalException, R2d2ApplicationException {

    DatasetVersion dvToReturn = null;
    if (versionNumber == null) {
      dvToReturn = datasetVersionService.getLatest(UUID.fromString(id), Utils.toCustomPrincipal(p));
    } else {
      dvToReturn = datasetVersionService.get(new VersionId(UUID.fromString(id), versionNumber), Utils.toCustomPrincipal(p));
    }

    return dtoMapper.convertToDatasetVersionDto(dvToReturn);

  }

  @GetMapping("/{id}/files")
  public ResponseEntity<List<FileDto>> listFilesOfDataset(@PathVariable("id") String id,
      @RequestParam(name = "v", required = false) Integer versionNumber, @AuthenticationPrincipal R2D2Principal p, Pageable pageable)
      throws AuthorizationException, R2d2TechnicalException, NotFoundException {
	  VersionId versionId = null;
	  if (versionNumber != null) {
		    versionId = new VersionId(UUID.fromString(id), versionNumber);
	  } else {
		  versionId = new VersionId(UUID.fromString(id));
	  }
    Page<File> files = datasetVersionService.listFiles(versionId, pageable, p);
    List<FileDto> dtos = files.stream().map(f -> dtoMapper.convertToFileDto(f)).collect(Collectors.toList());
    HttpHeaders headers = new HttpHeaders();
    headers.add("Total-Number-of-Elements", Long.toString(files.getTotalElements()));
    headers.add("Elements-on-this-Page", Integer.toString(files.getNumberOfElements()));
    return new ResponseEntity<List<FileDto>>(dtos, headers, HttpStatus.OK);
  }



  @GetMapping("/{id}/files/{fileId}")
  public ResponseEntity<FileDto> getFileOfDataset(@PathVariable("id") String datasetId, @PathVariable("fileId") String fileId,
      @RequestParam(name = "v", required = false) Integer versionNumber, @AuthenticationPrincipal R2D2Principal p)
      throws R2d2ApplicationException, AuthorizationException, R2d2TechnicalException {
    VersionId versionId = new VersionId(UUID.fromString(datasetId), versionNumber);
    File file = datasetVersionService.getFileForDataset(versionId, UUID.fromString(fileId), p);
    return new ResponseEntity<FileDto>(dtoMapper.convertToFileDto(file), HttpStatus.OK);

  }

  // move to File Controller
  /*
  @GetMapping("/{id}/{versionNumber}/files/{fileId}")
  public ResponseEntity<?> download(@PathVariable("id") String datasetId, @PathVariable("versionNumber") int versionNumber,
      @PathVariable("fileId") String fileId,
      @RequestParam(value = "download", required = false, defaultValue = "false") boolean forceDownload, HttpServletResponse response,
      Principal prinz) throws R2d2ApplicationException, AuthorizationException, R2d2TechnicalException {
  
  
    FileDownloadWrapper fd = datasetVersionService.getFileContent(new VersionId(UUID.fromString(datasetId), versionNumber),
        UUID.fromString(fileId), Utils.toCustomPrincipal(prinz));
    try {
      String contentDispositionType = "inline";
      if (forceDownload) {
        contentDispositionType = "attachment";
      }
  
      response.setContentType(fd.getFile().getFormat());
  
      //Add filename and RFC 5987 encoded filename as content disposition headers
      response.setHeader("Content-Disposition", contentDispositionType + "; "
      //Leave only utf-8 encoded filename, as normal filename could lead to encoding problems in Apache
      //+ "filename=\"" + fileVOWrapper.getFileVO().getName() + "\"; "
          + "filename*=UTF-8''"
          + URLEncoder.encode(fd.getFile().getFilename(), StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20"));
  
      return new ResponseEntity<InputStreamResource>(new InputStreamResource(fd.readFile()), HttpStatus.OK);
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }
  }
  */



  //POST files

  @PutMapping("/{id}/files/{fileId}")
  public ResponseEntity<DatasetVersionDto> addFile(@PathVariable("id") String id, @PathVariable("fileId") String fileId,
      @RequestParam(name = "lmd") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime lmd,
      @AuthenticationPrincipal R2D2Principal p) throws R2d2ApplicationException, AuthorizationException, R2d2TechnicalException {
    DatasetVersion response = null;
    response = datasetVersionService.addFile(UUID.fromString(id), UUID.fromString(fileId), lmd, p);

    return new ResponseEntity<DatasetVersionDto>(dtoMapper.convertToDatasetVersionDto(response), HttpStatus.ACCEPTED);
  }

  @DeleteMapping("/{id}/files/{fileId}")
  public ResponseEntity<DatasetVersionDto> removeFile(@PathVariable("id") String id, @PathVariable("fileId") String fileId,
      @RequestParam(name = "lmd") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime lmd,
      @AuthenticationPrincipal R2D2Principal p) throws R2d2ApplicationException, AuthorizationException, R2d2TechnicalException {
    DatasetVersion response = null;
    response = datasetVersionService.removeFile(UUID.fromString(id), UUID.fromString(fileId), lmd, p);

    return new ResponseEntity<DatasetVersionDto>(dtoMapper.convertToDatasetVersionDto(response), HttpStatus.ACCEPTED);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> searchDetailed(@RequestBody JsonNode searchSource,
      @RequestParam(name = "scroll", required = false) String scrollTimeValue, HttpServletResponse httpResponse,
      @AuthenticationPrincipal R2D2Principal p) throws AuthorizationException, R2d2TechnicalException, IOException {

    String searchSourceText = objectMapper.writeValueAsString(searchSource);
    long scrollTime = -1;

    if (scrollTimeValue != null) {

      scrollTime = TimeValue.parseTimeValue(scrollTimeValue, "test").millis();
    }

    SearchSourceBuilder ssb = Utils.parseJsonToSearchSourceBuilder(searchSourceText);

    // SearchResponse resp = datasetVersionService.searchDetailed(ssb, scrollTime, Utils.toCustomPrincipal(p));
    SearchResponse resp = datasetVersionService.searchDetailed(ssb, scrollTime, p);


    httpResponse.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    /*
    XContentBuilder builder = XContentFactory.jsonBuilder();
    resp.toXContent(builder, ToXContent.EMPTY_PARAMS);
    */


    return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
  }


  @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SearchResult<DatasetVersionDto>> search(@RequestParam(name = "q", required = false) String query,
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



    SearchResult<DatasetVersion> resp = datasetVersionService.search(sq, p);


    return new ResponseEntity<SearchResult<DatasetVersionDto>>(dtoMapper.convertToSearchResultDto(resp), HttpStatus.OK);
  }



}
