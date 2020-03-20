package de.mpg.mpdl.r2d2;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootApplication
@PropertySource("classpath:application.r2d2.properties")
public class R2D2Application {

  @Autowired
  private Environment env;



  public static void main(String[] args) {
    SpringApplication.run(R2D2Application.class, args);
  }

  @Bean
  public RestHighLevelClient elasticSearchClient() {
    return new RestHighLevelClient(RestClient.builder(new HttpHost(env.getProperty("elasticsearch.url"))));
  }



  @Bean
  public ObjectMapper jsonObjectMapper() {
    ObjectMapper jsonObjectMapper = new ObjectMapper();
    jsonObjectMapper.registerModule(new JavaTimeModule());
    jsonObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    jsonObjectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    jsonObjectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    jsonObjectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

    return jsonObjectMapper;

  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
