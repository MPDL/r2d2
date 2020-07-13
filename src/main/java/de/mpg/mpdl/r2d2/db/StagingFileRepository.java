package de.mpg.mpdl.r2d2.db;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.StagingFile;

public interface StagingFileRepository extends CrudRepository<StagingFile, UUID> {

}
