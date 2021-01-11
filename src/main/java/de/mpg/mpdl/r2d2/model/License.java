package de.mpg.mpdl.r2d2.model;

import java.net.URL;

public class License {

  private String name;

  private URL url;

  //TODO: license text!?

  //TODO: Add license as type to the MD_schema file

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public URL getUrl() {
    return url;
  }

  public void setUrl(URL url) {
    this.url = url;
  }
}
