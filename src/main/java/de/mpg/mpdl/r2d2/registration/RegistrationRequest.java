package de.mpg.mpdl.r2d2.registration;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.mpg.mpdl.r2d2.registration.validation.PasswordMatches;
import de.mpg.mpdl.r2d2.registration.validation.ValidEmail;


@PasswordMatches
public class RegistrationRequest {

  private final String NOT_EMPTY_MSG = "MUST not be empty!";

  @NotNull
  @Size(min = 1, message = NOT_EMPTY_MSG)
  private String first;
  @NotNull
  @Size(min = 1, message = NOT_EMPTY_MSG)
  private String last;
  @NotNull
  @ValidEmail
  private String email;
  @NotNull
  @Size(min = 8, message = "MUST be at least 8 characters ...")
  private String pass;
  private String match;

  public String getFirst() {
    return first;
  }

  public void setFirst(String first) {
    this.first = first;
  }

  public String getLast() {
    return last;
  }

  public void setLast(String last) {
    this.last = last;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPass() {
    return pass;
  }

  public void setPass(String pass) {
    this.pass = pass;
  }

  public String getMatch() {
    return match;
  }

  public void setMatch(String match) {
    this.match = match;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    //@formatter:off
    builder.append("RegistrationRequest {first = ").append(first).append(", last = ").append(last).append(", email = ").append(email)
        .append(", pass = ").append(pass).append(", match = ").append(match).append("]");
    //@formatter:on
    return builder.toString();
  }
}
