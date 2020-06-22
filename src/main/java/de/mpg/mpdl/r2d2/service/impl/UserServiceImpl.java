package de.mpg.mpdl.r2d2.service.impl;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityExistsException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.r2d2.db.LocalUserAccountRepository;
import de.mpg.mpdl.r2d2.db.RegistrationConfirmationTokenRepository;
import de.mpg.mpdl.r2d2.db.UserAccountRepository;
import de.mpg.mpdl.r2d2.model.Person;
import de.mpg.mpdl.r2d2.model.aa.LocalUserAccount;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.model.aa.UserAccountRO;
import de.mpg.mpdl.r2d2.model.aa.UserAccount.Role;
import de.mpg.mpdl.r2d2.registration.RegistrationConfirmationToken;
import de.mpg.mpdl.r2d2.registration.RegistrationRequest;
import de.mpg.mpdl.r2d2.service.UserService;
import de.mpg.mpdl.r2d2.util.Utils;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserAccountRepository accountRepository;

  @Autowired
  private LocalUserAccountRepository userRepository;

  @Autowired
  private RegistrationConfirmationTokenRepository tokenRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Transactional
  @Override
  public LocalUserAccount registerNewUser(RegistrationRequest request) throws EntityExistsException {

    if (emailExist(request.getEmail())) {
      throw new EntityExistsException(request.getEmail() + " is already a registered user.");
    }
    UserAccount account = new UserAccount();
    account.setEmail(request.getEmail());
    Person person = new Person();
    person.setFamilyName(request.getLast());
    person.setGivenName(request.getFirst());
    account.setPerson(person);
    account.setCreationDate(Utils.generateCurrentDateTimeForDatabase());
    account.setModificationDate(Utils.generateCurrentDateTimeForDatabase());
    account.getRoles().add(Role.USER);

    account = accountRepository.save(account);
    
    account.setCreator(new UserAccountRO(account));
    account.setModifier(new UserAccountRO(account));
    account = accountRepository.save(account);

    LocalUserAccount user = new LocalUserAccount();
    user.setUser(account);
    user.setUsername(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPass()));

    return userRepository.save(user);
  }

  private boolean emailExist(String email) {
    Optional<LocalUserAccount> user = userRepository.findById(email);
    if (user.isPresent()) {
      return true;
    }
    return false;
  }

  @Override
  public LocalUserAccount getUser(String token) {
    LocalUserAccount user = tokenRepository.findByToken(token).getUser();
    return user;
  }

  @Override
  public LocalUserAccount getByEmail(String email) {
    return userRepository.findById(email).get();
  }

  @Override
  public void saveLocalUserAccount(LocalUserAccount user) {
    userRepository.save(user);
  }

  @Override
  public void createConfirmationToken(LocalUserAccount user, String token) {
    RegistrationConfirmationToken token2register = new RegistrationConfirmationToken(token, user);
    tokenRepository.save(token2register);
  }

  @Override
  public RegistrationConfirmationToken getConfirmationToken(String token) {
    return tokenRepository.findByToken(token);
  }

  @Override
  public RegistrationConfirmationToken renewConfirmationToken(String oldToken) {
    RegistrationConfirmationToken token = tokenRepository.findByToken(oldToken);
    token.updateToken(UUID.randomUUID().toString());
    token = tokenRepository.save(token);
    return token;
  }

  @Override
  public String checkConfirmationToken(String token) {
    final RegistrationConfirmationToken token2check = tokenRepository.findByToken(token);
    if (token2check == null) {
      return "INVALID";
    }

    final LocalUserAccount user = token2check.getUser();
    final Calendar cal = Calendar.getInstance();
    if ((token2check.getExpirationDate().getTime() - cal.getTime().getTime()) <= 0) {
      tokenRepository.delete(token2check);
      return "EXPIRED";
    }

    user.getUser().setActive(true);
    // tokenRepository.delete(token2check);
    userRepository.save(user);
    return "VALID";
  }

  @Override
  public void deleteUser(LocalUserAccount user) {
    final RegistrationConfirmationToken token = tokenRepository.findByUser(user);
    if (token != null) {
      tokenRepository.delete(token);
    }
    userRepository.delete(user);

  }

}
