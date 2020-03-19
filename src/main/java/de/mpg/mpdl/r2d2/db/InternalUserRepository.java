package de.mpg.mpdl.r2d2.db;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.aa.InternalUser;
import de.mpg.mpdl.r2d2.model.aa.User;

public interface InternalUserRepository extends CrudRepository<InternalUser, String>{
	
	//public User findByEmail(String email);

}
