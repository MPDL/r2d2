package de.mpg.mpdl.r2d2.util.testdata;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.search.model.DatasetVersionIto;
import de.mpg.mpdl.r2d2.search.model.FileIto;
import de.mpg.mpdl.r2d2.util.DtoMapper;

/**
 * Class to search and index Test Data objects.
 * <p>
 * Wrapper class for the Elasticsearch RestHighLevelClient.
 */
@Component
public class TestDataIndexer {

  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  protected RestHighLevelClient client;

  @Autowired
  private DtoMapper dtoMapper;

  @Autowired
  private Environment env;

  //TODO: Add search/indexing for: index.dataset.latest, index.affiliation !?

  /**
   * Writes the given objects in the appropriate Index.
   * 
   * @param objectsToIndex objects to be indexed
   * @throws R2d2TechnicalException
   */
  public void index(Object... objectsToIndex) throws R2d2TechnicalException {
    for (Object objectToIndex : objectsToIndex) {
      this.index(objectToIndex);
    }
  }

  private <E> String index(E entity) throws R2d2TechnicalException {
    if (entity.getClass() == DatasetVersion.class) {
      return this.indexDataset((DatasetVersion) entity);
    } else if (entity.getClass() == File.class) {
      return this.indexFile((File) entity);
    } else {
      throw new IllegalArgumentException("No index for: " + entity.getClass());
    }
  }

  private String indexDataset(DatasetVersion datasetVersion) throws R2d2TechnicalException {
    String indexName = env.getProperty("index.dataset.public.name");
    String id = datasetVersion.getVersionId().toString();
    DatasetVersionIto datasetVersionIto = dtoMapper.convertToDatasetVersionIto(datasetVersion);

    return this.index(indexName, id, datasetVersionIto);
  }

  private String indexFile(File file) throws R2d2TechnicalException {
    String indexName = env.getProperty("index.file.name");
    String id = file.getId().toString();
    FileIto fileIto = dtoMapper.convertToFileIto(file);

    return this.index(indexName, id, fileIto);
  }

  private <E> String index(String indexName, String id, E entity) throws R2d2TechnicalException {
    try {
      IndexRequest indexRequest =
          new IndexRequest(indexName).id(id).source(objectMapper.writeValueAsBytes(objectMapper.valueToTree(entity)), XContentType.JSON)
              .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
      IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);

      return indexResponse.getId();
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }
  }

  /**
   * Search the appropriate Index for all entities of the specified class.
   * 
   * @param itoClass the index-transfer-object class of the entities
   * @param <I> the (ito) class type
   * @return a List of all indexed entities of type I
   * @throws R2d2TechnicalException
   */
  public <I> List<I> searchAll(Class<I> itoClass) throws R2d2TechnicalException {
    String indexName;
    if (itoClass == DatasetVersionIto.class) {
      indexName = env.getProperty("index.dataset.public.name");
    } else if (itoClass == FileIto.class) {
      indexName = env.getProperty("index.file.name");
    } else {
      throw new IllegalArgumentException("No index for: " + itoClass);
    }

    try {
      SearchRequest searchRequest = new SearchRequest(indexName);
      SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
      searchSourceBuilder.query(QueryBuilders.matchAllQuery());
      searchRequest.source(searchSourceBuilder);

      SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

      List<I> entities = new ArrayList<>();
      for (SearchHit searchHit : searchResponse.getHits().getHits()) {
        I entity = objectMapper.readValue(searchHit.getSourceAsString(), itoClass);
        entities.add(entity);
      }

      return entities;
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }
  }

}
