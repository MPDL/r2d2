package de.mpg.mpdl.r2d2.service.doi;

import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.transformation.doi.DoiDataCreator;
import de.mpg.mpdl.r2d2.transformation.doi.model.DoiData;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

@Repository
public class DoiRepositoryImpl implements DoiRepository {

  //TODO:
  // - Maybe use an extra Class DoiClient for the REST-Calls and call the DoiData-Creation in another Class(Repository/Service)?
  // - Make the Webclient-Calls asynchronous?
  // - Optimise creation of the WebClient (via configuration)

  private Environment env;

  private DoiDataCreator doiDataCreator;

  private WebClient dataciteWebClient;

  public DoiRepositoryImpl(Environment env, DoiDataCreator doiDataCreator) {
    this.env = env;
    this.doiDataCreator = doiDataCreator;

    dataciteWebClient = WebClient.builder().baseUrl(env.getProperty("datacite.doi.api.url"))
        .defaultHeaders(
            headers -> headers.setBasicAuth(env.getProperty("datacite.doi.api.username"), env.getProperty("datacite.doi.api.password")))
        .build();
  }

  @Override
  public String createDraftDoi(DatasetVersion datasetVersion) throws R2d2TechnicalException {
    DoiData doiData = doiDataCreator.createDoiDataForDraftDoiCreation(datasetVersion);

    String uri = "/dois";

    String response = dataciteWebClient.post().uri(uri).header(HttpHeaders.CONTENT_TYPE, "application/json").bodyValue(doiData).retrieve()
        .bodyToMono(String.class).block();

    return response;
  }

  @Override
  public String updateToFindableDoi(DatasetVersion datasetVersion) throws R2d2TechnicalException {
    DoiData doiData = doiDataCreator.createDoiDataForDoiPublication(datasetVersion);
    String uri = "/dois" + "/" + datasetVersion.getMetadata().getDoi();

    String response = dataciteWebClient.put().uri(uri).header(HttpHeaders.CONTENT_TYPE, "application/json").bodyValue(doiData).retrieve()
        .bodyToMono(String.class).block();

    return response;
  }

}
