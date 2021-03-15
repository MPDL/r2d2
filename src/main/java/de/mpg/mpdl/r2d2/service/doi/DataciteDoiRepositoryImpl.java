package de.mpg.mpdl.r2d2.service.doi;

import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.transformation.doi.DoiDataCreator;
import de.mpg.mpdl.r2d2.transformation.doi.model.DoiData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Repository
public class DataciteDoiRepositoryImpl implements DoiRepository {

  //TODO:
  // - Maybe use an extra Class DoiClient for the REST-Calls and call the DoiData-Creation in another Class(Repository/Service)?
  // - Make the Webclient-Calls asynchronous? Or set Timeouts (default is 30s)
  // - Optimise creation of the WebClient (via configuration)
  // - Cover different responses/error-statusCodes in the requests?
  // - Rename class to DataciteRepository?

  private static Logger LOGGER = LoggerFactory.getLogger(DataciteDoiRepositoryImpl.class);

  private Environment env;

  private DoiDataCreator doiDataCreator;

  private WebClient dataciteWebClient;

  public DataciteDoiRepositoryImpl(Environment env, DoiDataCreator doiDataCreator) {
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

    try {
      DoiData doiDataResponse = dataciteWebClient.post().uri(uri).header(HttpHeaders.CONTENT_TYPE, "application/json").bodyValue(doiData)
          .retrieve().bodyToMono(DoiData.class).block();

      String doi = doiDataResponse.getAttributes().getDoi();

      LOGGER.info("Created a Draft Doi: " + doi);

      return doi;
    } catch (WebClientResponseException e) {
      throw new R2d2TechnicalException(e);
    }
  }

  @Override
  public String updateToFindableDoi(DatasetVersion datasetVersion) throws R2d2TechnicalException {
    DoiData doiData = doiDataCreator.createDoiDataForDoiPublication(datasetVersion);
    String uri = "/dois" + "/" + datasetVersion.getMetadata().getDoi();

    try {
      DoiData response = dataciteWebClient.put().uri(uri).header(HttpHeaders.CONTENT_TYPE, "application/json").bodyValue(doiData).retrieve()
          .bodyToMono(DoiData.class).block();

      String doi = response.getAttributes().getDoi();

      LOGGER.info("Updated Doi to Findable Doi: " + doi);

      return doi;
    } catch (WebClientResponseException e) {
      throw new R2d2TechnicalException(e);
    }
  }

}
