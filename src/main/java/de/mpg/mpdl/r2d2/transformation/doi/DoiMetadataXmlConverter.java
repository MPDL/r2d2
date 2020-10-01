package de.mpg.mpdl.r2d2.transformation.doi;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.springframework.stereotype.Component;

import de.mpg.mpdl.r2d2.transformation.doi.model.DoiMetadata;

@Component
public class DoiMetadataXmlConverter {

  public static final String DATACITE_METADATA_SCHEMA_4_3_LOCATION =
      "http://datacite.org/schema/kernel-4 http://schema.datacite.org/meta/kernel-4.3/metadata.xsd";

  public String convertToXML(DoiMetadata doiMetadata) throws JAXBException {
    JAXBContext context = JAXBContext.newInstance(DoiMetadata.class);
    Marshaller marshaller = context.createMarshaller();

    StringWriter stringWriter = new StringWriter();
    Result result = new StreamResult(stringWriter);

    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    //TODO: Update DOXI Metadata Generator to 4.3 Version !?
    marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, DATACITE_METADATA_SCHEMA_4_3_LOCATION);
    marshaller.marshal(doiMetadata, result);

    return stringWriter.toString();
  }

}
