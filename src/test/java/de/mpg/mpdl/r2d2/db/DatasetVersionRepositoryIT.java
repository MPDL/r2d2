package de.mpg.mpdl.r2d2.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.util.BaseIntegrationTest;
import de.mpg.mpdl.r2d2.util.Utils;

/**
 * Integration test for DatasetVersionRepository
 * 
 * @author helk
 *
 */
public class DatasetVersionRepositoryIT extends BaseIntegrationTest {

  @Autowired
  private DatasetVersionRepository datasetVersionRepository;

  @Test
  public void testGetLatestVersion() {
    //Given
    OffsetDateTime currentDateTime = Utils.generateCurrentDateTimeForDatabase();

    Dataset dataset = new Dataset();
    dataset.setId(UUID.randomUUID());
    dataset.setCreationDate(currentDateTime);
    dataset.setModificationDate(currentDateTime);

    DatasetVersion datasetVersion1 = new DatasetVersion();
    datasetVersion1.setCreationDate(currentDateTime);
    datasetVersion1.setModificationDate(currentDateTime);
    datasetVersion1.setDataset(dataset);

    DatasetVersion datasetVersion2 = new DatasetVersion();
    datasetVersion2.setCreationDate(currentDateTime);
    datasetVersion2.setModificationDate(currentDateTime);
    datasetVersion2.setDataset(dataset);

    int latestVersionNumber = 2;
    datasetVersion2.setVersionNumber(latestVersionNumber);

    datasetVersionRepository.saveAll(Arrays.asList(datasetVersion1, datasetVersion2));

    //When
    DatasetVersion latestDatasetVersion = datasetVersionRepository.findLatestVersion(dataset.getId());

    //Then
    assertThat(latestDatasetVersion.getVersionNumber()).isEqualTo(latestVersionNumber);
  }

}
