package de.mpg.mpdl.r2d2.transformation.doi;

import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.DatasetVersionMetadata;
import de.mpg.mpdl.r2d2.model.Person;
import de.mpg.mpdl.r2d2.transformation.doi.model.DoiIdentifier;
import de.mpg.mpdl.r2d2.transformation.doi.model.DoiMetadata;
import de.mpg.mpdl.r2d2.transformation.doi.model.DoiResourceType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class DoiMetadataMapperTest {

  private final DoiMetadataMapper doiMetadataMapper = Mappers.getMapper(DoiMetadataMapper.class);

  @Test
  public void testConvertToDoiMetadata() {
    //Given
    String title = "Title";
    Person author1 = Person.builder().givenName("G1").familyName("F1").build();
    Person author2 = Person.builder().givenName("G2").familyName("F2").build();
    OffsetDateTime publicationDate = OffsetDateTime.now().truncatedTo(ChronoUnit.MICROS);

    DatasetVersionMetadata metadata = DatasetVersionMetadata.builder().title(title).authors(Arrays.asList(author1, author2)).build();
    DatasetVersion datasetVersion = DatasetVersion.builder().metadata(metadata).publicationDate(publicationDate).build();

    //When
    DoiMetadata doiMetadata = doiMetadataMapper.convertToDoiMetadata(datasetVersion);

    //Then
    assertThat(doiMetadata).isNotNull();
    assertThat(doiMetadata.getTitles()).extracting("title").containsExactly(title);
    assertThat(doiMetadata.getCreators()).extracting("creatorName", "GivenName", "FamilyName").containsExactly(
        tuple(author1.getFamilyName() + ", " + author1.getGivenName(), author1.getGivenName(), author1.getFamilyName()),
        tuple(author2.getFamilyName() + ", " + author2.getGivenName(), author2.getGivenName(), author2.getFamilyName()));
    assertThat(doiMetadata.getPublicationYear()).isEqualTo(publicationDate.getYear());
  }

  @Test
  public void testConvertEmptyDatasetVersionToDoiMetadata() {
    //Given
    DatasetVersion datasetVersion = DatasetVersion.builder().build();

    //When
    DoiMetadata doiMetadata = doiMetadataMapper.convertToDoiMetadata(datasetVersion);

    //Then
    assertThat(doiMetadata).isNotNull();
    assertThat(doiMetadata.getTitles()).isNull();
    assertThat(doiMetadata.getCreators()).isEmpty();
    assertThat(doiMetadata.getPublicationYear()).isEqualTo(0);
    //Check default values
    assertThat(doiMetadata.getIdentifier()).extracting("identifierType").isEqualTo(DoiIdentifier.IDENTIFIER_TYPE_DOI);
    assertThat(doiMetadata.getPublisher()).isEqualTo(DoiMetadata.PUBLISHER_MPG);
    assertThat(doiMetadata.getResourceType()).extracting("resourceTypeGeneral").isEqualTo(DoiResourceType.RESOURCE_TYPE_GENERAL_DATASET);
  }

}
