package de.mpg.mpdl.r2d2.model;

import java.util.List;

public class Person {

  private String givenName;

  private String familyName;

  // ORCID (The id part of the ORCID-URI)
  private String orcid;

  // Affiliation contains department/group
  private List<Affiliation> affiliations;

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
