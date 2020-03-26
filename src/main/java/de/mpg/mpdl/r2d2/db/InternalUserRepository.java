package de.mpg.mpdl.r2d2.db;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.aa.LocalUserAccount;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;

public interface InternalUserRepository extends CrudRepository<LocalUserAccount, String> {

  //public User findByEmail(String email);

}
