package de.mpg.mpdl.r2d2.service;

import javax.persistence.EntityExistsException;

import de.mpg.mpdl.r2d2.exceptions.R2d2ApplicationException;
import de.mpg.mpdl.r2d2.model.aa.LocalUserAccount;
import de.mpg.mpdl.r2d2.registration.ConfirmationToken;
import de.mpg.mpdl.r2d2.registration.RegistrationRequest;


public interface UserService {

  LocalUserAccount registerNewUser(RegistrationRequest request) throws EntityExistsException;

  LocalUserAccount getUser(String token);

  LocalUserAccount getByEmail(String email);

  void saveLocalUserAccount(LocalUserAccount user);

  void createConfirmationToken(LocalUserAccount user, String token);

  ConfirmationToken getConfirmationToken(String token);

  ConfirmationToken renewConfirmationToken(String token);

  String validateConfirmationToken(String token);

  LocalUserAccount activateUser(String token);

  void resetPassword(LocalUserAccount user, String password);

  boolean validateOldPassword(LocalUserAccount user, String password);
}
