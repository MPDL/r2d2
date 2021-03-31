package de.mpg.mpdl.r2d2.transformation.doi;

import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.transformation.doi.model.DoiAttributes;
import de.mpg.mpdl.r2d2.transformation.doi.model.DoiData;
import de.mpg.mpdl.r2d2.util.testdata.builder.DatasetBuilder;
import de.mpg.mpdl.r2d2.util.testdata.builder.DatasetVersionBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mapstruct.factory.Mappers;
import org.springframework.mock.env.MockEnvironment;

import java.util.Base64;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DoiDataCreatorTest {

  private DoiDataCreator doiDataCreator;

  private String prefix = "Test-Prefix";

  @BeforeAll
  void setupDoiDataCreator() {
    DoiMetadataMapper doiMetadataMapper = Mappers.getMapper(DoiMetadataMapper.class);
    DoiMetadataXmlConverter doiMetadataXmlConverter = new DoiMetadataXmlConverter();
    MockEnvironment env = new MockEnvironment();
    env.setProperty("datacite.doi.prefix", prefix);

    doiDataCreator = new DoiDataCreator(doiMetadataMapper, doiMetadataXmlConverter, env);
  }

  @Test
  void testCreateDoiDataForDraftDoiCreation() throws R2d2TechnicalException {
    //Given
    UUID uuid = UUID.randomUUID();
    DatasetVersion datasetVersion = DatasetVersionBuilder.aDatasetVersion().dataset(DatasetBuilder.aDataset().id(uuid).build()).build();

    //When
    DoiData doiData = doiDataCreator.createDoiDataForDraftDoiCreation(datasetVersion);

    //Then
    assertThat(doiData).extracting(DoiData::getType).isEqualTo(DoiData.DoiType.DOIS);
    assertThat(doiData).extracting(DoiData::getAttributes).isNotNull();
    assertThat(doiData.getAttributes()).extracting(DoiAttributes::getPrefix).isEqualTo(this.prefix);
    //    assertThat(doiData.getAttributes().getUrl()).endsWith(uuid.toString());
  }

  @Test
  void testCreateDoiDataForDoiPublication() throws R2d2TechnicalException {
    //Given
    UUID uuid = UUID.randomUUID();
    DatasetVersion datasetVersion = DatasetVersionBuilder.aDatasetVersion().dataset(DatasetBuilder.aDataset().id(uuid).build()).build();

    //When
    DoiData doiData = doiDataCreator.createDoiDataForDoiPublication(datasetVersion);

    //Then
    assertThat(doiData).extracting(DoiData::getType).isNull();
    assertThat(doiData).extracting(DoiData::getAttributes).isNotNull();
    assertThat(doiData.getAttributes()).extracting(DoiAttributes::getEvent).isEqualTo(DoiAttributes.DoiEvent.PUBLISH);
    assertThat(doiData.getAttributes().getUrl()).endsWith(uuid.toString());
    //Check metadataXML is Base64 encoded:
    assertThatCode(() -> Base64.getDecoder().decode(doiData.getAttributes().getXml())).doesNotThrowAnyException();
  }

}
