package de.mpg.mpdl.r2d2;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("s3")
public class S3ObjectStoreConfigurationProperties {

  private String provider;
  private String accessKey;
  private String secretKey;
  private String endpoint;

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public String getAccessKey() {
    return accessKey;
  }

  public void setAccessKey(String accessKey) {
    this.accessKey = accessKey;
  }

  public String getSecretKey() {
    return secretKey;
  }

  public void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

}
