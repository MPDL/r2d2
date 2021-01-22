package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.transformation.doi.model.DoiResourceType;

public final class DoiResourceTypeBuilder {
  private String resourceTypeGeneral = DoiResourceType.RESOURCE_TYPE_GENERAL_DATASET;
  private String resourceType;

  private DoiResourceTypeBuilder() {}

  public static DoiResourceTypeBuilder aDoiResourceType() {
    return new DoiResourceTypeBuilder();
  }

  public DoiResourceTypeBuilder resourceTypeGeneral(String resourceTypeGeneral) {
    this.resourceTypeGeneral = resourceTypeGeneral;
    return this;
  }

  public DoiResourceTypeBuilder resourceType(String resourceType) {
    this.resourceType = resourceType;
    return this;
  }

  public DoiResourceType build() {
    DoiResourceType doiResourceType = new DoiResourceType();
    doiResourceType.setResourceTypeGeneral(resourceTypeGeneral);
    doiResourceType.setResourceType(resourceType);
    return doiResourceType;
  }
}
