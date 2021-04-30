package de.mpg.mpdl.r2d2;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.keystone.config.KeystoneProperties;
import org.jclouds.s3.blobstore.S3BlobStoreContext;
import org.jclouds.s3.reference.S3Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

@Configuration
@ConditionalOnProperty(value = "r2d2.storage", havingValue = "s3")
public class S3ObjectStoreConfiguration {

  private final Logger LOGGER = LoggerFactory.getLogger(getClass());


  private BlobStoreContext blobStoreContext;

  @Bean
  public S3ObjectStoreConfigurationProperties s3Properties() {
    return new S3ObjectStoreConfigurationProperties();
  }

  @PostConstruct
  public void init() {

  }

  @PreDestroy
  public void destroy() throws IOException {
    blobStoreContext().close();
    LOGGER.info("successfully closed S3 BlobStoreContext");
  }

  @Bean
  public BlobStoreContext blobStoreContext() {
    try {
      final Properties overrides = new Properties();
      overrides.put(S3Constants.PROPERTY_S3_VIRTUAL_HOST_BUCKETS, "true");

      Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());

      LOGGER.info("authenticating with " + s3Properties().getAccessKey());
      blobStoreContext = ContextBuilder.newBuilder(s3Properties().getProvider()).endpoint(s3Properties().getEndpoint())
          .credentials(s3Properties().getAccessKey(), s3Properties().getSecretKey()).modules(modules).overrides(overrides)
          .build(S3BlobStoreContext.class);

      LOGGER.info("successfully initialized S3 BlobStoreContext 4 Endpoint" + s3Properties().getEndpoint());
      return blobStoreContext;
    } catch (Exception e) {
      LOGGER.error("Error creating blobStoreContext", e);
    }
    return null;
  }
}
