package de.mpg.mpdl.r2d2.search.es.daoimpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.search.dao.GenericDaoEs;
import de.mpg.mpdl.r2d2.search.model.SearchQuery;
import de.mpg.mpdl.r2d2.search.model.SearchRecord;
import de.mpg.mpdl.r2d2.search.model.SearchResult;
import de.mpg.mpdl.r2d2.search.util.ElasticSearchIndexField;
import de.mpg.mpdl.r2d2.search.util.ElasticSearchIndexField.Type;

/**
 * ElasticSearchClient enables elasticsearch accessibility
 * 
 * @author frank (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public abstract class ElasticSearchGenericDAOImpl<E> implements GenericDaoEs<E> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchGenericDAOImpl.class);

  @Autowired
  protected RestHighLevelClient client;

  @Autowired
  protected ObjectMapper mapper;

  protected String indexName;

  protected Class<E> typeParameterClass;

  public static final int DEFAULT_SEARCH_SIZE = 100;
  public static final int MAX_SEARCH_SIZE = 10000;
  public static final int DEFAULT_SCROLL_TIME = 60000;

  public ElasticSearchGenericDAOImpl(String indexName, Class<E> typeParameterClass) {
    this.indexName = indexName;
    this.typeParameterClass = typeParameterClass;
  }

  protected JsonNode applyCustomValues(E entity) {
    JsonNode node = mapper.valueToTree(entity);
    return node;
  }

  protected abstract String[] getSourceExclusions();

  /**
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @param vo
   * @return {@link String}
   */
  public String create(String id, E entity) throws R2d2TechnicalException {
    try {

      IndexRequest ir = new IndexRequest(indexName).id(id).source(mapper.writeValueAsBytes(applyCustomValues(entity)), XContentType.JSON);
      IndexResponse indexResponse = client.index(ir, RequestOptions.DEFAULT);
      return indexResponse.getId();

    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }

  }

  /**
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @param vo
   * @return {@link String}
   */
  public String createImmediately(String id, E entity) throws R2d2TechnicalException {
    try {
      IndexRequest ir = new IndexRequest(indexName).id(id).source(mapper.writeValueAsBytes(applyCustomValues(entity)), XContentType.JSON)
          .setRefreshPolicy(RefreshPolicy.IMMEDIATE);
      IndexResponse indexResponse = client.index(ir, RequestOptions.DEFAULT);
      return indexResponse.getId();

    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }

  }

  /**
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @return {@link ValueObject}
   */
  public E get(String id) throws R2d2TechnicalException {
    try {
      GetRequest getRequest = new GetRequest(indexName, id);
      GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
      return mapper.readValue(getResponse.getSourceAsBytes(), typeParameterClass);
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }

  }

  /**
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @param vo
   * @return {@link String}
   */
  public String updateImmediately(String id, E entity) throws R2d2TechnicalException {
    try {
      UpdateRequest updateRequest = new UpdateRequest(indexName, id).setRefreshPolicy(RefreshPolicy.IMMEDIATE);
      updateRequest.doc(mapper.writeValueAsBytes(entity), XContentType.JSON);
      UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
      return Long.toString(updateResponse.getVersion());
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }

  }

  public String update(String id, E entity) throws R2d2TechnicalException {
    try {
      UpdateRequest updateRequest = new UpdateRequest(indexName, id);
      UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
      return Long.toString(updateResponse.getVersion());
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }

  }

  /**
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @return {@link String}
   */
  public String deleteImmediatly(String id) throws R2d2TechnicalException {
    try {
      DeleteRequest deleteRequest = new DeleteRequest(indexName, id).setRefreshPolicy(RefreshPolicy.IMMEDIATE);
      DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
      return deleteResponse.getId();
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }
  }

  /**
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @return {@link String}
   */
  public String delete(String id) throws R2d2TechnicalException {
    try {
      DeleteRequest deleteRequest = new DeleteRequest(indexName, id);
      DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
      return deleteResponse.getId();
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }
  }

  public long deleteByQuery(QueryBuilder query) throws R2d2TechnicalException {
    try {
      DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(indexName).setQuery(query);
      BulkByScrollResponse resp = client.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
      return resp.getDeleted();
    } catch (Exception e) {
      throw new R2d2TechnicalException(e);
    }

  }

  public SearchResult<E> search(SearchQuery searchQuery) throws R2d2TechnicalException {

    SearchResponse searchResponse;
    try {

      if (searchQuery.getScrollId() != null) {
        searchResponse = scrollOn(searchQuery.getScrollId(), DEFAULT_SCROLL_TIME);
      } else {
        SearchRequest sr = new SearchRequest(indexName);
        SearchSourceBuilder ssb = new SearchSourceBuilder();

        if (searchQuery.isScroll()) {
          sr.scroll(new Scroll(new TimeValue(DEFAULT_SCROLL_TIME)));
        }

        if (searchQuery.getQuery() != null) {
          ssb.query(QueryBuilders.queryStringQuery(searchQuery.getQuery()));
        }

        if (searchQuery.getFrom() != 0) {
          ssb.from(searchQuery.getFrom());
        }

        if (searchQuery.getSize() == -1) {
          ssb.size(ElasticSearchGenericDAOImpl.DEFAULT_SEARCH_SIZE);
        } else if (searchQuery.getSize() > ElasticSearchGenericDAOImpl.MAX_SEARCH_SIZE) {
          ssb.size(ElasticSearchGenericDAOImpl.MAX_SEARCH_SIZE);
        } else {
          ssb.size(searchQuery.getSize());
        }

        if (searchQuery.getSort() != null) {
          ssb.sort(searchQuery.getSort());
        }

        if (getSourceExclusions() != null) {
          ssb.fetchSource(null, getSourceExclusions());
        }

        LOGGER.debug(ssb.toString());
        searchResponse = client.search(sr, RequestOptions.DEFAULT);
      }

      return getSearchRetrieveResponseFromElasticSearchResponse(searchResponse, typeParameterClass);
    } catch (Exception e) {
      throw new R2d2TechnicalException(e.getMessage(), e);
    }

  }

  public SearchResponse searchDetailed(SearchSourceBuilder ssb, long scrollTime) throws R2d2TechnicalException {

    try {
      SearchRequest searchRequest = new SearchRequest(indexName).source(ssb);

      if (scrollTime != -1) {
        searchRequest.scroll(new Scroll(new TimeValue(scrollTime)));
      }

      if (getSourceExclusions() != null && getSourceExclusions().length > 0) {
        if (ssb.fetchSource() == null) {
          ssb.fetchSource(null, getSourceExclusions());
        } else if (ssb.fetchSource().fetchSource()) {
          String[] excludes = ssb.fetchSource().excludes();
          String[] both = Stream
              .concat(Arrays.stream(getSourceExclusions()), (excludes != null ? Arrays.stream(excludes) : Arrays.stream(new String[0])))
              .toArray(String[]::new);
          ssb.fetchSource(ssb.fetchSource().includes(), both);
        }
      }

      LOGGER.debug(searchRequest.toString());
      return client.search(searchRequest, RequestOptions.DEFAULT);
    } catch (Exception e) {
      throw new R2d2TechnicalException(e.getMessage(), e);
    }

  }

  public SearchResponse searchDetailed(SearchSourceBuilder ssb) throws R2d2TechnicalException {

    return searchDetailed(ssb, -1);

  }

  public SearchResponse scrollOn(String scrollId, long scrollTime) throws R2d2TechnicalException {

    try {
      SearchScrollRequest ssr = new SearchScrollRequest(scrollId).scroll(new Scroll(new TimeValue(scrollTime)));
      return client.scroll(ssr, RequestOptions.DEFAULT);
    } catch (Exception e) {
      throw new R2d2TechnicalException(e.getMessage(), e);
    }

  }

  public SearchResult<E> getSearchRetrieveResponseFromElasticSearchResponse(SearchResponse sr, Class<E> clazz) throws IOException {
    SearchResult<E> srrVO = new SearchResult<E>();
    // srrVO.setOriginalResponse(sr);
    srrVO.setTotal((int) sr.getHits().getTotalHits().value);
    srrVO.setScrollId(sr.getScrollId());

    List<SearchRecord<E>> hitList = new ArrayList<>();
    srrVO.setHits(hitList);
    for (SearchHit hit : sr.getHits().getHits()) {
      SearchRecord<E> srr = new SearchRecord<E>();
      hitList.add(srr);

      E data = mapper.readValue(hit.getSourceAsString(), clazz);

      srr.setSource(data);
      srr.setId(hit.getId());
    }

    return srrVO;
  }

  // SP: Alias-Suche funktioniert in ES 6.1 nicht mehr wie erwartet
  public Map<String, ElasticSearchIndexField> getIndexFields() throws R2d2TechnicalException {
    // String realIndexName = indexName;
    //
    // GetAliasesResponse aliasResp =
    // client.getElasticSearchClient().admin().indices().prepareGetAliases(indexName).get();
    // if (!aliasResp.getAliases().isEmpty()) {
    // realIndexName = aliasResp.getAliases().keys().iterator().next().value;
    // }

    // GetMappingsResponse resp =
    // client.getElasticSearchClient().admin().indices().prepareGetMappings(realIndexName).addTypes(indexType).get();
    try {
      GetMappingsRequest getMappingRequest = new GetMappingsRequest().indices(indexName);

      GetMappingsResponse resp = this.client.indices().getMapping(getMappingRequest, RequestOptions.DEFAULT);

      if (resp.mappings().isEmpty() == false) { // SP: avoiding NullPointerException
        MappingMetaData mmd = resp.mappings().get(indexName);

        Map<String, ElasticSearchIndexField> map = ElasticSearchIndexField.Factory.createIndexMapFromElasticsearch(mmd);
        ElasticSearchIndexField allField = new ElasticSearchIndexField();
        allField.setIndexName("_all");
        allField.setType(Type.TEXT);
        map.put("_all", allField);
        return map;
      }
    } catch (IOException e) {
      throw new R2d2TechnicalException(e.getMessage(), e);
    }

    return new HashMap<String, ElasticSearchIndexField>();
  }

}
