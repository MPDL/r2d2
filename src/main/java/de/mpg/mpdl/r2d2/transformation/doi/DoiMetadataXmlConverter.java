package de.mpg.mpdl.r2d2.transformation.doi;

import de.mpg.mpdl.r2d2.transformation.doi.model.DoiMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;

@Component
public class DoiMetadataXmlConverter {

  private static Logger LOGGER = LoggerFactory.getLogger(DoiMetadataXmlConverter.class);

  public static final String DATACITE_METADATA_SCHEMA_4_3_LOCATION =
      "http://datacite.org/schema/kernel-4 http://schema.datacite.org/meta/kernel-4.3/metadata.xsd";

  public String convertToXML(DoiMetadata doiMetadata) throws JAXBException {
    JAXBContext context = JAXBContext.newInstance(DoiMetadata.class);
    Marshaller marshaller = context.createMarshaller();

    StringWriter stringWriter = new StringWriter();
    Result result = new StreamResult(stringWriter);

    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, DATACITE_METADATA_SCHEMA_4_3_LOCATION);
    marshaller.marshal(doiMetadata, result);

    String doiMetadataXml = stringWriter.toString();

    LOGGER.debug(doiMetadataXml);

    return doiMetadataXml;
  }

  public DoiMetadata convertToDoiMetadata(String metadataXml) throws JAXBException {
    JAXBContext context = JAXBContext.newInstance(DoiMetadata.class);
    Unmarshaller unmarshaller = context.createUnmarshaller();

    StringReader stringReader = new StringReader(metadataXml);
    DoiMetadata doiMetadata = (DoiMetadata) unmarshaller.unmarshal(stringReader);

    return doiMetadata;
  }

}
