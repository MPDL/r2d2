package de.mpg.mpdl.r2d2.registration;

import java.util.List;

import javax.validation.GroupSequence;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import de.mpg.mpdl.r2d2.model.Affiliation;
import de.mpg.mpdl.r2d2.registration.validation.FirstConstraint;
import de.mpg.mpdl.r2d2.registration.validation.PasswordMatches;
import de.mpg.mpdl.r2d2.registration.validation.SecondConstraint;
import de.mpg.mpdl.r2d2.registration.validation.ValidEmail;


@PasswordMatches(groups = SecondConstraint.class, pattern = "pass", match = "match")
@GroupSequence({RegistrationRequest.class, FirstConstraint.class, SecondConstraint.class})
public class RegistrationRequest {

  @NotBlank(message = "{first.not.blank}")
  private String first;
  @NotBlank(message = "{last.not.blank}")
  private String last;
  @NotBlank(message = "{email.not.blank}")
  @ValidEmail(groups = SecondConstraint.class)
  private String email;
  //@NotBlank(message = "{pass.not.blank}")
  @Size(min = 8, message = "{pass.min}")
  private String pass;
  private String match;

  @Valid
  private List<Affiliation> affiliations;

  private String orcid;

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

  public List<Affiliation> getAffiliations() {
    return affiliations;
  }

  public void setAffiliations(List<Affiliation> affiliations) {
    this.affiliations = affiliations;
  }

  public String getOrcid() {
    return orcid;
  }

  public void setOrcid(String orcid) {
    this.orcid = orcid;
  }

}
