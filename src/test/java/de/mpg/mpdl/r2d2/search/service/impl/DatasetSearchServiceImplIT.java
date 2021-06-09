package de.mpg.mpdl.r2d2.search.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.search.model.DatasetVersionIto;
import de.mpg.mpdl.r2d2.search.model.SearchQuery;
import de.mpg.mpdl.r2d2.search.model.SearchRecord;
import de.mpg.mpdl.r2d2.search.model.SearchResult;
import de.mpg.mpdl.r2d2.util.BaseIntegrationTest;
import de.mpg.mpdl.r2d2.util.testdata.TestDataFactory;
import de.mpg.mpdl.r2d2.util.testdata.TestDataIndexer;

public class DatasetSearchServiceImplIT extends BaseIntegrationTest {

  @Autowired
  private DatasetSearchServiceImpl datasetSearchServiceImpl;

  @Autowired
  private TestDataIndexer testDataIndexer;

  @Test
  void testSearchWithEmptyQuery() throws R2d2TechnicalException, AuthorizationException {
    //Given
    Dataset dataset = TestDataFactory.aDataset().id(UUID.randomUUID()).build();
    Dataset dataset2 = TestDataFactory.aDataset().id(UUID.randomUUID()).build();
    DatasetVersion datasetVersion = TestDataFactory.aDatasetVersion().dataset(dataset).state(Dataset.State.PUBLIC).build();
    DatasetVersion datasetVersion2 = TestDataFactory.aDatasetVersion().dataset(dataset2).state(Dataset.State.PUBLIC).build();

    this.testDataIndexer.index(datasetVersion, datasetVersion2);

    SearchQuery searchQuery = new SearchQuery();

    //When
    SearchResult<DatasetVersionIto> searchResult = datasetSearchServiceImpl.search(searchQuery, false, null);

    //Then
    assertThat(searchResult).isNotNull().extracting(SearchResult::getTotal).isEqualTo(2);
    assertThat(searchResult.getHits())
        .flatExtracting((Function<? super SearchRecord<DatasetVersionIto>, ?>) searRecord -> searRecord.getSource().getId())
        .containsOnly(datasetVersion.getId(), datasetVersion2.getId());
  }

  @Test
  void testSearchReturnsAuthorized() throws R2d2TechnicalException, AuthorizationException {
    //Given
    UserAccount userAccount = TestDataFactory.anUser().id(UUID.randomUUID()).build();
    R2D2Principal r2d2Principal = TestDataFactory.aR2D2Principal().userAccount(userAccount).build();

    Dataset dataset = TestDataFactory.aDataset().id(UUID.randomUUID()).creator(userAccount).build();
    Dataset dataset2 = TestDataFactory.aDataset().id(UUID.randomUUID()).build();
    Dataset dataset3 = TestDataFactory.aDataset().id(UUID.randomUUID()).build();
    DatasetVersion datasetVersion = TestDataFactory.aDatasetVersion().dataset(dataset).build();
    DatasetVersion datasetVersion2 = TestDataFactory.aDatasetVersion().dataset(dataset2).state(Dataset.State.PUBLIC).build();
    DatasetVersion datasetVersion3 = TestDataFactory.aDatasetVersion().dataset(dataset3).state(Dataset.State.PRIVATE).build();

    this.testDataIndexer.index(datasetVersion, datasetVersion2, datasetVersion3);

    SearchQuery searchQuery = new SearchQuery();

    //When
    SearchResult<DatasetVersionIto> searchResult = datasetSearchServiceImpl.search(searchQuery, false, r2d2Principal);

    //Then
    assertThat(searchResult).isNotNull().extracting(SearchResult::getTotal).isEqualTo(2);
    assertThat(searchResult.getHits())
        .flatExtracting((Function<? super SearchRecord<DatasetVersionIto>, ?>) searRecord -> searRecord.getSource().getId())
        .containsOnly(datasetVersion.getId(), datasetVersion2.getId());
  }

  @Test
  void testSearchMineOnly() throws R2d2TechnicalException, AuthorizationException {
    //Given
    UserAccount userAccount = TestDataFactory.anUser().id(UUID.randomUUID()).build();
    R2D2Principal r2d2Principal = TestDataFactory.aR2D2Principal().userAccount(userAccount).build();

    Dataset dataset = TestDataFactory.aDataset().id(UUID.randomUUID()).creator(userAccount).build();
    Dataset dataset2 = TestDataFactory.aDataset().id(UUID.randomUUID()).build();
    Dataset dataset3 = TestDataFactory.aDataset().id(UUID.randomUUID()).build();
    DatasetVersion datasetVersion = TestDataFactory.aDatasetVersion().dataset(dataset).build();
    DatasetVersion datasetVersion2 = TestDataFactory.aDatasetVersion().dataset(dataset2).state(Dataset.State.PUBLIC).build();
    DatasetVersion datasetVersion3 = TestDataFactory.aDatasetVersion().dataset(dataset3).state(Dataset.State.PRIVATE).build();

    this.testDataIndexer.index(datasetVersion, datasetVersion2, datasetVersion3);

    SearchQuery searchQuery = new SearchQuery();

    //When
    SearchResult<DatasetVersionIto> searchResult = datasetSearchServiceImpl.search(searchQuery, true, r2d2Principal);

    //Then
    assertThat(searchResult).isNotNull().extracting(SearchResult::getTotal).isEqualTo(1);
    assertThat(searchResult.getHits())
        .flatExtracting((Function<? super SearchRecord<DatasetVersionIto>, ?>) searRecord -> searRecord.getSource().getId())
        .containsOnly(datasetVersion.getId());
  }

  @Test
  void testSearchWithQuery() throws R2d2TechnicalException, AuthorizationException {
    //Given
    UserAccount userAccount = TestDataFactory.anUser().id(UUID.randomUUID()).build();
    R2D2Principal r2d2Principal = TestDataFactory.aR2D2Principal().userAccount(userAccount).build();

    Dataset dataset = TestDataFactory.aDataset().id(UUID.randomUUID()).creator(userAccount).build();
    Dataset dataset2 = TestDataFactory.aDataset().id(UUID.randomUUID()).creator(userAccount).build();
    DatasetVersion datasetVersion = TestDataFactory.aDatasetVersion().dataset(dataset).build();
    DatasetVersion datasetVersion2 = TestDataFactory.aDatasetVersion().dataset(dataset2).build();

    this.testDataIndexer.index(datasetVersion, datasetVersion2);

    SearchQuery searchQuery = new SearchQuery();
    String queryStringQuery = "id:" + dataset.getId();
    searchQuery.setQuery(queryStringQuery);

    //When
    SearchResult<DatasetVersionIto> searchResult = datasetSearchServiceImpl.search(searchQuery, false, r2d2Principal);

    //Then
    assertThat(searchResult).isNotNull().extracting(SearchResult::getTotal).isEqualTo(1);
    assertThat(searchResult.getHits())
        .flatExtracting((Function<? super SearchRecord<DatasetVersionIto>, ?>) searRecord -> searRecord.getSource().getId())
        .containsExactly(datasetVersion.getId());
  }

}
