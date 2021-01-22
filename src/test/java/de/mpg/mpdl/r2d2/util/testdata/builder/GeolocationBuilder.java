package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.model.Geolocation;

public final class GeolocationBuilder {
  private double latitude;
  private double longitude;
  private String description;

  private GeolocationBuilder() {}

  public static GeolocationBuilder aGeolocation() {
    return new GeolocationBuilder();
  }

  public GeolocationBuilder latitude(double latitude) {
    this.latitude = latitude;
    return this;
  }

  public GeolocationBuilder longitude(double longitude) {
    this.longitude = longitude;
    return this;
  }

  public GeolocationBuilder description(String description) {
    this.description = description;
    return this;
  }

  public Geolocation build() {
    Geolocation geolocation = new Geolocation();
    geolocation.setLatitude(latitude);
    geolocation.setLongitude(longitude);
    geolocation.setDescription(description);
    return geolocation;
  }
}
