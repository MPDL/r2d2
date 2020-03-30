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
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = {DatasetVersionRepositoryTest.Initializer.class})
public class DatasetVersionRepositoryTest {

  //TODO: Outsource the test DB/ES setup and configuration

  //Setup test DB in Container:

  public static PostgreSQLContainer<?> postgreSQLContainer = createAndStartDBContainer();

  public static PostgreSQLContainer<?> createAndStartDBContainer() {
    PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:12.2");
    container.withDatabaseName("integration-tests-db").withUsername("UN").withPassword("PW");
    container.start();

    return container;
  }

  //Setup test ES in Container:

  public static ElasticsearchContainer elasticSearchContainer = creatAndStartESContainer();

  public static ElasticsearchContainer creatAndStartESContainer() {
    ElasticsearchContainer container = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.5.2");
    container.start();

    return container;
  }

  //Configure DB & ES Test properties:

  static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
      TestPropertyValues.of("spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
          "spring.datasource.username=" + postgreSQLContainer.getUsername(),
          "spring.datasource.password=" + postgreSQLContainer.getPassword(),
          "elasticsearch.url=" + elasticSearchContainer.getHttpHostAddress()).applyTo(configurableApplicationContext.getEnvironment());
    }
  }

  //TODO: Stop DB/ES container after test?

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
