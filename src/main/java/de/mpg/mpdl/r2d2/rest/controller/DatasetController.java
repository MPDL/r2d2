package de.mpg.mpdl.r2d2.rest.controller;

import java.security.Principal;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.mpg.mpdl.r2d2.exceptions.R2d2ApplicationException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.service.DatasetVersionService;
import de.mpg.mpdl.r2d2.util.Utils;

@RestController
@RequestMapping("api/dataset")
public class DatasetController {

  private static final Logger Logger = LoggerFactory.getLogger(DatasetController.class);

  @Autowired
  private DatasetVersionService datasetVersionService;

  @GetMapping(path = "/{uuid}")
  public DatasetVersion getDataset(@PathVariable("uuid") String uuid, Principal p) throws R2d2TechnicalException, R2d2ApplicationException {

    return datasetVersionService.get(UUID.fromString(uuid), Utils.toCustomPrincipal(p));

  }

}
