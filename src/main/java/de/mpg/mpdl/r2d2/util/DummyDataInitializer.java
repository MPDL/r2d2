package de.mpg.mpdl.r2d2.util;

import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.r2d2.db.DatasetVersionRepository;
import de.mpg.mpdl.r2d2.db.InternalUserRepository;
import de.mpg.mpdl.r2d2.db.UserRepository;
import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.Person;
import de.mpg.mpdl.r2d2.model.aa.InternalUser;
import de.mpg.mpdl.r2d2.model.aa.User;
import de.mpg.mpdl.r2d2.model.aa.User.Role;

@Component
public class DummyDataInitializer {

  @Autowired
  private DatasetVersionRepository datasetVersionRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private InternalUserRepository internalUserRepository;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @PostConstruct
  public void initialize() {
    User user = new User();
    user.setEmail("testuser@mpdl.mpg.de");
    user.setName("Test Admin");
    user.setId(UUID.fromString("2d0dd850-eabb-43fe-8b8f-1a1b54018738"));
    user.setCreator(user);
    user.setModifier(user);
    user.getRoles().add(Role.ADMIN);


    user = userRepository.save(user);

    InternalUser internalUser = new InternalUser();
    internalUser.setUser(user);
    internalUser.setUsername("testuser@mpdl.mpg.de");
    internalUser.setPassword(passwordEncoder.encode("test"));

    internalUserRepository.save(internalUser);


    DatasetVersion dv = new DatasetVersion();
    dv.setId(UUID.fromString("a6124f2a-9a06-489d-a7e2-40b583ebbd23"));
    dv.setCreator(user);
    dv.setModifier(user);
    dv.getMetadata().setTitle("Test title");

    Person author = new Person();
    author.setFamilyName("Last Name");
    author.setGivenName("First Name");
    dv.getMetadata().getAuthors().add(author);

    Dataset dataset = new Dataset();
    dataset.setId(UUID.fromString("9cdb1d04-8527-4c32-8e00-4e4730861cbb"));
    dataset.setCreator(user);
    dataset.setModifier(user);

    dv.setDataset(dataset);

    datasetVersionRepository.save(dv);

  }

}
