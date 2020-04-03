package de.mpg.mpdl.r2d2.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.util.BaseIntegrationTest;

public class DatasetVersionRepositoryTest extends BaseIntegrationTest {

  @Autowired
  private DatasetVersionRepository datasetVersionRepository;

  @Test
  public void testGetLatestVersion() {
    //Given
    Dataset dataset = new Dataset();
    DatasetVersion datasetVersion1 = new DatasetVersion();
    datasetVersion1.setDataset(dataset);

    DatasetVersion datasetVersion2 = new DatasetVersion();
    datasetVersion2.setDataset(dataset);
    int latestVersionNumber = 2;
    datasetVersion2.setVersionNumber(latestVersionNumber);

    datasetVersionRepository.saveAll(Arrays.asList(datasetVersion1, datasetVersion2));

    //When
    DatasetVersion latestDatasetVersion = datasetVersionRepository.getLatestVersion(dataset.getId());

    //Then
    assertThat(latestDatasetVersion.getVersionNumber()).isEqualTo(latestVersionNumber);
  }

}
