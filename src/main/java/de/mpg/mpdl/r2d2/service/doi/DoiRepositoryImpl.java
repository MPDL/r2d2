package de.mpg.mpdl.r2d2.service.doi;

import org.springframework.web.reactive.function.client.WebClient;

import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.transformation.doi.DoiMetadataXmlConverter;

public class DoiRepositoryImpl implements DoiRepository {

  DoiMetadataXmlConverter doiMetadataXmlConverter;

  //@Autowired
  //WebClient doiWebClient;

  public DoiRepositoryImpl(DoiMetadataXmlConverter doiMetadataXmlConverter, WebClient.Builder webClientBuilder) {
    this.doiMetadataXmlConverter = doiMetadataXmlConverter;
    //TODO: Set correct URL
    //this.doiWebClient = webClientBuilder.baseUrl("...").build();
  }

  @Override
  public String create(DatasetVersion datasetVersion) {
    //TODO: Transform relevant Dataset data to JSON or XML -> in an extra class

    //        doiWebClient.post

    return null;
  }

}
