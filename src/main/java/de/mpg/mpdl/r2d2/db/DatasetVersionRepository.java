package de.mpg.mpdl.r2d2.db;

import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;

public interface DatasetVersionRepository extends CrudRepository<DatasetVersion, UUID>{
	
	 @Query("SELECT datasetVersion FROM DatasetVersion datasetVersion WHERE datasetVersion.dataset=:datasetId AND datasetVersion.versionNumber=(SELECT MAX(datasetVersion.versionNumber) FROM DatasetVersion datasetVersion WHERE datasetVersion.dataset=:datasetId)")
	public DatasetVersion getLatestVersion(@Param("datasetId") UUID datasetId);

}
