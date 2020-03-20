package de.mpg.mpdl.r2d2.search.es.connector;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;

@Component
public class ElasticSearchAdminController {

  @Autowired
  private RestHighLevelClient client;

  /**
   * Create a new index
   * 
   * @param indexName
   */
  public boolean createIndex(String indexName, String alias) throws R2d2TechnicalException {

    try {
      GetIndexRequest request = new GetIndexRequest(indexName);

      boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);

      if (exists) {
        deleteIndex(indexName);
      }

      CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
      CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
      return createIndexResponse.isAcknowledged();

    } catch (IOException e) {
      throw new R2d2TechnicalException(e);
    }
  }

  /**
   * delete an existing index
   * 
   * @param index
   * @return boolean
   */
  public boolean deleteIndex(String index) throws R2d2TechnicalException {

    try {
      DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(index);
      AcknowledgedResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
      return deleteIndexResponse.isAcknowledged();
    } catch (IOException e) {
      throw new R2d2TechnicalException(e);
    }
  }


}
