package de.mpg.mpdl.r2d2.model.aa;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import de.mpg.mpdl.r2d2.model.BaseDb;


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
