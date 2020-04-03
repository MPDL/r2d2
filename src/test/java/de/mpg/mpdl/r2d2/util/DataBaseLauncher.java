package de.mpg.mpdl.r2d2.util;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

public class DataBaseLauncher {

  private static final String DOCKER_IMAGE_NAME = "postgres:12.2";

  private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = createAndStartPostgreSQLContainer();

  private static PostgreSQLContainer<?> createAndStartPostgreSQLContainer() {
    PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DOCKER_IMAGE_NAME);
    postgreSQLContainer.withDatabaseName("testDatabaseName").withUsername("username").withPassword("password");
    postgreSQLContainer.start();

    return postgreSQLContainer;
  }

  static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
      TestPropertyValues.of("spring.datasource.url=" + POSTGRESQL_CONTAINER.getJdbcUrl(),
          "spring.datasource.username=" + POSTGRESQL_CONTAINER.getUsername(),
          "spring.datasource.password=" + POSTGRESQL_CONTAINER.getPassword()).applyTo(configurableApplicationContext.getEnvironment());
    }
  }

}
