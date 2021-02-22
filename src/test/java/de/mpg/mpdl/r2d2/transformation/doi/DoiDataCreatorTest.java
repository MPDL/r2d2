package de.mpg.mpdl.r2d2.transformation.doi;

import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.transformation.doi.model.DoiData;
import de.mpg.mpdl.r2d2.util.testdata.builder.DatasetVersionBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mapstruct.factory.Mappers;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DoiDataCreatorTest {

  private DoiDataCreator doiDataCreator;

  @BeforeAll
  void setupDoiDataCreator(){
    DoiMetadataMapper doiMetadataMapper = Mappers.getMapper(DoiMetadataMapper.class);
    DoiMetadataXmlConverter doiMetadataXmlConverter = new DoiMetadataXmlConverter();
    MockEnvironment env = new MockEnvironment();
    env.setProperty("datacite.doi.prefix", "Prefix");

    doiDataCreator = new DoiDataCreator(doiMetadataMapper, doiMetadataXmlConverter, env);
  }

  @Test
  void testCreateDoiDataForDraftDoiCreation() throws R2d2TechnicalException {
    //Given
    DatasetVersion datasetVersion = DatasetVersionBuilder.aDatasetVersion().build();

    //When
    DoiData doiData = doiDataCreator.createDoiDataForDraftDoiCreation(datasetVersion);

    //Then
    assertThat(doiData).extracting(DoiData::getType).isEqualTo(DoiData.DoiType.DOIS);
    assertThat(doiData).extracting(DoiData::getAttributes).isNotNull();
    //TODO Add more assertions
  }

  @Test
  void testCreateDoiDataForDoiPublication() throws R2d2TechnicalException {
    //Given
    DatasetVersion datasetVersion = DatasetVersionBuilder.aDatasetVersion().build();

    //When
    DoiData doiData = doiDataCreator.createDoiDataForDoiPublication(datasetVersion);

    //Then
    assertThat(doiData).extracting(DoiData::getType).isNull();
    assertThat(doiData).extracting(DoiData::getAttributes).isNotNull();
    //TODO Add more assertions
  }

}
