package de.mpg.mpdl.r2d2.transformation.doi;

import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.transformation.doi.model.DoiAttributes;
import de.mpg.mpdl.r2d2.transformation.doi.model.DoiAttributes.DoiEvent;
import de.mpg.mpdl.r2d2.transformation.doi.model.DoiData;
import de.mpg.mpdl.r2d2.transformation.doi.model.DoiData.DoiType;
import de.mpg.mpdl.r2d2.transformation.doi.model.DoiMetadata;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;
import java.util.Base64;
import java.util.UUID;

// TODO: Rename this class and all Doi model classes to Datacite... instead of Doi!?
@Component
public class DoiDataCreator {

  private DoiMetadataMapper doiMetadataMapper;

  private DoiMetadataXmlConverter doiMetadataXmlConverter;

  private Environment env;

  public DoiDataCreator(DoiMetadataMapper doiMetadataMapper, DoiMetadataXmlConverter doiMetadataXmlConverter, Environment env) {
    this.doiMetadataMapper = doiMetadataMapper;
    this.doiMetadataXmlConverter = doiMetadataXmlConverter;
    this.env = env;
  }

  public DoiData createDoiDataForDraftDoiCreation(DatasetVersion datasetVersion) throws R2d2TechnicalException {
    DoiAttributes doiAttributes = new DoiAttributes();
    doiAttributes.setPrefix(env.getProperty("datacite.doi.prefix"));
    doiAttributes.setUrl(this.getDatasetURL(datasetVersion.getId()));

    DoiData doiData = new DoiData();
    doiData.setType(DoiType.DOIS);
    doiData.setAttributes(doiAttributes);

    return doiData;
  }

  public DoiData createDoiDataForDoiPublication(DatasetVersion datasetVersion) throws R2d2TechnicalException {
    String doiMetadataXmlBase64Encoded = this.transformToDoiMetadataXmlBase64Encoded(datasetVersion);

    DoiAttributes doiAttributes = new DoiAttributes();
    doiAttributes.setEvent(DoiEvent.PUBLISH);
    doiAttributes.setUrl(this.getDatasetURL(datasetVersion.getId()));
    doiAttributes.setXml(doiMetadataXmlBase64Encoded);

    DoiData doiData = new DoiData();
    doiData.setAttributes(doiAttributes);

    return doiData;
  }

  private String transformToDoiMetadataXmlBase64Encoded(DatasetVersion datasetVersion) throws R2d2TechnicalException {
    DoiMetadata doiMetadata = this.doiMetadataMapper.convertToDoiMetadata(datasetVersion);

    String doiMetadataXml;
    try {
      doiMetadataXml = this.doiMetadataXmlConverter.convertToXML(doiMetadata);
    } catch (JAXBException e) {
      throw new R2d2TechnicalException(e);
    }

    String doiMetadataXmlBase64Encoded = Base64.getEncoder().encodeToString(doiMetadataXml.getBytes());

    return doiMetadataXmlBase64Encoded;
  }

  //TODO: Move/Refactor getting the URL of a Dataset
  private String getDatasetURL(UUID dataSetID) {
    String r2d2URL = this.env.getProperty("r2d2.url");

    String datasetURL = r2d2URL + "/datasets" + "/" + dataSetID;
    return datasetURL;
  }

}
