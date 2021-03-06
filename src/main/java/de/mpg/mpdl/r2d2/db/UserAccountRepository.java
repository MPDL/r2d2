package de.mpg.mpdl.r2d2.db;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {

  public UserAccount findByEmail(String email);

}
