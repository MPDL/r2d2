package de.mpg.mpdl.r2d2;

import java.io.IOException;
import java.net.URI;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@ConditionalOnProperty(value = "r2d2.storage", havingValue = "s3awssdk")
public class S3ObjectStoreAwsSdkConfiguration {

  private final Logger LOGGER = LoggerFactory.getLogger(getClass());

  private S3Client s3Client;
  private S3AsyncClient s3AsyncClient;

  @Bean
  public S3ObjectStoreConfigurationProperties s3Properties() {
    return new S3ObjectStoreConfigurationProperties();
  }

  @PostConstruct
  public void init() {

  }

  @PreDestroy
  public void destroy() throws IOException {
    s3Client().close();
    LOGGER.info("successfully closed S3Client");
    s3AsyncClient().close();
    LOGGER.info("successfully closed S3AsyncClient");
  }

  @Bean
  public S3Client s3Client() {
    try {
      Region region = Region.of("n/a");
      s3Client = S3Client.builder().endpointOverride(new URI(s3Properties().getEndpoint())).region(region).build();

      LOGGER.info("successfully initialized S3Client 4 Endpoint" + s3Properties().getEndpoint());
      return s3Client;
    } catch (Exception e) {
      LOGGER.error("Error creating S3Client", e);
    }
    return null;
  }

  @Bean
  public S3AsyncClient s3AsyncClient() {
    try {
      Region region = Region.of("n/a");
      s3AsyncClient = S3AsyncClient.builder().endpointOverride(new URI(s3Properties().getEndpoint())).region(region).build();

      LOGGER.info("successfully initialized S3AsyncClient 4 Endpoint" + s3Properties().getEndpoint());
      return s3AsyncClient;
    } catch (Exception e) {
      LOGGER.error("Error creating S3AsyncClient", e);
    }
    return null;
  }
}
