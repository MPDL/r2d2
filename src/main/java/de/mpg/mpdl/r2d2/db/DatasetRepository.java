package de.mpg.mpdl.r2d2.db;

import org.springframework.data.repository.CrudRepository;

import de.mpg.mpdl.r2d2.model.Dataset;

public interface DatasetRepository extends CrudRepository<Dataset, Long>{

}
