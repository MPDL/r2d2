package de.mpg.mpdl.r2d2.transformation.doi;

import de.mpg.mpdl.r2d2.transformation.doi.model.DoiCreator;
import de.mpg.mpdl.r2d2.transformation.doi.model.DoiMetadata;
import de.mpg.mpdl.r2d2.transformation.doi.model.DoiTitle;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class DoiMetadataXmlConverterTest {

  private DoiMetadataXmlConverter doiMetadataXmlConverter = new DoiMetadataXmlConverter();

  @Test
  public void testConvertToXML() throws JAXBException, SAXException {
    //Given
    DoiMetadata doiMetadata = DoiMetadata.builder().titles(Arrays.asList(DoiTitle.builder().title("TestTitle").build()))
        .creators(Arrays.asList(DoiCreator.builder().creatorName("Creator Name").givenName("Given Name").familyName("Family Name").build()))
        .publicationYear(2020).build();

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
