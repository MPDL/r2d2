package de.mpg.mpdl.r2d2.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = {DatasetVersionRepositoryTest.Initializer.class})
public class DatasetVersionRepositoryTest {

  //Setup test DB in Container + Configure DB Test properties:
  //TODO: Outsource the test DB setup and configuration

  public static PostgreSQLContainer<?> postgreSQLContainer = createAndStartContainer();

  public static PostgreSQLContainer<?> createAndStartContainer() {
    PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:12.2");
    container.withDatabaseName("integration-tests-db").withUsername("UN").withPassword("PW");
    container.start();

    return container;
  }

  static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
      TestPropertyValues.of("spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
          "spring.datasource.username=" + postgreSQLContainer.getUsername(),
          "spring.datasource.password=" + postgreSQLContainer.getPassword()).applyTo(configurableApplicationContext.getEnvironment());
    }
  }

  //Test specifics:

  @Autowired
  private DatasetVersionRepository datasetVersionRepository;

  @Test
  public void testGetLatestVersion() {
    //Given
    Dataset dataset = new Dataset();
    DatasetVersion datasetVersion1 = new DatasetVersion();
    datasetVersion1.setDataset(dataset);

    DatasetVersion datasetVersion2 = new DatasetVersion();
    datasetVersion2.setDataset(dataset);
    int latestVersionNumber = 2;
    datasetVersion2.setVersionNumber(latestVersionNumber);

    datasetVersionRepository.saveAll(Arrays.asList(datasetVersion1, datasetVersion2));

    //When
    DatasetVersion latestDatasetVersion = datasetVersionRepository.getLatestVersion(dataset.getId());

    //Then
    assertThat(latestDatasetVersion.getVersionNumber()).isEqualTo(latestVersionNumber);
  }

}
