package de.mpg.mpdl.r2d2;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
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



  /**
   * Overwrites the default Jackson ObjectMapper from Spring Boot
   * 
   * @return
   */
  @Bean
  @Primary
  public ObjectMapper jsonObjectMapper() {
    ObjectMapper jsonObjectMapper = new ObjectMapper();
    JavaTimeModule timeModule = new JavaTimeModule();

    jsonObjectMapper.registerModule(timeModule);
    jsonObjectMapper.setSerializationInclusion(Include.NON_EMPTY);
    jsonObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    jsonObjectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    jsonObjectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    jsonObjectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    jsonObjectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

    return jsonObjectMapper;

  }


  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public MessageSource messageSource() {
    ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
    messageSource.setBasenames("classpath:messages/registration", "classpath:messages/other");
    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
  }

  @Bean
  public LocalValidatorFactoryBean validator(MessageSource messageSource) {
    LocalValidatorFactoryBean validationBean = new LocalValidatorFactoryBean();
    validationBean.setValidationMessageSource(messageSource);
    return validationBean;
  }

  @Bean
  public SessionLocaleResolver localeResolver() {
    SessionLocaleResolver localeResolver = new SessionLocaleResolver();
    localeResolver.setDefaultLocale(Locale.US);
    return localeResolver;
  }

}
