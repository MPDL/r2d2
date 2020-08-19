package de.mpg.mpdl.r2d2.db;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;

public interface DatasetRepository extends CrudRepository<Dataset, UUID> {

  @Query("select version from DatasetVersion version where version.id.dataset = :id")
  List<DatasetVersion> listAllVersions(@Param("id") UUID id);
}
