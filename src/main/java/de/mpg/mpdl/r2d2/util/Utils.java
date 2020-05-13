package de.mpg.mpdl.r2d2.util;

import java.io.IOException;
import java.security.Principal;
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

  public static OffsetDateTime generateCurrentDateTimeForDatabase() {
    return OffsetDateTime.now().truncatedTo(ChronoUnit.MICROS);
  }

}
