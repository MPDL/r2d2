package de.mpg.mpdl.r2d2.service.doi;

import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.util.testdata.builder.DatasetBuilder;
import de.mpg.mpdl.r2d2.util.testdata.builder.DatasetVersionBuilder;
import de.mpg.mpdl.r2d2.util.testdata.builder.DatasetVersionMetadataBuilder;
import de.mpg.mpdl.r2d2.util.testdata.builder.PersonBuilder;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class DoiRepositoryImplAbstractTest {

  static Logger LOGGER;

  DoiRepositoryImpl doiRepository;

  DatasetVersion datasetVersion;
  String doi;

  @Test
  @Order(1)
  void testCreateDraftDoi() throws R2d2TechnicalException {
    //Given
    UUID uuid = UUID.randomUUID();
    this.datasetVersion = DatasetVersionBuilder.aDatasetVersion().dataset(DatasetBuilder.aDataset().id(uuid).build()).build();

    //When
    String response = this.doiRepository.createDraftDoi(datasetVersion);

    //Then
    assertThat(response).isNotNull();
    //TODO: Add further appropriate assertions

    //After
    LOGGER.info("Created Draft Doi: " + response);
    this.doi = response;
  }

  @Test
  @Order(2)
  void testUpdateToFindableDoi() throws R2d2TechnicalException {
    //Given
    this.datasetVersion.setMetadata(DatasetVersionMetadataBuilder.aDatasetVersionMetadata().doi(this.doi)
        .authors(Arrays.asList(PersonBuilder.aPerson().givenName("Creator").familyName("Nr.1").build()))
        .title("Title for Findable Doi Test").build());
    this.datasetVersion.setPublicationDate(OffsetDateTime.now().truncatedTo(ChronoUnit.MICROS));

    //When
    String response = this.doiRepository.updateToFindableDoi(datasetVersion);

    //Then
    assertThat(response).isNotNull();
    //TODO: Add further appropriate assertions

    LOGGER.info("Update to Findable Doi: " + response);
  }

}
