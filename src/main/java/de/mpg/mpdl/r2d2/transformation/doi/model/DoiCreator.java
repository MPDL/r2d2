package de.mpg.mpdl.r2d2.transformation.doi.model;

import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"creatorName", "givenName", "familyName"})
public class DoiCreator {

  private String creatorName;

  private String givenName;

  private String familyName;

  public String getCreatorName() {
    return creatorName;
  }

  public void setCreatorName(String creatorName) {
    this.creatorName = creatorName;
  }

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
}
