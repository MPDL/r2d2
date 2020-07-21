package de.mpg.mpdl.r2d2.util;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class DeleteSearchIndexExtension implements AfterEachCallback {

  @Override
  public void afterEach(ExtensionContext context) throws Exception {

    RestHighLevelClient elasticSearchClient =
        new RestHighLevelClient(RestClient.builder(HttpHost.create(SearchEngineLauncher.ELASTIC_SEARCH_CONTAINER.getHttpHostAddress())));

    //TODO: Maybe read settings and mappings from the resource files, instead of getting them from the already created indices!?

    GetIndexResponse getIndexResponse = elasticSearchClient.indices().get(new GetIndexRequest("_all"), RequestOptions.DEFAULT);
    String[] indices = getIndexResponse.getIndices();
    Map<String, List<AliasMetaData>> aliases = getIndexResponse.getAliases();
    Map<String, MappingMetaData> mappings = getIndexResponse.getMappings();
    Map<String, Settings> settings = getIndexResponse.getSettings();

    elasticSearchClient.indices().delete(new DeleteIndexRequest("_all"), RequestOptions.DEFAULT);

    for (String index : indices) {
      List<AliasMetaData> indexAliases = aliases.get(index);
      MappingMetaData indexMappingMetaData = mappings.get(index);
      Settings indexSettings = settings.get(index);

      //How to get the original settings of an index (without the auto-created setting: creation_date, provided_name, uuid, version.created)?
      //FIXME: Generalize index recreation: Do all indices have the following auto-created settings and the exact same setting-keys (like: index.creation_date)?

      //Remove all implicit/index-specific settings (before using the settings for recreation):
      Builder settingsBuilder = Settings.builder().put(indexSettings);
      settingsBuilder.remove("index.creation_date");
      settingsBuilder.remove("index.provided_name");
      settingsBuilder.remove("index.uuid");
      settingsBuilder.remove("index.version.created");
      indexSettings = settingsBuilder.build();

      CreateIndexRequest createIndexRequest = new CreateIndexRequest(index).settings(indexSettings);
      indexAliases.forEach(indexAlias -> createIndexRequest.alias(new Alias(indexAlias.alias())));
      createIndexRequest.mapping(indexMappingMetaData.getSourceAsMap());

      elasticSearchClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
    }

    elasticSearchClient.close();

  }

}
