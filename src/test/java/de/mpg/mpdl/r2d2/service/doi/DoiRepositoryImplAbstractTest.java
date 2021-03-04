package de.mpg.mpdl.r2d2.service.doi;

import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.util.testdata.builder.DatasetBuilder;
import de.mpg.mpdl.r2d2.util.testdata.builder.DatasetVersionBuilder;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class DoiRepositoryImplAbstractTest {

  static Logger LOGGER;

  DoiRepositoryImpl doiRepository;

  @Test
  void testCreateDraftDoi() throws IOException, InterruptedException, R2d2TechnicalException {
    //Given

    UUID uuid = UUID.randomUUID();
    DatasetVersion datasetVersion = DatasetVersionBuilder.aDatasetVersion().dataset(DatasetBuilder.aDataset().id(uuid).build()).build();

    //When
    String response = this.doiRepository.createDraftDoi(datasetVersion);

    //Then
    assertThat(response).isNotNull();
    //TODO: Add further appropriate assertions

    LOGGER.info("Create Draft-Doi response: " + response);
  }

  @Test
  void testUpdateToFindableDoi() {
    //Given

    //When

    //Then

  }

}
