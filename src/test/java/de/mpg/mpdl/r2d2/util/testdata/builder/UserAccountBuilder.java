package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.model.Person;
import de.mpg.mpdl.r2d2.model.aa.Grant;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class UserAccountBuilder {
  boolean active = false;
  private OffsetDateTime creationDate;
  private OffsetDateTime modificationDate;
  private UserAccount creator;
  private UserAccount modifier;
  //@GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;
  private String email;
  private Person person;
  private List<Grant> grants = new ArrayList<Grant>();

  private UserAccountBuilder() {}

  public static UserAccountBuilder anUserAccount() {
    return new UserAccountBuilder();
  }

  public UserAccountBuilder creationDate(OffsetDateTime creationDate) {
    this.creationDate = creationDate;
    return this;
  }

  public UserAccountBuilder modificationDate(OffsetDateTime modificationDate) {
    this.modificationDate = modificationDate;
    return this;
  }

  public UserAccountBuilder creator(UserAccount creator) {
    this.creator = creator;
    return this;
  }

  public UserAccountBuilder modifier(UserAccount modifier) {
    this.modifier = modifier;
    return this;
  }

  public UserAccountBuilder id(UUID id) {
    this.id = id;
    return this;
  }

  public UserAccountBuilder email(String email) {
    this.email = email;
    return this;
  }

  public UserAccountBuilder active(boolean active) {
    this.active = active;
    return this;
  }

  public UserAccountBuilder person(Person person) {
    this.person = person;
    return this;
  }

  public UserAccountBuilder grants(List<Grant> grants) {
    this.grants = grants;
    return this;
  }

  public UserAccount build() {
    UserAccount userAccount = new UserAccount();
    userAccount.setCreationDate(creationDate);
    userAccount.setModificationDate(modificationDate);
    userAccount.setCreator(creator);
    userAccount.setModifier(modifier);
    userAccount.setId(id);
    userAccount.setEmail(email);
    userAccount.setActive(active);
    userAccount.setPerson(person);
    userAccount.setGrants(grants);
    return userAccount;
  }
}
