package de.mpg.mpdl.r2d2.db;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.VersionId;

public interface FileRepository extends JpaRepository<File, UUID> {

  @Query("select file from File file join file.datasets version where version.id = :versionId")
  Page<File> findAllForVersion(@Param("versionId") VersionId versionId, Pageable pageable);

  @Query("select file.id from File file join file.datasets version where version.id = :versionId")
  List<UUID> findAllIdsForVersion(@Param("versionId") VersionId versionId);

  @Query("select file from File file join file.datasets version where version.id = :versionId")
  List<File> findAllForVersion(@Param("versionId") VersionId versionId);

  @Query("select file from File file join file.datasets version where version.dataset.id = :datasetId")
  List<File> findAllForDataset(@Param("datasetId") UUID datasetId);

}
