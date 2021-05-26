package de.mpg.mpdl.r2d2.rest.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import de.mpg.mpdl.r2d2.aa.JWTLoginFilter;
import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.util.BaseIntegrationTest;
import de.mpg.mpdl.r2d2.util.testdata.TestDataManager;
import de.mpg.mpdl.r2d2.util.testdata.TestDataFactory;
import org.assertj.core.api.Condition;
import org.assertj.core.util.Strings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * REST Integration Tests for DatasetController.
 */
@AutoConfigureMockMvc
class DatasetControllerIT extends BaseIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private TestDataManager testDataManager;

  @Test
  void testCreateDataset() throws Exception {
    //Given
    UserAccount userAccount = TestDataFactory.anUser().build();

    this.testDataManager.persist(userAccount);

    String path = "/datasets/" + "";
    String datasetTitle = "datasetTitle";
    String body = "{\"metadata\":{\"title\":\"" + datasetTitle + "\"}}";
    String token = this.createBearerJsonWebToken(userAccount.getId().toString());

    //When
    ResultActions getDatasetResult =
        mockMvc.perform(post(path).header("Authorization", token).content(body).contentType(MediaType.APPLICATION_JSON));

    //Then
    getDatasetResult.andExpect(status().isCreated()).andExpect(jsonPath("$.metadata.title").value(datasetTitle));

    List<DatasetVersion> datasetVersionsFromDB = this.testDataManager.findAll(DatasetVersion.class);

    Condition<DatasetVersion> doiNotEmpty = new Condition<>(dv -> !Strings.isNullOrEmpty(dv.getMetadata().getDoi()), "DOI not empty");
    assertThat(datasetVersionsFromDB).hasSize(1).have(doiNotEmpty)
        .extracting(dv -> dv.getMetadata().getTitle(), DatasetVersion::getVersionNumber).containsOnly(tuple(datasetTitle, 1));
  }

  @Test
  void testGetDataset() throws Exception {
    //Given
    UserAccount userAccount = TestDataFactory.anUser().build();

    Dataset dataset = TestDataFactory.aDataset().creator(userAccount).build();
    DatasetVersion datasetVersion = TestDataFactory.aDatasetVersion().dataset(dataset).build();

    this.testDataManager.persist(userAccount, datasetVersion);

    String id = datasetVersion.getId().toString();
    String path = "/datasets/{id}";
    String token = this.createBearerJsonWebToken(userAccount.getId().toString());

    //When
    ResultActions getDatasetResult = mockMvc.perform(get(path, id).header("Authorization", token));

    //Then
    getDatasetResult.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id));
  }

  private String createBearerJsonWebToken(String userId) {
    String tokenPrefix = "Bearer ";
    String jsonWebToken = JWT.create().withSubject("userName").withClaim("user_id", userId).sign(Algorithm.HMAC512(JWTLoginFilter.SECRET));
    String token = tokenPrefix + jsonWebToken;

    return token;
  }

}
