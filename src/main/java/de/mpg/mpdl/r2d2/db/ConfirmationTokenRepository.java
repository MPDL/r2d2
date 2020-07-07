package de.mpg.mpdl.r2d2.db;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import de.mpg.mpdl.r2d2.model.aa.LocalUserAccount;
import de.mpg.mpdl.r2d2.registration.ConfirmationToken;

public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationToken, UUID> {

  ConfirmationToken findByToken(String token);

  ConfirmationToken findByUser(LocalUserAccount user);
}
