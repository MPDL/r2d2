package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.model.aa.Grant;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;

import java.util.UUID;

public final class GrantBuilder {
  private UserAccount.Role role;
  private UUID dataset;

  private GrantBuilder() {}

  public static GrantBuilder aGrant() {
    return new GrantBuilder();
  }

  public GrantBuilder role(UserAccount.Role role) {
    this.role = role;
    return this;
  }

  public GrantBuilder dataset(UUID dataset) {
    this.dataset = dataset;
    return this;
  }

  public Grant build() {
    Grant grant = new Grant();
    grant.setRole(role);
    grant.setDataset(dataset);
    return grant;
  }
}
