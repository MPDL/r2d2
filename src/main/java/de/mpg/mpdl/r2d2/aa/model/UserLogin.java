package de.mpg.mpdl.r2d2.aa.model;

import javax.persistence.Entity;
import javax.persistence.Id;


public class UserLogin {


  private String username;

  private String password;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }



}
