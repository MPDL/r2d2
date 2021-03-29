package de.mpg.mpdl.r2d2.model;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import de.mpg.mpdl.r2d2.model.validation.PublishConstraintGroup;
import de.mpg.mpdl.r2d2.model.validation.SaveConstraintGroup;

public class Person {

  @NotBlank(message = "{person.familyName.blank}", groups = {PublishConstraintGroup.class})
  private String givenName;

  @NotBlank(message = "{person.givenName.blank}", groups = {PublishConstraintGroup.class})
  private String familyName;

  // ORCID (The id part of the ORCID-URI)
  private String orcid;

  // Affiliation contains department/group
  @NotEmpty(message = "{person.affiliations.empty}", groups = {PublishConstraintGroup.class})
  private List<@Valid Affiliation> affiliations = new ArrayList<>();

  public String getGivenName() {
    return givenName;
  }

  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  public String getFamilyName() {
    return familyName;
  }

  public void setFamilyName(String familyName) {
    this.familyName = familyName;
  }

  public String getOrcid() {
    return orcid;
  }

  public void setOrcid(String orcid) {
    this.orcid = orcid;
  }

  public List<Affiliation> getAffiliations() {
    return affiliations;
  }

  public void setAffiliations(List<Affiliation> affiliations) {
    this.affiliations = affiliations;
  }

}
