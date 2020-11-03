package de.mpg.mpdl.r2d2.search.es.daoimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.search.dao.DatasetVersionDaoEs;
import de.mpg.mpdl.r2d2.search.model.DatasetVersionIto;

public abstract class DatasetVersionDaoImpl extends ElasticSearchGenericDAOImpl<DatasetVersionIto> implements DatasetVersionDaoEs {


  public static final String INDEX_DATASET_LATEST_VERSION = "dataset.latestVersion";
  public static final String INDEX_VERSION_NUMBER = "versionNumber";
  public static final String INDEX_STATE = "state";


  private static final Class<DatasetVersionIto> typeParameterClass = DatasetVersionIto.class;

  private static final String JOIN_FIELD_NAME = "joinField";

  private static final String[] SOURCE_EXCLUSIONS = new String[] {"joinField.name", "sort-metadata-creators-first",
      "sort-metadata-creators-compound", "sort-metadata-dates-by-category", "sort-metadata-dates-by-category-year"};

  public DatasetVersionDaoImpl(String indexName) {
    super(indexName, typeParameterClass);
  }

  @Override
  protected JsonNode applyCustomValues(DatasetVersionIto datasetVersion) {

    ObjectNode node = (ObjectNode) super.applyCustomValues(datasetVersion);

    /*
     * node.putObject(JOIN_FIELD_NAME).put("name", "item"); String[] creatorStrings
     * = createSortCreatorsString(item); node.put("sort-metadata-creators-first",
     * creatorStrings[0]); node.put("sort-metadata-creators-compound",
     * creatorStrings[1]); String firstDate = createSortMetadataDates(item); if
     * (firstDate != null) { node.put("sort-metadata-dates-by-category", firstDate);
     * node.put("sort-metadata-dates-by-category-year", firstDate.substring(0, 4));
     * }
     */
    return node;
  }

  @Override
  protected String[] getSourceExclusions() {
    return SOURCE_EXCLUSIONS;
  }

  /**
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @param vo
   * @return {@link String}
   */

  /*
   * public String createFulltext(String itemId, String fileId, byte[] file)
   * throws R2d2TechnicalException { try {
   * 
   * ObjectNode rootObject = mapper.createObjectNode();
   * rootObject.putObject("fileData").put("itemId", itemId).put("fileId",
   * fileId).put("data", Base64.getEncoder().encodeToString(file));
   * rootObject.putObject(JOIN_FIELD_NAME).put("name", "file").put("parent",
   * itemId);
   * 
   * IndexResponse indexResponse =
   * client.getClient().prepareIndex().setIndex(indexName).setType(indexType).
   * setRouting(itemId) .setPipeline("attachment").setId(itemId + "__" +
   * fileId).setSource(mapper.writeValueAsBytes(rootObject),
   * XContentType.JSON).get(); return indexResponse.getId();
   * 
   * } catch (Exception e) { throw new R2d2TechnicalException(e); }
   * 
   * 
   * }
   */

}
