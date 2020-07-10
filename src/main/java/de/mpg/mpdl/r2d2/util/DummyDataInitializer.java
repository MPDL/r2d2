package de.mpg.mpdl.r2d2.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.r2d2.db.DatasetVersionRepository;
import de.mpg.mpdl.r2d2.db.LocalUserAccountRepository;
import de.mpg.mpdl.r2d2.db.UserAccountRepository;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.DatasetVersionRO;
import de.mpg.mpdl.r2d2.model.Person;
import de.mpg.mpdl.r2d2.model.Dataset.State;
import de.mpg.mpdl.r2d2.model.aa.LocalUserAccount;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.model.aa.UserAccount.Role;
import de.mpg.mpdl.r2d2.model.aa.UserAccountRO;
import de.mpg.mpdl.r2d2.search.dao.DatasetVersionDaoEs;

@Component
@ConditionalOnProperty(value = "init.data.creation", havingValue = "true")
public class DummyDataInitializer {

  @Autowired
  private DatasetVersionRepository datasetVersionRepository;

  @Autowired
  private DatasetVersionDaoEs datasetVersionDao;

  @Autowired
  private UserAccountRepository userRepository;

  @Autowired
  private LocalUserAccountRepository internalUserRepository;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @PostConstruct
  public void initialize() throws R2d2TechnicalException {

    OffsetDateTime currentDateTime = Utils.generateCurrentDateTimeForDatabase();

    UserAccount user = new UserAccount();
    user.setEmail("testuser@mpdl.mpg.de");
    user.setActive(true);
    Person person = new Person();
    person.setGivenName("Test");
    person.setFamilyName("Admin");
    user.setPerson(person);
    user.setId(UUID.fromString("2d0dd850-eabb-43fe-8b8f-1a1b54018738"));
    user.setCreator(new UserAccountRO(user));
    user.setModifier(new UserAccountRO(user));
    user.setCreationDate(currentDateTime);
    user.setModificationDate(currentDateTime);
    user.getRoles().add(Role.ADMIN);


    user = userRepository.save(user);

    LocalUserAccount internalUser = new LocalUserAccount();
    internalUser.setUser(user);
    internalUser.setUsername("testuser@mpdl.mpg.de");
    internalUser.setPassword(passwordEncoder.encode("test"));

    internalUserRepository.save(internalUser);


    UserAccount user2 = new UserAccount();
    user2.setEmail("testuser2@mpdl.mpg.de");
    user2.setActive(true);
    Person person2 = new Person();
    person2.setGivenName("Test");
    person2.setFamilyName("User");
    user2.setPerson(person2);
    user2.setId(UUID.fromString("2d0dd850-eabb-43fe-8b8f-1a1b54018739"));
    user2.setCreator(new UserAccountRO(user));
    user2.setModifier(new UserAccountRO(user));
    user2.setCreationDate(currentDateTime);
    user2.setModificationDate(currentDateTime);
    user2.getRoles().add(Role.USER);


    user2 = userRepository.save(user2);

    LocalUserAccount internalUser2 = new LocalUserAccount();
    internalUser2.setUser(user2);
    internalUser2.setUsername("testuser2@mpdl.mpg.de");
    internalUser2.setPassword(passwordEncoder.encode("test"));

    internalUserRepository.save(internalUser2);

    DatasetVersion dv = new DatasetVersion();
    dv.setId(UUID.fromString("a6124f2a-9a06-489d-a7e2-40b583ebbd23"));
    dv.setCreator(new UserAccountRO(user));
    dv.setModifier(new UserAccountRO(user));
    dv.setCreationDate(currentDateTime);
    dv.setModificationDate(currentDateTime);
    dv.getMetadata().setTitle("Test title");

    Person author = new Person();
    author.setFamilyName("Last Name");
    author.setGivenName("First Name");
    dv.getMetadata().getAuthors().add(author);

    Dataset dataset = new Dataset();
    dataset.setId(dv.getId());
    dataset.setCreator(new UserAccountRO(user));
    dataset.setModifier(new UserAccountRO(user));
    dataset.setCreationDate(currentDateTime);
    dataset.setModificationDate(currentDateTime);
    dataset.getDatamanager().add(new UserAccountRO(user));
    dataset.setLatestVersion(1);
    dv.setDataset(dataset);

    dv = datasetVersionRepository.save(dv);
    datasetVersionDao.createImmediately(dv.getId().toString(), dv);

    DatasetVersion dv2 = new DatasetVersion();
    dv2.setState(State.PUBLIC);
    dv2.setId(UUID.fromString("a6124f2a-9a06-489d-a7e2-40b583ebbd24"));
    dv2.setCreator(new UserAccountRO(user));
    dv2.setModifier(new UserAccountRO(user));
    dv2.setCreationDate(currentDateTime);
    dv2.setModificationDate(currentDateTime);
    dv2.getMetadata().setTitle("Test title 2");

    Person author2 = new Person();
    author2.setFamilyName("Last Name 2");
    author2.setGivenName("First Name 2");
    dv2.getMetadata().getAuthors().add(author2);

    Dataset dataset2 = new Dataset();
    dataset2.setId(dv2.getId());
    dataset2.setCreator(new UserAccountRO(user));
    dataset2.setModifier(new UserAccountRO(user));
    dataset2.setCreationDate(currentDateTime);
    dataset2.setModificationDate(currentDateTime);
    dataset2.setState(State.PUBLIC);
    dataset2.setLatestPublicVersion(1);
    dataset2.setLatestVersion(1);

    dv2.setDataset(dataset2);

    dv2 = datasetVersionRepository.save(dv2);
    datasetVersionDao.createImmediately(dv2.getId().toString(), dv2);



    DatasetVersion dv3 = new DatasetVersion();
    dv3.setState(State.PUBLIC);
    dv3.setId(UUID.fromString("a6124f2a-9a06-489d-a7e2-40b583ebbd25"));
    dv3.setCreator(new UserAccountRO(user2));
    dv3.setModifier(new UserAccountRO(user2));
    dv3.setCreationDate(currentDateTime);
    dv3.setModificationDate(currentDateTime);
    dv3.getMetadata().setTitle("Test private item from user");

    Person author3 = new Person();
    author3.setFamilyName("Last Name 2");
    author3.setGivenName("First Name 2");
    dv3.getMetadata().getAuthors().add(author3);

    Dataset dataset3 = new Dataset();
    dataset3.setId(dv3.getId());
    dataset3.setCreator(new UserAccountRO(user2));
    dataset3.setModifier(new UserAccountRO(user2));
    dataset3.setCreationDate(currentDateTime);
    dataset3.setModificationDate(currentDateTime);
    dataset3.setState(State.PUBLIC);
    dataset3.setLatestVersion(1);
    dataset3.setLatestPublicVersion(1);
    dv3.setDataset(dataset3);

    dv3 = datasetVersionRepository.save(dv3);
    datasetVersionDao.createImmediately(dv3.getId().toString(), dv3);

  }

}
