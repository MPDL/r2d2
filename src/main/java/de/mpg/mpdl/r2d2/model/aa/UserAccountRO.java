package de.mpg.mpdl.r2d2.model.aa;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.UUID;


@Builder(toBuilder = true)
//@NoArgsConstructor is already defined
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserAccountRO {


  private UUID id;

  private String name;

  public UserAccountRO() {

  }

  public UserAccountRO(UserAccount userAccount) {
    this.id = userAccount.getId();
    this.name = userAccount.getPerson().getGivenName() + " " + userAccount.getPerson().getFamilyName();
  }


  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }



}
