package de.mpg.mpdl.r2d2.db;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.util.BaseIntegrationTest;
import de.mpg.mpdl.r2d2.util.testdata.EntityManagerWrapper;
import de.mpg.mpdl.r2d2.util.testdata.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for DatasetVersionRepository
 *
 * @author helk
 *
 */
public class DatasetVersionRepositoryIT extends BaseIntegrationTest {

  @Autowired
  private EntityManagerWrapper entityManagerWrapper;

  @Autowired
  private DatasetVersionRepository datasetVersionRepository;

  @Test
  public void testFindLatestVersion() {
    //Given
    int latestVersionNumber = 2;

    Dataset dataset = TestDataFactory.newDatasetWithCreationAndModificationDate();
    DatasetVersion datasetVersion1 =
        TestDataFactory.newDatasetVersionWithCreationAndModificationDate().toBuilder().dataset(dataset).build();
    DatasetVersion datasetVersion2 = TestDataFactory.newDatasetVersionWithCreationAndModificationDate().toBuilder().dataset(dataset)
        .versionNumber(latestVersionNumber).build();

    entityManagerWrapper.persist(dataset);
    entityManagerWrapper.merge(datasetVersion1);
    entityManagerWrapper.merge(datasetVersion2);

    //When
    DatasetVersion latestDatasetVersion = datasetVersionRepository.findLatestVersion(dataset.getId());

    //Then
    assertThat(latestDatasetVersion.getVersionNumber()).isEqualTo(latestVersionNumber);
  }

}
