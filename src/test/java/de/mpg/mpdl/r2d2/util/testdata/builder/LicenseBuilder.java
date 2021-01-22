package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.model.License;

import java.net.URL;

public final class LicenseBuilder {
  private String name;
  private URL url;

  private LicenseBuilder() {}

  public static LicenseBuilder aLicense() {
    return new LicenseBuilder();
  }

  public LicenseBuilder name(String name) {
    this.name = name;
    return this;
  }

  public LicenseBuilder url(URL url) {
    this.url = url;
    return this;
  }

  public License build() {
    License license = new License();
    license.setName(name);
    license.setUrl(url);
    return license;
  }
}
