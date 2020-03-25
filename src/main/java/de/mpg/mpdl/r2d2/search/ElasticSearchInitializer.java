package de.mpg.mpdl.r2d2.search;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;

import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.search.es.connector.ElasticSearchAdminController;

@Configuration
public class ElasticSearchInitializer {

  private static Logger Logger = LoggerFactory.getLogger(ElasticSearchInitializer.class);

  @Autowired
  private ElasticSearchAdminController esAdminController;
  
  @Autowired
  private Environment env;

  @PostConstruct
  private void initializeIndex() {

    try {
      String datasetIndexSettings = Files
          .readString(Paths.get(ElasticSearchInitializer.class.getClassLoader().getResource("es/datasets_index_settings.json").toURI()));
      String datasetIndexMapping =
          Files.readString(Paths.get(ElasticSearchInitializer.class.getClassLoader().getResource("es/datasets_index_mapping.json").toURI()));

      esAdminController.createIndex(env.getProperty("index.dataset.name"), true, datasetIndexSettings, datasetIndexMapping);
    } catch (Exception e) {
      Logger.error("Error while initialzing index", e);
    }
  }



}
