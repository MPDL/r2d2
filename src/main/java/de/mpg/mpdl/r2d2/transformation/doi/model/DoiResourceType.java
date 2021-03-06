package de.mpg.mpdl.r2d2.transformation.doi.model;

import javax.xml.bind.annotation.*;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class DoiResourceType {

  public static final String RESOURCE_TYPE_GENERAL_DATASET = "Dataset";

  public static final String RESOURCE_TYPE_DATASET = "Dataset";

  @XmlAttribute
  private String resourceTypeGeneral = RESOURCE_TYPE_GENERAL_DATASET;

  @XmlValue
  private String resourceType = RESOURCE_TYPE_DATASET;

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
