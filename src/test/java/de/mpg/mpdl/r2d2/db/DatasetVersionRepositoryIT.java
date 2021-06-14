package de.mpg.mpdl.r2d2.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.DatasetVersionMetadata;
import de.mpg.mpdl.r2d2.model.Geolocation;
import de.mpg.mpdl.r2d2.util.R2D2IntegrationTest;
import de.mpg.mpdl.r2d2.util.testdata.TestDataFactory;
import de.mpg.mpdl.r2d2.util.testdata.TestDataManager;
import de.mpg.mpdl.r2d2.util.testdata.builder.DatasetVersionMetadataBuilder;
import de.mpg.mpdl.r2d2.util.testdata.builder.GeolocationBuilder;
import de.mpg.mpdl.r2d2.util.testdata.builder.PersonBuilder;

/**
 * Integration test for DatasetVersionRepository
 *
 * @author helk
 *
 */
@R2D2IntegrationTest
public class DatasetVersionRepositoryIT {

  @Autowired
  private TestDataManager testDataManager;

  @Autowired
  private DatasetVersionRepository datasetVersionRepository;

  @Test
  public void testFindLatestVersion() {
    //Given
    int latestVersionNumber = 2;

    Dataset dataset = TestDataFactory.aDataset().build();
    DatasetVersion datasetVersion1 = TestDataFactory.aDatasetVersion().dataset(dataset).build();
    DatasetVersion datasetVersion2 = TestDataFactory.aDatasetVersion().dataset(dataset).versionNumber(latestVersionNumber).build();

    testDataManager.persist(datasetVersion1, datasetVersion2);

    //When
    DatasetVersion latestDatasetVersion = datasetVersionRepository.findLatestVersion(dataset.getId());

    //Then
    assertThat(latestDatasetVersion.getVersionNumber()).isEqualTo(latestVersionNumber);
  }

  @Test
  public void testSaveDatasetVersionWithMetadata() {
    //Given
    String title = "Title";
    String description = "Description";
    double latitude = 1.2;

    Geolocation geolocation = GeolocationBuilder.aGeolocation().latitude(latitude).build();

    DatasetVersionMetadata metadata = DatasetVersionMetadataBuilder.aDatasetVersionMetadata().title(title)
        .authors(Arrays.asList(PersonBuilder.aPerson().build())).description(description).geolocation(geolocation).build();
    Dataset dataset = TestDataFactory.aDataset().build();
    DatasetVersion datasetVersion = TestDataFactory.aDatasetVersion().dataset(dataset).metadata(metadata).build();

    //When
    datasetVersionRepository.save(datasetVersion);

    //Then
    List<DatasetVersion> datasetVersions = testDataManager.findAll(DatasetVersion.class);

    assertThat(datasetVersions).hasSize(1);

    DatasetVersionMetadata returnedMetadata = datasetVersions.stream().findFirst().get().getMetadata();
    assertThat(returnedMetadata).isNotNull();
    assertThat(returnedMetadata.getTitle()).isEqualTo(title);
    assertThat(returnedMetadata.getAuthors()).isNotNull();
    assertThat(returnedMetadata.getDescription()).isEqualTo(description);
    assertThat(returnedMetadata.getGeolocation().getLatitude()).isEqualTo(latitude);
  }

}
