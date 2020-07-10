package de.mpg.mpdl.r2d2.util;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class DeleteSearchIndexExtension implements AfterEachCallback {

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    RestHighLevelClient elasticSearchClient =
        new RestHighLevelClient(RestClient.builder(HttpHost.create(SearchEngineLauncher.ELASTIC_SEARCH_CONTAINER.getHttpHostAddress())));

    elasticSearchClient.indices().delete(new DeleteIndexRequest("_all"), RequestOptions.DEFAULT);

    elasticSearchClient.close();
  }

}
