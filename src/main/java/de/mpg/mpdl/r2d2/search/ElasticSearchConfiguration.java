package de.mpg.mpdl.r2d2.search;

import java.net.InetAddress;
import java.net.UnknownHostException;

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

import de.mpg.mpdl.r2d2.exceptions.R2d2ApplicationException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.search.es.connector.ElasticSearchAdminController;

@Configuration
public class ElasticSearchConfiguration {

  private static Logger Logger = LoggerFactory.getLogger(ElasticSearchConfiguration.class);

  @Autowired
  @Lazy
  private ElasticSearchAdminController esAdminController;


  @Autowired
  private Environment env;

  @Bean
  public RestHighLevelClient elasticSearchClient() throws R2d2TechnicalException {
    try {
      return new RestHighLevelClient(RestClient.builder(HttpHost.create(env.getProperty("elasticsearch.url"))));
    } catch (Exception e) {
      throw new R2d2TechnicalException("Could not create ElasticSearch rest client", e);
    }
  }



}
