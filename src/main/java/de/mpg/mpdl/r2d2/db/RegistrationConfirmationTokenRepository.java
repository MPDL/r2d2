package de.mpg.mpdl.r2d2.db;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import de.mpg.mpdl.r2d2.model.aa.LocalUserAccount;
import de.mpg.mpdl.r2d2.registration.RegistrationConfirmationToken;

public interface RegistrationConfirmationTokenRepository extends CrudRepository<RegistrationConfirmationToken, UUID> {

  RegistrationConfirmationToken findByToken(String token);

  RegistrationConfirmationToken findByUser(LocalUserAccount user);
}
