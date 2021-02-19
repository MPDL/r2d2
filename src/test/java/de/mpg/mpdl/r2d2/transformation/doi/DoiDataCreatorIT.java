package de.mpg.mpdl.r2d2.transformation.doi;

import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.transformation.doi.model.DoiData;
import de.mpg.mpdl.r2d2.util.BaseIntegrationTest;
import de.mpg.mpdl.r2d2.util.testdata.builder.DatasetBuilder;
import de.mpg.mpdl.r2d2.util.testdata.builder.DatasetVersionBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class DoiDataCreatorIT extends BaseIntegrationTest {

  @Autowired
  private DoiDataCreator doiDataCreator;

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
