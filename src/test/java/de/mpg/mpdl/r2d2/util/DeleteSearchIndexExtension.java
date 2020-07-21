package de.mpg.mpdl.r2d2.util;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class DeleteSearchIndexExtension implements AfterEachCallback {

  @Override
  public void afterEach(ExtensionContext context) throws Exception {

    RestHighLevelClient elasticSearchClient =
        new RestHighLevelClient(RestClient.builder(HttpHost.create(SearchEngineLauncher.ELASTIC_SEARCH_CONTAINER.getHttpHostAddress())));
    //Alternatively get the HostAddress from the environment/contex prperty: elasticsearch.url

    //TODO: Delete and recreate all indices, instead of delete all data of the indices (which is slower)

    GetIndexResponse getIndexResponse = elasticSearchClient.indices().get(new GetIndexRequest("_all"), RequestOptions.DEFAULT);
    DeleteByQueryRequest deleteAllDocumentsInAllIndicesRequest = new DeleteByQueryRequest(getIndexResponse.getIndices());
    deleteAllDocumentsInAllIndicesRequest.setQuery(QueryBuilders.matchAllQuery());
    elasticSearchClient.deleteByQuery(deleteAllDocumentsInAllIndicesRequest, RequestOptions.DEFAULT);

    //FIXME: The deletion runs asynchronously: Wait for the deletion of the indices to finish 
    //(By waiting for the deletion task to complete or by waiting for the elastic search index status)

    elasticSearchClient.close();

  }

}
