package de.mpg.mpdl.r2d2.model;

import javax.validation.constraints.NotBlank;

import de.mpg.mpdl.r2d2.model.validation.PublishConstraintGroup;
import de.mpg.mpdl.r2d2.model.validation.SaveConstraintGroup;

public class Affiliation {

  //grid ID
  private String id;

  //organization/institute
  @NotBlank(message = "{affiliation.organization.blank}", groups = {PublishConstraintGroup.class})
  private String organization;

  //department/group
  private String department;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getOrganization() {
    return organization;
  }

  public void setOrganization(String organization) {
    this.organization = organization;
  }

  public String getDepartment() {
    return department;
  }

  public void setDepartment(String department) {
    this.department = department;
  }

}
