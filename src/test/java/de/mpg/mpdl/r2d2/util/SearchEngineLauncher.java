package de.mpg.mpdl.r2d2.util;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

public class SearchEngineLauncher {

  private static final String DOCKER_IMAGE_NAME = "docker.elastic.co/elasticsearch/elasticsearch:7.5.2";

  public static final ElasticsearchContainer ELASTIC_SEARCH_CONTAINER = creatAndStartElasticSearchContainer();

  private static ElasticsearchContainer creatAndStartElasticSearchContainer() {
    ElasticsearchContainer elasticSearchContainer = new ElasticsearchContainer(DOCKER_IMAGE_NAME);
    elasticSearchContainer.start();

    return elasticSearchContainer;
  }

  static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
      TestPropertyValues.of("elasticsearch.url=" + ELASTIC_SEARCH_CONTAINER.getHttpHostAddress())
          .applyTo(configurableApplicationContext.getEnvironment());
    }
  }

}
