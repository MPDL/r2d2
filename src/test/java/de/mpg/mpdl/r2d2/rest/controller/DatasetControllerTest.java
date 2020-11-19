package de.mpg.mpdl.r2d2.rest.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.client.RestTemplate;

import de.mpg.mpdl.r2d2.aa.UserDetailsServiceImpl;
import de.mpg.mpdl.r2d2.db.UserAccountRepository;
import de.mpg.mpdl.r2d2.search.service.DatasetSearchService;
import de.mpg.mpdl.r2d2.service.DatasetVersionService;
import de.mpg.mpdl.r2d2.service.FileService;
import de.mpg.mpdl.r2d2.util.DtoMapper;

/**
 * Testing Rest API delegates to the service methods
 * 
 * @author helk
 *
 */
@WebMvcTest(DatasetController.class)
public class DatasetControllerTest {

  @MockBean
  private UserDetailsServiceImpl userDetailsServiceImpl;

  @MockBean
  private UserAccountRepository userAccountRepository;

  @MockBean
  private DatasetVersionService datasetVersionService;

  @MockBean
  private DatasetSearchService datasetSearchService;

  @MockBean
  private RestTemplate restTemplate;

  @MockBean
  private FileService fileService;

  @MockBean
  private DtoMapper dtoMapper;

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void testCreateDataset() throws Exception {
    //Given
    String path = "/datasets/" + "";
    String body = "{}";

    //When
    ResultActions getDatasetResult = mockMvc.perform(post(path).content(body).contentType(MediaType.APPLICATION_JSON));

    //Then
    getDatasetResult.andExpect(status().isCreated());

    Mockito.verify(datasetVersionService).create(Mockito.any(), Mockito.any());
  }

}
