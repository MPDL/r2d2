package de.mpg.mpdl.r2d2.db;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.util.BaseIntegrationTest;
import de.mpg.mpdl.r2d2.util.testdata.TestDataBuilder;

/**
 * Integration test for DatasetVersionRepository
 * 
 * @author helk
 *
 */
public class DatasetVersionRepositoryIT extends BaseIntegrationTest {

  @Autowired
  private TestDataBuilder testDataBuilder;

  @Autowired
  private DatasetVersionRepository datasetVersionRepository;

  @Test
  public void testGetLatestVersion() {
    //Given
    int latestVersionNumber = 2;

    Dataset dataset = testDataBuilder.newDataset().setcurrentCreationAndModificationDate().persist();
    testDataBuilder.newDatasetVersion().setDataset(dataset).setcurrentCreationAndModificationDate().persist();
    testDataBuilder.newDatasetVersion().setDataset(dataset).setcurrentCreationAndModificationDate().setVersionNumber(latestVersionNumber)
        .persist();

    //When
    DatasetVersion latestDatasetVersion = datasetVersionRepository.findLatestVersion(dataset.getId());

    //Then
    assertThat(latestDatasetVersion.getVersionNumber()).isEqualTo(latestVersionNumber);
  }

}
