package de.mpg.mpdl.r2d2.db;

import org.springframework.data.repository.CrudRepository;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.User;

public interface UserRepository extends CrudRepository<User, Long>{

}
