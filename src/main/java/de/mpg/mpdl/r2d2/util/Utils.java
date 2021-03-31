package de.mpg.mpdl.r2d2.util;

import java.io.IOException;
import java.security.Principal;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.DeprecationHandler;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchModule;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;

public class Utils {

  public static R2D2Principal toCustomPrincipal(Principal p) {
    if (p != null) {
      return (R2D2Principal) ((UsernamePasswordAuthenticationToken) p).getPrincipal();
    }

    return null;
  }


  public static SearchSourceBuilder parseJsonToSearchSourceBuilder(String json) throws IOException {
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    SearchModule searchModule = new SearchModule(Settings.EMPTY, false, Collections.emptyList());
    try (XContentParser parser = XContentFactory.xContent(XContentType.JSON)
        .createParser(new NamedXContentRegistry(searchModule.getNamedXContents()), DeprecationHandler.THROW_UNSUPPORTED_OPERATION, json)) {
      searchSourceBuilder.parseXContent(parser);
    }
    return searchSourceBuilder;

  }


  //Truncate to microseconds, as the database doesn't support nanoseconds
  public static OffsetDateTime generateCurrentDateTimeForDatabase() {
    return OffsetDateTime.now().truncatedTo(ChronoUnit.MICROS);
  }
  
  
  static final String tokenChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  static SecureRandom secureRandom = new SecureRandom();

  /** Create a secure random string for tokens, eg. review tokens **/
  public static String randomString(int len){
     StringBuilder sb = new StringBuilder(len);
     for(int i = 0; i < len; i++)
        sb.append(tokenChars.charAt(secureRandom.nextInt(tokenChars.length())));
     return sb.toString();
  }

}
