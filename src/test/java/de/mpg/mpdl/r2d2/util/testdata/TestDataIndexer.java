package de.mpg.mpdl.r2d2.util.testdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.search.model.DatasetVersionIto;
import de.mpg.mpdl.r2d2.search.model.FileIto;
import de.mpg.mpdl.r2d2.util.DtoMapper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

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
    //TODO: Add indexing for Affiliations?
  }

  private String indexDataset(DatasetVersion datasetVersion) throws R2d2TechnicalException {
    //TODO: In which case should "index.dataset.latest.name" ("LatestDatasetVersionDaoImpl") be used?
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

}
