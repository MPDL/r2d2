package de.mpg.mpdl.r2d2.search;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.search.es.connector.ElasticSearchAdminController;

@Configuration
@ConditionalOnProperty(value = "init.index.creation", havingValue = "true")
public class ElasticSearchInitializer {

  private static Logger LOGGER = LoggerFactory.getLogger(ElasticSearchInitializer.class);

  @Autowired
  private ResourceLoader resourceLoader;

  @Autowired
  private ElasticSearchAdminController esAdminController;

  @Autowired
  private Environment env;

  @PostConstruct
  private void initializeIndex() {

    try {
      /*
      String datasetIndexSettings = Files
        .readString(Paths.get(ElasticSearchInitializer.class.getClassLoader().getResource("es/datasets_index_settings.json").toURI()));
      String datasetIndexMapping = Files
        .readString(Paths.get(ElasticSearchInitializer.class.getClassLoader().getResource("es/datasets_index_mapping.json").toURI()));
      */

      Resource datasets_index_settings = resourceLoader.getResource("classpath:es/datasets_index_settings.json");
      Resource datasets_index_mapping = resourceLoader.getResource("classpath:es/datasets_index_mapping.json");
      String datasetIndexSettings = StreamUtils.copyToString(datasets_index_settings.getInputStream(), Charset.forName("UTF-8"));
      String datasetIndexMapping = StreamUtils.copyToString(datasets_index_mapping.getInputStream(), Charset.forName("UTF-8"));


      Resource files_index_mapping = resourceLoader.getResource("classpath:es/files_index_mapping.json");
      String fileIndexMapping = StreamUtils.copyToString(files_index_mapping.getInputStream(), Charset.forName("UTF-8"));

      esAdminController.createIndex(env.getProperty("index.dataset.public.name"), true, datasetIndexSettings, datasetIndexMapping);
      esAdminController.createIndex(env.getProperty("index.dataset.latest.name"), true, datasetIndexSettings, datasetIndexMapping);
      esAdminController.createIndex(env.getProperty("index.file.name"), true, datasetIndexSettings, fileIndexMapping);
    } catch (Exception e) {
      LOGGER.error("Error while initialzing index", e);
    }
  }



}
