package de.mpg.mpdl.r2d2.db;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.VersionId;

public interface DatasetVersionRepository extends JpaRepository<DatasetVersion, VersionId> {

  @Query("SELECT datasetVersion FROM DatasetVersion datasetVersion WHERE datasetVersion.dataset.id=:datasetId AND datasetVersion.versionNumber=(SELECT MAX(datasetVersion.versionNumber) FROM DatasetVersion datasetVersion WHERE datasetVersion.dataset.id=:datasetId)")
  public DatasetVersion findLatestVersion(@Param("datasetId") UUID datasetId);

  @Query("SELECT datasetVersion FROM DatasetVersion datasetVersion WHERE datasetVersion.dataset.id=:datasetId AND datasetVersion.state='PUBLIC' AND datasetVersion.versionNumber=(SELECT MAX(datasetVersion.versionNumber) FROM DatasetVersion datasetVersion WHERE datasetVersion.dataset.id=:datasetId)")
  public DatasetVersion findLatestPublicVersion(@Param("datasetId") UUID datasetId);

}
