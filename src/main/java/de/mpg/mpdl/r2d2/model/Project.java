package de.mpg.mpdl.r2d2.model;

public class Project {

  private String name;

  private String acronym;

  private String grantId;

  private String fundingProgram;

  private String fundingOrganisation;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAcronym() {
    return acronym;
  }

  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }

  public String getGrantId() {
    return grantId;
  }

  public void setGrantId(String grantId) {
    this.grantId = grantId;
  }

  public String getFundingProgram() {
    return fundingProgram;
  }

  public void setFundingProgram(String fundingProgram) {
    this.fundingProgram = fundingProgram;
  }

  public String getFundingOrganisation() {
    return fundingOrganisation;
  }

  public void setFundingOrganisation(String fundingOrganisation) {
    this.fundingOrganisation = fundingOrganisation;
  }
}
