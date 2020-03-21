package de.mpg.mpdl.r2d2;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.filesystem.reference.FilesystemConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "r2d2.storage", havingValue = "fs")
public class FileSystemStorageConfiguration {

  private final Logger LOGGER = LoggerFactory.getLogger(getClass());

  private BlobStoreContext blobStoreContext;

  @Value("${fs.location}")
  private String location;

  @PostConstruct
  public void init() {
    final Properties overrides = new Properties();
    overrides.put(FilesystemConstants.PROPERTY_BASEDIR, location);

    blobStoreContext = ContextBuilder.newBuilder("filesystem").overrides(overrides).build(BlobStoreContext.class);

    LOGGER.info("successfully initialized FS BlobStoreContext @ " + location);

  }

  @PreDestroy
  public void destroy() throws IOException {
    blobStoreContext.close();
    LOGGER.info("successfully closed FS BlobStoreContext");
  }

  @Bean
  public BlobStoreContext blStoreContext() {
    return blobStoreContext;
  }
}
