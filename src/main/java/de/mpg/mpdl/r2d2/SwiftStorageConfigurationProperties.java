package de.mpg.mpdl.r2d2;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("mpcdf.cloud")
public class SwiftStorageConfigurationProperties {

  private String provider;
  private String scope;
  private String identity;
  private String credentials;
  private String endpoint;

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public String getIdentity() {
    return identity;
  }

  public void setIdentity(String identity) {
    this.identity = identity;
  }

  public String getCredentials() {
    return credentials;
  }

  public void setCredentials(String credentials) {
    this.credentials = credentials;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

}
