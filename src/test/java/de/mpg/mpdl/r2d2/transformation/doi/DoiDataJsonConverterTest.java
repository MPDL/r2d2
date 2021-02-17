package de.mpg.mpdl.r2d2.transformation.doi;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.mpg.mpdl.r2d2.transformation.doi.model.DoiMetadata;
import de.mpg.mpdl.r2d2.util.testdata.builder.DoiCreatorBuilder;
import de.mpg.mpdl.r2d2.util.testdata.builder.DoiIdentifierBuilder;
import de.mpg.mpdl.r2d2.util.testdata.builder.DoiMetadataBuilder;
import de.mpg.mpdl.r2d2.util.testdata.builder.DoiTitleBuilder;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBException;
import java.util.Arrays;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

public class DoiDataJsonConverterTest {

  private final DoiDataJsonConverter doiDataJsonConverter = new DoiDataJsonConverter();

  @Test
  void testCreateJsonForDoiCreation() throws JsonProcessingException, JAXBException {
    //Given
    DoiMetadata doiMetadata =
        DoiMetadataBuilder.aDoiMetadata().titles(Arrays.asList(DoiTitleBuilder.aDoiTitle().title("Test Title ").build()))
            .creators(Arrays.asList(
                DoiCreatorBuilder.aDoiCreator().creatorName("Creator Name").givenName("Given Name").familyName("Family Name").build()))
            .publicationYear(2021).build();
    String metadataXml = new DoiMetadataXmlConverter().convertToXML(doiMetadata);

    DoiDataJsonConverter.DoiDataType doiDataType = DoiDataJsonConverter.DoiDataType.DOIS;
    String prefix = "prefix";
    String url = "https://testURL.org";
    String doiMetadataXmlBase64Encoded = Base64.getEncoder().encodeToString(metadataXml.getBytes());

    //When
    String doiDataJson = doiDataJsonConverter.createJsonForDoiCreation(doiDataType, prefix, url, doiMetadataXmlBase64Encoded);

    //Then
    assertThat(doiDataJson).isNotNull();
    //TODO Assert the values / validate the JSON String
  }

  @Test
  void testCreateJsonForDoiUpdate() throws JsonProcessingException, JAXBException {
    //Given
    DoiMetadata doiMetadata =
        DoiMetadataBuilder.aDoiMetadata().identifier(DoiIdentifierBuilder.aDoiIdentifier().identifier("the_DOI_to_add").build())
            .titles(Arrays.asList(DoiTitleBuilder.aDoiTitle().title("Test Title").build()))
            .creators(Arrays.asList(
                DoiCreatorBuilder.aDoiCreator().creatorName("Creator Name").givenName("Given Name").familyName("Family Name").build()))
            .publicationYear(2021).build();
    String metadataXml = new DoiMetadataXmlConverter().convertToXML(doiMetadata);

    DoiDataJsonConverter.DoiEvent doiEvent = DoiDataJsonConverter.DoiEvent.PUBLISH;
    String url = "https://testURL.org";
    String doiMetadataXmlBase64Encoded = Base64.getEncoder().encodeToString(metadataXml.getBytes());

    //When
    String doiDataJson = doiDataJsonConverter.createJsonForDoiUpdate(doiEvent, url, doiMetadataXmlBase64Encoded);

    //Then
    assertThat(doiDataJson).isNotNull();
    //TODO Assert the values / validate the JSON String
  }

}
