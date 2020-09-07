package de.mpg.mpdl.r2d2.transformation.doi.model;

public class DoiResourceType {

  public static final String RESOURCE_TYPE_GENERAL_DATASET = "Dataset";

  private String resourceTypeGeneral = RESOURCE_TYPE_GENERAL_DATASET;

  private String resourceType;

  public String getResourceTypeGeneral() {
    return resourceTypeGeneral;
  }

  public void setResourceTypeGeneral(String resourceTypeGeneral) {
    this.resourceTypeGeneral = resourceTypeGeneral;
  }

  public String getResourceType() {
    return resourceType;
  }

  public void setResourceType(String resourceType) {
    this.resourceType = resourceType;
  }
}
