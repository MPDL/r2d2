package de.mpg.mpdl.rdrepo.db;

import org.springframework.data.repository.CrudRepository;

import de.mpg.mpdl.rdrepo.model.Dataset;

public interface DatasetRepository extends CrudRepository<Dataset, Long>{

}
