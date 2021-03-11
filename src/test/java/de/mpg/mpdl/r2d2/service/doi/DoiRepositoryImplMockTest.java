package de.mpg.mpdl.r2d2.service.doi;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import de.mpg.mpdl.r2d2.transformation.doi.DoiDataCreator;
import de.mpg.mpdl.r2d2.transformation.doi.DoiMetadataMapper;
import de.mpg.mpdl.r2d2.transformation.doi.DoiMetadataXmlConverter;
import org.junit.jupiter.api.*;
import org.mapstruct.factory.Mappers;
import org.slf4j.LoggerFactory;
import org.springframework.mock.env.MockEnvironment;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DoiRepositoryImplMockTest extends DoiRepositoryImplAbstractTest {

  private WireMockServer wireMockServer;

  private String r2d2URL = "datasetUrl";
  private String prefix = "doiPrefix";
  private String username = "apiUsername";
  private String password = "apiPassword";

  private String createDraftDoiResponseBody =
      "{\"data\": {\"id\": \"10.1000/1234-5678\", \"type\": \"dois\", \"attributes\": {\"doi\": \"10.1000/1234-5678\"} } }";

  private String updateToFindableDoiResponseBody =
      "{\"data\": {\"id\": \"10.1000/1234-5678\", \"type\": \"dois\", \"attributes\": {\"doi\": \"10.1000/1234-5678\"} } }";

  @BeforeAll
  void setup() throws IOException {
    this.setupMockServer();
    this.setupDoiRepository();
  }

  void setupMockServer() {
    wireMockServer = new WireMockServer(new WireMockConfiguration().dynamicPort());
    wireMockServer.start();
    WireMock.configureFor("localhost", wireMockServer.port());

    //TODO: Refine the Stub: Add Body, Add different Responses, ...
    stubFor(post(urlEqualTo("/dois")).withHeader("Content-Type", equalTo("application/json"))
        .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(createDraftDoiResponseBody)));

    //TODO: Add Request body
    stubFor(put(urlMatching("/dois/.*")).withHeader("Content-Type", equalTo("application/json"))
        //How to check the request body more precise?
        .withRequestBody(matchingJsonPath("$.data", matchingJsonPath("$.attributes")))
        .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(updateToFindableDoiResponseBody)));
  }

  void setupDoiRepository() {
    MockEnvironment env = new MockEnvironment();
    env.setProperty("r2d2.url", r2d2URL);
    env.setProperty("datacite.doi.prefix", prefix);
    env.setProperty("datacite.doi.api.url", wireMockServer.baseUrl());
    env.setProperty("datacite.doi.api.username", username);
    env.setProperty("datacite.doi.api.password", password);

    DoiMetadataMapper doiMetadataMapper = Mappers.getMapper(DoiMetadataMapper.class);
    DoiMetadataXmlConverter doiMetadataXmlConverter = new DoiMetadataXmlConverter();
    DoiDataCreator doiDataCreator = new DoiDataCreator(doiMetadataMapper, doiMetadataXmlConverter, env);

    doiRepository = new DoiRepositoryImpl(env, doiDataCreator);
  }

  @AfterAll
  void teardown() {
    wireMockServer.stop();
  }

}
