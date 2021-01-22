package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.model.Affiliation;

public final class AffiliationBuilder {
  //grid ID
  private String id;
  //organization/institute
  private String organization;
  //department/group
  private String department;

  private AffiliationBuilder() {}

  public static AffiliationBuilder anAffiliation() {
    return new AffiliationBuilder();
  }

  public AffiliationBuilder id(String id) {
    this.id = id;
    return this;
  }

  public AffiliationBuilder organization(String organization) {
    this.organization = organization;
    return this;
  }

  public AffiliationBuilder department(String department) {
    this.department = department;
    return this;
  }

  public Affiliation build() {
    Affiliation affiliation = new Affiliation();
    affiliation.setId(id);
    affiliation.setOrganization(organization);
    affiliation.setDepartment(department);
    return affiliation;
  }
}
