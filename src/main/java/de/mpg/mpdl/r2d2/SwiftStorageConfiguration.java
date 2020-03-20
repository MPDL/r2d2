package de.mpg.mpdl.r2d2;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.keystone.config.KeystoneProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

@Configuration
public class SwiftStorageConfiguration {

  private final Logger LOGGER = LoggerFactory.getLogger(getClass());

  private BlobStoreContext blobStoreContext;

  @Bean
  public SwiftStorageConfigurationProperties swiftProperties() {
    return new SwiftStorageConfigurationProperties();
  }

  @PostConstruct
  public void init() {
    final Properties overrides = new Properties();
    overrides.put(KeystoneProperties.KEYSTONE_VERSION, "3");
    overrides.put(KeystoneProperties.SCOPE, swiftProperties().getScope());

    Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());

    blobStoreContext = ContextBuilder.newBuilder(swiftProperties().getProvider()).endpoint(swiftProperties().getEndpoint())
        .credentials(swiftProperties().getIdentity(), swiftProperties().getCredentials()).modules(modules).overrides(overrides)
        .build(BlobStoreContext.class);

    LOGGER.info("successfully initialized Swift BlobStoreContext 4 " + swiftProperties().getIdentity());

  }

  @PreDestroy
  public void destroy() throws IOException {
    blobStoreContext.close();
    LOGGER.info("successfully closed Swift BlobStoreContext");
  }

  @Bean
  public BlobStoreContext blStoreContext() {
    return blobStoreContext;
  }
}
