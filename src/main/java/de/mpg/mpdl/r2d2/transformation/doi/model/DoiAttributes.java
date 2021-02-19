package de.mpg.mpdl.r2d2.transformation.doi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class DoiAttributes {

  public enum DoiEvent {
    PUBLISH,
    REGISTER,
    HIDE;

    @JsonValue
    @Override
    public String toString() {
      return super.toString().toLowerCase();
    }
  }

  private DoiEvent event;

  private String prefix;

  private String url;

  private String xml;

  public DoiEvent getEvent() {
    return event;
  }

  public void setEvent(DoiEvent event) {
    this.event = event;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getXml() {
    return xml;
  }

  public void setXml(String xml) {
    this.xml = xml;
  }
}
