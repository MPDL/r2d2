package de.mpg.mpdl.r2d2.db;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;

public interface DatasetVersionRepository extends CrudRepository<DatasetVersion, UUID>{
	
	
	public DatasetVersion getLatestVersion(UUID datasetId);

}
