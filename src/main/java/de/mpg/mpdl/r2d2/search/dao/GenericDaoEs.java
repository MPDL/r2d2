package de.mpg.mpdl.r2d2.search.dao;

import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;



import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.search.model.SearchQuery;
import de.mpg.mpdl.r2d2.search.model.SearchResult;
import de.mpg.mpdl.r2d2.search.util.ElasticSearchIndexField;

/**
 * Generic Dao interface for elasticsearch
 * 
 * @author haarlaender
 * 
 * @param <E>
 * @param <Query>
 */
public interface GenericDaoEs<E> {

  /**
   * creates a new object in elasticsearch for the entity with a specific id
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @param vo
   * @return {@link String}
   */
  public String createImmediately(String id, E entity) throws R2d2TechnicalException;

  public String create(String id, E entity) throws R2d2TechnicalException;

  /**
   * retrieves the object from elasticsearch for a given id
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @return {@link ValueObject}
   */
  public E get(String id) throws R2d2TechnicalException;

  /**
   * updates the object with the given id and the new entity in elasticsearch
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @param vo
   * @return {@link String}
   */
  public String updateImmediately(String id, E entity) throws R2d2TechnicalException;

  public String update(String id, E entity) throws R2d2TechnicalException;

  /**
   * deletes the object with the given id in elasticsearch
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @return {@link String}
   */
  public String deleteImmediatly(String id) throws R2d2TechnicalException;

  public String delete(String id) throws R2d2TechnicalException;

  public long deleteByQuery(QueryBuilder query) throws R2d2TechnicalException;


  /**
   * searches in elasticsearch with a given searchQuery
   * 
   * @param searchQuery
   * @return
   * @throws IngeTechnicalException
   */
  public SearchResult<E> search(SearchQuery searchQuery) throws R2d2TechnicalException;



  public SearchResponse searchDetailed(SearchSourceBuilder ssb) throws R2d2TechnicalException;

  public SearchResponse searchDetailed(SearchSourceBuilder ssb, long scrollTime) throws R2d2TechnicalException;

  public SearchResponse scrollOn(String scrollId, long scrollTime) throws R2d2TechnicalException;


  /**
   * Retrieves the mapping for the index and transforms it into a map of ElasticSearchIndexField
   * objects, including information about the field name, its type and the nested path
   * 
   * @return
   * @throws IngeTechnicalException
   */
  public Map<String, ElasticSearchIndexField> getIndexFields() throws R2d2TechnicalException;

}
