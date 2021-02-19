package de.mpg.mpdl.r2d2.transformation.doi.model;

import com.fasterxml.jackson.annotation.*;

// @JsonRootName("data")
@JsonTypeName("data")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DoiData {

  public enum DoiType {
    DOIS;

    @JsonValue
    @Override
    public String toString() {
      return super.toString().toLowerCase();
    }
  }

  private DoiType type;

  private DoiAttributes attributes;

  public DoiType getType() {
    return type;
  }

  public void setType(DoiType type) {
    this.type = type;
  }

  public DoiAttributes getAttributes() {
    return attributes;
  }

  public void setAttributes(DoiAttributes attributes) {
    this.attributes = attributes;
  }
}
