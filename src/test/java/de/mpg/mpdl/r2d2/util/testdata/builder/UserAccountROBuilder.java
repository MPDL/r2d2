package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.model.aa.UserAccountRO;

import java.util.UUID;

public final class UserAccountROBuilder {
  private UUID id;
  private String name;

  private UserAccountROBuilder() {}

  public static UserAccountROBuilder anUserAccountRO() {
    return new UserAccountROBuilder();
  }

  public UserAccountROBuilder id(UUID id) {
    this.id = id;
    return this;
  }

  public UserAccountROBuilder name(String name) {
    this.name = name;
    return this;
  }

  public UserAccountRO build() {
    UserAccountRO userAccountRO = new UserAccountRO();
    userAccountRO.setId(id);
    userAccountRO.setName(name);
    return userAccountRO;
  }
}
