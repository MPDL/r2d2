package de.mpg.mpdl.r2d2.model;

import java.net.URI;
import java.util.List;

public class Person {

  private String givenName;

  private String familyName;

  // ORCID
  private URI nameIdentifier;

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

  public URI getNameIdentifier() {
    return nameIdentifier;
  }

  public void setNameIdentifier(URI nameIdentifier) {
    this.nameIdentifier = nameIdentifier;
  }

  public List<Affiliation> getAffiliations() {
    return affiliations;
  }

  public void setAffiliations(List<Affiliation> affiliations) {
    this.affiliations = affiliations;
  }

}
