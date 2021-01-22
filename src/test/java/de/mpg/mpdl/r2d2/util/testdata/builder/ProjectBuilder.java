package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.model.Project;

public final class ProjectBuilder {
  private String name;
  private String acronym;
  private String grantId;
  private String fundingProgram;
  private String fundingOrganisation;

  private ProjectBuilder() {}

  public static ProjectBuilder aProject() {
    return new ProjectBuilder();
  }

  public ProjectBuilder name(String name) {
    this.name = name;
    return this;
  }

  public ProjectBuilder acronym(String acronym) {
    this.acronym = acronym;
    return this;
  }

  public ProjectBuilder grantId(String grantId) {
    this.grantId = grantId;
    return this;
  }

  public ProjectBuilder fundingProgram(String fundingProgram) {
    this.fundingProgram = fundingProgram;
    return this;
  }

  public ProjectBuilder fundingOrganisation(String fundingOrganisation) {
    this.fundingOrganisation = fundingOrganisation;
    return this;
  }

  public Project build() {
    Project project = new Project();
    project.setName(name);
    project.setAcronym(acronym);
    project.setGrantId(grantId);
    project.setFundingProgram(fundingProgram);
    project.setFundingOrganisation(fundingOrganisation);
    return project;
  }
}
