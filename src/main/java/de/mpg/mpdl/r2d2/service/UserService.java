package de.mpg.mpdl.r2d2.service;

import javax.persistence.EntityExistsException;

import de.mpg.mpdl.r2d2.exceptions.R2d2ApplicationException;
import de.mpg.mpdl.r2d2.model.aa.LocalUserAccount;
import de.mpg.mpdl.r2d2.registration.RegistrationConfirmationToken;
import de.mpg.mpdl.r2d2.registration.RegistrationRequest;


public interface UserService {

  LocalUserAccount registerNewUser(RegistrationRequest request) throws EntityExistsException;

  LocalUserAccount getUser(String token);

  LocalUserAccount getByEmail(String email);

  void saveLocalUserAccount(LocalUserAccount user);

  void createConfirmationToken(LocalUserAccount user, String token);

  RegistrationConfirmationToken getConfirmationToken(String token);

  RegistrationConfirmationToken renewConfirmationToken(String token);

  String checkConfirmationToken(String token);

  void deleteUser(LocalUserAccount user);

}
