package de.mpg.mpdl.r2d2.transformation.doi;

import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.Person;
import de.mpg.mpdl.r2d2.transformation.doi.model.DoiMetadata;
import de.mpg.mpdl.r2d2.transformation.doi.model.DoiResourceType;
import de.mpg.mpdl.r2d2.util.testdata.DatasetVersionBuilder;
import de.mpg.mpdl.r2d2.util.testdata.PersonBuilder;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class DoiMetadataMapperTest {

  private final DoiMetadataMapper doiMetadataMapper = Mappers.getMapper(DoiMetadataMapper.class);

  @Test
  public void testConvertToDoiMetadata() {
    //Given
    String title = "Title";
    Person author1 = new PersonBuilder().setName("G1", "F1").create();
    Person author2 = new PersonBuilder().setName("G2", "F2").create();
    OffsetDateTime publicationDate = OffsetDateTime.now().truncatedTo(ChronoUnit.MICROS);
    DatasetVersion datasetVersion =
        new DatasetVersionBuilder().setMetadata(title, author1, author2).setPublicationDate(publicationDate).create();

    //When
    DoiMetadata doiMetadata = doiMetadataMapper.convertToDoiMetadata(datasetVersion);

    //Then
    assertThat(doiMetadata).isNotNull();
    assertThat(doiMetadata.getTitle()).isEqualTo(title);
    assertThat(doiMetadata.getCreators()).extracting("GivenName", "FamilyName")
        .containsExactly(tuple(author1.getGivenName(), author1.getFamilyName()), tuple(author2.getGivenName(), author2.getFamilyName()));
    assertThat(doiMetadata.getPublicationYear()).isEqualTo(publicationDate.getYear());
    assertThat(doiMetadata.getResourceType()).extracting("resourceTypeGeneral").isEqualTo(DoiResourceType.RESOURCE_TYPE_GENERAL_DATASET);
  }

}
