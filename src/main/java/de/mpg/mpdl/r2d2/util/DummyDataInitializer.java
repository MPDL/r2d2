package de.mpg.mpdl.r2d2.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.r2d2.db.DatasetVersionRepository;
import de.mpg.mpdl.r2d2.db.LocalUserAccountRepository;
import de.mpg.mpdl.r2d2.db.UserAccountRepository;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.Person;
import de.mpg.mpdl.r2d2.model.Dataset.State;
import de.mpg.mpdl.r2d2.model.aa.LocalUserAccount;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.model.aa.UserAccount.Role;
import de.mpg.mpdl.r2d2.model.aa.UserAccountRO;
import de.mpg.mpdl.r2d2.search.dao.DatasetVersionDaoEs;

@Component
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
    
    OffsetDateTime currentDateTime = OffsetDateTime.now();
    
    UserAccount user = new UserAccount();
    user.setEmail("testuser@mpdl.mpg.de");
    user.setName("Test Admin");
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
    dataset.setId(UUID.fromString("9cdb1d04-8527-4c32-8e00-4e4730861cbb"));
    dataset.setCreator(new UserAccountRO(user));
    dataset.setModifier(new UserAccountRO(user));
    dataset.setCreationDate(currentDateTime);
    dataset.setModificationDate(currentDateTime);
    dataset.getDatamanager().add(new UserAccountRO(user));

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
    dataset2.setId(UUID.fromString("9cdb1d04-8527-4c32-8e00-4e4730861cbc"));
    dataset2.setCreator(new UserAccountRO(user));
    dataset2.setModifier(new UserAccountRO(user));
    dataset2.setCreationDate(currentDateTime);
    dataset2.setModificationDate(currentDateTime);
    dataset2.setState(State.PUBLIC);

    dv2.setDataset(dataset2);

    dv2 = datasetVersionRepository.save(dv2);
    datasetVersionDao.createImmediately(dv2.getId().toString(), dv2);

  }

}
