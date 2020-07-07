package de.mpg.mpdl.r2d2.registration;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import de.mpg.mpdl.r2d2.registration.validation.PasswordMatches;

@PasswordMatches(pattern = "newPass1", match = "newPass2")
public class PasswordChangeRequest {

  private String oldPass;

  private String token;

  @Size(min = 8, message = "{pass.min}")
  private String newPass1;

  private String newPass2;

  public String getOldPass() {
    return oldPass;
  }

  public void setOldPass(String oldPass) {
    this.oldPass = oldPass;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getNewPass1() {
    return newPass1;
  }

  public void setNewPass1(String newPass1) {
    this.newPass1 = newPass1;
  }

  public String getNewPass2() {
    return newPass2;
  }

  public void setNewPass2(String newPass2) {
    this.newPass2 = newPass2;
  }


}
