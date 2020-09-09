package de.mpg.mpdl.r2d2.util.testdata;

import java.time.OffsetDateTime;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.r2d2.model.Person;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.model.aa.UserAccount.Role;
import de.mpg.mpdl.r2d2.model.aa.UserAccountRO;
import de.mpg.mpdl.r2d2.util.Utils;

@Component
@Scope("prototype")
public class UserAccountBuilder {

  private final UserAccount userAccount = new UserAccount();

  @PersistenceContext
  private EntityManager entityManager;

  public UserAccount create() {
    return this.userAccount;
  }

  public UserAccountBuilder setPerson(String givenName, String familyName) {
    Person person = new Person();
    person.setFamilyName(familyName);
    person.setGivenName(givenName);
    this.userAccount.setPerson(person);

    return this;
  }

  public UserAccountBuilder setCreatorAndModifier(UserAccount userAccount) {
    UserAccountRO userAccountRO = new UserAccountRO(userAccount);
    // this.userAccount.setCreator(userAccountRO);
    // this.userAccount.setModifier(userAccountRO);

    return this;
  }

  public UserAccountBuilder setCreatorAndModifier() {
    return this.setCreatorAndModifier(this.userAccount);
  }

  public UserAccountBuilder setcurrentCreationAndModificationDate() {
    OffsetDateTime currentDateTime = Utils.generateCurrentDateTimeForDatabase();

    this.userAccount.setCreationDate(currentDateTime);
    this.userAccount.setModificationDate(currentDateTime);

    return this;
  }

  public UserAccountBuilder setRole(Role role) {
    this.userAccount.getRoles().add(role);

    return this;
  }

  @Transactional
  public UserAccount persist() {
    UserAccount userAccount = this.create();

    entityManager.persist(userAccount);

    return userAccount;
  }

}
