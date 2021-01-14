package de.mpg.mpdl.r2d2.transformation.doi.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class DoiResourceType {

  public static final String RESOURCE_TYPE_GENERAL_DATASET = "Dataset";

  @Builder.Default
  @XmlAttribute
  private String resourceTypeGeneral = RESOURCE_TYPE_GENERAL_DATASET;

  @XmlValue
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
