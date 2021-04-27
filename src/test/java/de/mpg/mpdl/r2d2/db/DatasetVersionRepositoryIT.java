package de.mpg.mpdl.r2d2.db;

import de.mpg.mpdl.r2d2.model.*;
import de.mpg.mpdl.r2d2.util.*;
import de.mpg.mpdl.r2d2.util.testdata.TestDataManager;
import de.mpg.mpdl.r2d2.util.testdata.TestDataFactory;
import de.mpg.mpdl.r2d2.util.testdata.builder.DatasetVersionMetadataBuilder;
import de.mpg.mpdl.r2d2.util.testdata.builder.GeolocationBuilder;
import de.mpg.mpdl.r2d2.util.testdata.builder.PersonBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for DatasetVersionRepository
 *
 * @author helk
 *
 */
public class DatasetVersionRepositoryIT extends BaseIntegrationTest {

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private TestDataManager testDataManager;

  @Autowired
  private DatasetVersionRepository datasetVersionRepository;

  @Test
  public void testFindLatestVersion() {
    //Given
    int latestVersionNumber = 2;

    Dataset dataset = TestDataFactory.aDatasetWithCreationAndModificationDate().build();
    DatasetVersion datasetVersion1 = TestDataFactory.aDatasetVersionWithCreationAndModificationDate().dataset(dataset).build();
    DatasetVersion datasetVersion2 =
        TestDataFactory.aDatasetVersionWithCreationAndModificationDate().dataset(dataset).versionNumber(latestVersionNumber).build();

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
    Dataset dataset = TestDataFactory.aDatasetWithCreationAndModificationDate().build();
    DatasetVersion datasetVersion =
        TestDataFactory.aDatasetVersionWithCreationAndModificationDate().dataset(dataset).metadata(metadata).build();

    //When
    datasetVersionRepository.save(datasetVersion);

    //Then
    List<DatasetVersion> datasetVersions =
        entityManager.createQuery("Select datasetVersion from " + DatasetVersion.class.getSimpleName() + " datasetVersion").getResultList();

    assertThat(datasetVersions).hasSize(1);

    DatasetVersionMetadata returnedMetadata = datasetVersions.stream().findFirst().get().getMetadata();
    assertThat(returnedMetadata).isNotNull();
    assertThat(returnedMetadata.getTitle()).isEqualTo(title);
    assertThat(returnedMetadata.getAuthors()).isNotNull();
    assertThat(returnedMetadata.getDescription()).isEqualTo(description);
    assertThat(returnedMetadata.getGeolocation().getLatitude()).isEqualTo(latitude);
  }

}
