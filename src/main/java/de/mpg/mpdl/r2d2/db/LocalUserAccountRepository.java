package de.mpg.mpdl.r2d2.db;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.aa.LocalUserAccount;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;

public interface LocalUserAccountRepository extends CrudRepository<LocalUserAccount, UUID> {

  public Optional<LocalUserAccount> findByUsername(String username);

  long deleteByUser(UserAccount user);

}
