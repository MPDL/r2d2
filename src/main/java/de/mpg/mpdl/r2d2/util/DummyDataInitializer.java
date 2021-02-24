package de.mpg.mpdl.r2d2.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import de.mpg.mpdl.r2d2.model.aa.Grant;
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
  @Qualifier("PublicDatasetVersionDaoImpl")
  private DatasetVersionDaoEs datasetVersionDao;

  @Autowired
  private UserAccountRepository userRepository;

  @Autowired
  private LocalUserAccountRepository internalUserRepository;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @Autowired
  private DtoMapper mapper;

  private UserAccount createUser(String name, UserAccount creator, Grant role) {

    UserAccount user = new UserAccount();
    user.setEmail(name + "@mpdl.mpg.de");
    user.setActive(true);
    Person person = new Person();
    person.setGivenName("Test");
    person.setFamilyName(name);
    user.setPerson(person);
    user.setId(UUID.randomUUID());
    if (creator != null) {
      user.setCreator(creator);
      user.setModifier(creator);
    } else {
      user.setCreator(user);
      user.setModifier(user);
    }
    // user3.setCreationDate(currentDateTime);
    // user3.setModificationDate(currentDateTime);
    user.getGrants().add(role);


    user = userRepository.save(user);

    LocalUserAccount internaluser3 = new LocalUserAccount();
    internaluser3.setUser(user);
    internaluser3.setUsername(name + "@mpdl.mpg.de");
    internaluser3.setPassword(passwordEncoder.encode("test"));

    internalUserRepository.save(internaluser3);
    return user;
  }

  @PostConstruct
  public void initialize() throws R2d2TechnicalException {

    OffsetDateTime currentDateTime = Utils.generateCurrentDateTimeForDatabase();

    
    
    UserAccount user = createUser("testuser", null, new Grant(Role.ADMIN, null));


    UserAccount user2 = createUser("testuser2", user, new Grant(Role.USER, null));
    UserAccount user3 = createUser("testuser3", user, new Grant(Role.USER, null));
    UserAccount user4 = createUser("testuser4", user, new Grant(Role.USER, null));
    UserAccount user5 = createUser("testuser5", user, new Grant(Role.USER, null));



    DatasetVersion dv = new DatasetVersion();
    //dv.setId(UUID.fromString("a6124f2a-9a06-489d-a7e2-40b583ebbd23"));
    dv.setCreator(user);
    dv.setModifier(user);
    // dv.setCreationDate(currentDateTime);
    // dv.setModificationDate(currentDateTime);
    dv.getMetadata().setTitle("Test title");

    Person author = new Person();
    author.setFamilyName("Last Name");
    author.setGivenName("First Name");
    dv.getMetadata().getAuthors().add(author);

    Dataset dataset = new Dataset();
    dataset.setId(UUID.fromString("a6124f2a-9a06-489d-a7e2-40b583ebbd23"));
    dataset.setCreator(user);
    dataset.setModifier(user);
    // dataset.setCreationDate(currentDateTime);
    // dataset.setModificationDate(currentDateTime);
    //dataset.getDatamanager().add(new UserAccountRO(user));
    dataset.setLatestVersion(1);
    dv.setDataset(dataset);

    dv = datasetVersionRepository.save(dv);
    datasetVersionDao.createImmediately(dv.getVersionId().toString(), mapper.convertToDatasetVersionIto(dv));

    DatasetVersion dv2 = new DatasetVersion();
    dv2.setState(State.PUBLIC);
    //dv2.setId(UUID.fromString("a6124f2a-9a06-489d-a7e2-40b583ebbd24"));
    dv2.setCreator(user);
    dv2.setModifier(user);
    // dv2.setCreationDate(currentDateTime);
    // dv2.setModificationDate(currentDateTime);
    dv2.getMetadata().setTitle("Test title 2");

    Person author2 = new Person();
    author2.setFamilyName("Last Name 2");
    author2.setGivenName("First Name 2");
    dv2.getMetadata().getAuthors().add(author2);

    Dataset dataset2 = new Dataset();
    dataset2.setId(UUID.fromString("a6124f2a-9a06-489d-a7e2-40b583ebbd24"));
    dataset2.setCreator(user);
    dataset2.setModifier(user);
    // dataset2.setCreationDate(currentDateTime);
    // dataset2.setModificationDate(currentDateTime);
    dataset2.setState(State.PUBLIC);
    dataset2.setLatestPublicVersion(1);
    dataset2.setLatestVersion(1);

    dv2.setDataset(dataset2);

    dv2 = datasetVersionRepository.save(dv2);
    datasetVersionDao.createImmediately(dv2.getVersionId().toString(), mapper.convertToDatasetVersionIto(dv2));



    DatasetVersion dv3 = new DatasetVersion();
    dv3.setState(State.PUBLIC);
    //dv3.setId(UUID.fromString("a6124f2a-9a06-489d-a7e2-40b583ebbd25"));
    dv2.setCreator(user);
    dv3.setModifier(user);
    // dv3.setCreationDate(currentDateTime);
    // dv3.setModificationDate(currentDateTime);
    dv3.getMetadata().setTitle("Test private item from user");

    Person author3 = new Person();
    author3.setFamilyName("Last Name 2");
    author3.setGivenName("First Name 2");
    dv3.getMetadata().getAuthors().add(author3);

    Dataset dataset3 = new Dataset();
    dataset3.setId(UUID.fromString("a6124f2a-9a06-489d-a7e2-40b583ebbd25"));
    dataset3.setCreator(user);
    dataset3.setModifier(user);
    // dataset3.setCreationDate(currentDateTime);
    // dataset3.setModificationDate(currentDateTime);
    dataset3.setState(State.PUBLIC);
    dataset3.setLatestVersion(1);
    dataset3.setLatestPublicVersion(1);
    dv3.setDataset(dataset3);

    dv3 = datasetVersionRepository.save(dv3);
    datasetVersionDao.createImmediately(dv3.getVersionId().toString(), mapper.convertToDatasetVersionIto(dv3));

  }

}
