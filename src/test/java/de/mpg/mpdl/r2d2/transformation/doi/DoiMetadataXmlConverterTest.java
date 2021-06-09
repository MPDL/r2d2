package de.mpg.mpdl.r2d2.transformation.doi;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import javax.xml.bind.JAXBException;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import de.mpg.mpdl.r2d2.transformation.doi.model.DoiMetadata;
import de.mpg.mpdl.r2d2.util.testdata.builder.DoiCreatorBuilder;
import de.mpg.mpdl.r2d2.util.testdata.builder.DoiIdentifierBuilder;
import de.mpg.mpdl.r2d2.util.testdata.builder.DoiMetadataBuilder;
import de.mpg.mpdl.r2d2.util.testdata.builder.DoiTitleBuilder;

public class DoiMetadataXmlConverterTest {

  private DoiMetadataXmlConverter doiMetadataXmlConverter = new DoiMetadataXmlConverter();

  @Test
  public void testConvertToXML() throws JAXBException, SAXException {
    //Given
    DoiMetadata doiMetadata = DoiMetadataBuilder.aDoiMetadata()
        .titles(Arrays.asList(DoiTitleBuilder.aDoiTitle().title("TestTitle").build()))
        .creators(Arrays
            .asList(DoiCreatorBuilder.aDoiCreator().creatorName("Creator Name").givenName("Given Name").familyName("Family Name").build()))
        .publicationYear(2021).identifier(DoiIdentifierBuilder.aDoiIdentifier().identifier("doi").build()).build();

    //When
    String metadataXml = doiMetadataXmlConverter.convertToXML(doiMetadata);

    //Then
    assertThat(metadataXml).isNotNull();
    //TODO Assert the values of the unmarshalled DoiMetadata

    //TODO: validate the xml with the Datacite schema?
    //    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    //    File xsdFile = new File(this.getClass().getClassLoader().getResource("metadata.xsd").getFile());
    //    Schema schema = schemaFactory.newSchema(xsdFile);
    //    unmarshaller.setSchema(schema);
    doiMetadataXmlConverter.convertToDoiMetadata(metadataXml);
  }

}
