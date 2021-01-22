package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.model.aa.LocalUserAccount;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;

public final class LocalUserAccountBuilder {
  String password;
  private String username;
  private UserAccount user;

  private LocalUserAccountBuilder() {}

  public static LocalUserAccountBuilder aLocalUserAccount() {
    return new LocalUserAccountBuilder();
  }

  public LocalUserAccountBuilder username(String username) {
    this.username = username;
    return this;
  }

  public LocalUserAccountBuilder user(UserAccount user) {
    this.user = user;
    return this;
  }

  public LocalUserAccountBuilder password(String password) {
    this.password = password;
    return this;
  }

  public LocalUserAccount build() {
    LocalUserAccount localUserAccount = new LocalUserAccount();
    localUserAccount.setUsername(username);
    localUserAccount.setUser(user);
    localUserAccount.setPassword(password);
    return localUserAccount;
  }
}
