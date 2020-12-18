package de.mpg.mpdl.r2d2.service.impl;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityExistsException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.r2d2.db.ConfirmationTokenRepository;
import de.mpg.mpdl.r2d2.db.LocalUserAccountRepository;
import de.mpg.mpdl.r2d2.db.UserAccountRepository;
import de.mpg.mpdl.r2d2.model.Person;
import de.mpg.mpdl.r2d2.model.aa.LocalUserAccount;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.model.aa.UserAccount.Role;
import de.mpg.mpdl.r2d2.registration.ConfirmationToken;
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
  private ConfirmationTokenRepository tokenRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Transactional
  @Override
  public LocalUserAccount registerNewUser(RegistrationRequest request) throws EntityExistsException {

    if (emailExist(request.getEmail())) {
      throw new EntityExistsException(String.format("%s is already a registered user.", request.getEmail()));
    }
    UserAccount account = request2User(request);

    LocalUserAccount user = new LocalUserAccount();
    user.setUser(account);
    user.setUsername(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPass()));

    return userRepository.save(user);
  }

  private boolean emailExist(String username) {
    Optional<LocalUserAccount> user = userRepository.findByUsername(username);
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
  public LocalUserAccount getByEmail(String username) {
    return userRepository.findByUsername(username).get();
  }

  @Override
  public void saveLocalUserAccount(LocalUserAccount user) {
    userRepository.save(user);
  }

  @Override
  public void createConfirmationToken(LocalUserAccount user, String token) {
    ConfirmationToken token2register = new ConfirmationToken(token, user);
    tokenRepository.save(token2register);
  }

  @Override
  public ConfirmationToken getConfirmationToken(String token) {
    return tokenRepository.findByToken(token);
  }

  @Override
  public ConfirmationToken renewConfirmationToken(String oldToken) {
    ConfirmationToken token = tokenRepository.findByToken(oldToken);
    token.updateToken(UUID.randomUUID().toString());
    token = tokenRepository.save(token);
    return token;
  }

  @Override
  public String validateConfirmationToken(String token) {
    final ConfirmationToken token2validate = tokenRepository.findByToken(token);
    return !isExists(token2validate) ? "INVALID" : isExpired(token2validate) ? "EXPIRED" : null;
  }

  private boolean isExists(ConfirmationToken token) {
    return token != null;
  }

  private boolean isExpired(ConfirmationToken token) {
    final Calendar cal = Calendar.getInstance();
    return token.getExpirationDate().before(cal.getTime());
  }

  private UserAccount request2User(RegistrationRequest request) {
    UserAccount account = new UserAccount();
    account.setEmail(request.getEmail());
    Person person = new Person();
    person.setFamilyName(request.getLast());
    person.setGivenName(request.getFirst());
    if (request.getAffiliations() != null) {
      person.setAffiliations(request.getAffiliations());
    }
    if (request.getOrcid() != null) {
      person.setOrcid(request.getOrcid());
    }
    account.setPerson(person);
    account.setCreationDate(Utils.generateCurrentDateTimeForDatabase());
    account.setModificationDate(Utils.generateCurrentDateTimeForDatabase());
    account.getRoles().add(Role.USER);

    account = accountRepository.save(account);

    // account.setCreator(new UserAccountRO(account));
    // account.setModifier(new UserAccountRO(account));
    return accountRepository.save(account);
  }

  @Override
  public LocalUserAccount activateUser(String token4user) {
    final ConfirmationToken token = tokenRepository.findByToken(token4user);
    LocalUserAccount user = token.getUser();
    user.getUser().setActive(true);
    user = userRepository.save(user);
    tokenRepository.delete(token);
    return user;
  }

  @Override
  public void resetPassword(LocalUserAccount user, String password) {
    user.setPassword(passwordEncoder.encode(password));
    userRepository.save(user);
  }

  @Override
  public boolean validateOldPassword(final LocalUserAccount user, final String oldPassword) {
    return passwordEncoder.matches(oldPassword, user.getPassword());
  }
}
