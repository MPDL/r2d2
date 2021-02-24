package de.mpg.mpdl.r2d2.model.aa;

import java.util.List;
import java.util.UUID;

import de.mpg.mpdl.r2d2.model.aa.UserAccount.Role;

public class Grant {

  public Grant(Role role, UUID dataset) {
    this.role = role;
    this.dataset = dataset;
  }

  public Grant() {

  }

  private Role role;

  private UUID dataset;

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public UUID getDataset() {
    return dataset;
  }

  public void setDataset(UUID dataset) {
    this.dataset = dataset;
  }

}
