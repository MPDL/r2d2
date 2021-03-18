package de.mpg.mpdl.r2d2.transformation.doi;

import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.DatasetVersionMetadata;
import de.mpg.mpdl.r2d2.model.Person;
import de.mpg.mpdl.r2d2.transformation.doi.model.*;
import de.mpg.mpdl.r2d2.util.testdata.builder.DatasetVersionBuilder;
import de.mpg.mpdl.r2d2.util.testdata.builder.DatasetVersionMetadataBuilder;
import de.mpg.mpdl.r2d2.util.testdata.builder.PersonBuilder;
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
    Person author1 = PersonBuilder.aPerson().givenName("G1").familyName("F1").build();
    Person author2 = PersonBuilder.aPerson().givenName("G2").familyName("F2").build();
    OffsetDateTime publicationDate = OffsetDateTime.now().truncatedTo(ChronoUnit.MICROS);
    String doi = "Prefix/Suffix";

    DatasetVersionMetadata metadata =
        DatasetVersionMetadataBuilder.aDatasetVersionMetadata().title(title).authors(Arrays.asList(author1, author2)).doi(doi).build();
    DatasetVersion datasetVersion = DatasetVersionBuilder.aDatasetVersion().metadata(metadata).publicationDate(publicationDate).build();

    //When
    DoiMetadata doiMetadata = doiMetadataMapper.convertToDoiMetadata(datasetVersion);

    //Then
    assertThat(doiMetadata).isNotNull();
    assertThat(doiMetadata.getIdentifier()).extracting(DoiIdentifier::getIdentifier).isEqualTo(doi);
    assertThat(doiMetadata.getTitles()).extracting(DoiTitle::getTitle).containsExactly(title);
    assertThat(doiMetadata.getCreators()).extracting(DoiCreator::getCreatorName, DoiCreator::getGivenName, DoiCreator::getFamilyName)
        .containsExactly(tuple(author1.getFamilyName() + ", " + author1.getGivenName(), author1.getGivenName(), author1.getFamilyName()),
            tuple(author2.getFamilyName() + ", " + author2.getGivenName(), author2.getGivenName(), author2.getFamilyName()));
    assertThat(doiMetadata.getPublicationYear()).isEqualTo(publicationDate.getYear());
  }

  @Test
  public void testConvertEmptyDatasetVersionToDoiMetadata() {
    //Given
    DatasetVersion datasetVersion = DatasetVersionBuilder.aDatasetVersion().build();

    //When
    DoiMetadata doiMetadata = doiMetadataMapper.convertToDoiMetadata(datasetVersion);

    //Then
    assertThat(doiMetadata).isNotNull();
    assertThat(doiMetadata.getTitles()).isNull();
    assertThat(doiMetadata.getCreators()).isEmpty();
    assertThat(doiMetadata.getPublicationYear()).isEqualTo(0);
    //Check default values
    assertThat(doiMetadata.getIdentifier().getIdentifierType()).isEqualTo(DoiIdentifier.IDENTIFIER_TYPE_DOI);
    assertThat(doiMetadata.getIdentifier()).extracting(DoiIdentifier::getIdentifierType, DoiIdentifier::getIdentifier)
        .containsExactly(DoiIdentifier.IDENTIFIER_TYPE_DOI, null);
    assertThat(doiMetadata.getPublisher()).isEqualTo(DoiMetadata.PUBLISHER_MPG);
    assertThat(doiMetadata.getResourceType().getResourceTypeGeneral()).isEqualTo(DoiResourceType.RESOURCE_TYPE_GENERAL_DATASET);
    assertThat(doiMetadata.getResourceType().getResourceType()).isEqualTo(DoiResourceType.RESOURCE_TYPE_DATASET);
  }

}
