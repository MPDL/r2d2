package de.mpg.mpdl.r2d2.rest.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.mpg.mpdl.r2d2.exceptions.R2d2ApplicationException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.service.DatasetVersionService;

@RestController
@RequestMapping("api/dataset")
public class DatasetController {
	
	@Autowired
	private DatasetVersionService datasetVersionService;
	
	@GetMapping(path="/{uuid}")
	public DatasetVersion getDataset(@PathVariable("uuid") String uuid) throws R2d2TechnicalException, R2d2ApplicationException
	{
		return datasetVersionService.get(UUID.fromString(uuid), null);
		
	}

}
