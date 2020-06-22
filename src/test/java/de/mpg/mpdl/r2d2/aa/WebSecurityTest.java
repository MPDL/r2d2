package de.mpg.mpdl.r2d2.aa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import de.mpg.mpdl.r2d2.db.UserAccountRepository;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.service.DatasetVersionService;
import de.mpg.mpdl.r2d2.service.UserService;

/**
 * Test for the WebSecurity (CORS Configuration, AuthenticationFilters)
 * 
 * Note: This is a WebMvcTest. All Components, Services and Repositories are mocked. => The request
 * responses of the tests differ from real responses. Only WebSecurity and the Authorization and
 * Authentication mechanisms of the Filters are executed.
 * 
 * @author helk
 *
 */
@WebMvcTest
public class WebSecurityTest {

  @MockBean
  private UserDetailsServiceImpl userDetailsServiceImpl;

  @MockBean
  private UserAccountRepository userAccountRepository;

  @MockBean
  private DatasetVersionService datasetVersionService;

  @MockBean
  private UserService userService;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void testExposedHeaders() throws Exception {
    //Given
    String path = "/datasets/" + UUID.randomUUID();

    //When
    ResultActions getDatasetResult =
        mockMvc.perform(get(path).header("Access-Control-Request-Method", "Any value").header("Origin", "Any value"));

    //Then
    MockHttpServletResponse response = getDatasetResult.andExpect(status().isOk()).andReturn().getResponse();
    String exposedHeaders = response.getHeader("Access-Control-Expose-Headers");

    assertThat(exposedHeaders).contains("Authorization", "Content-Disposition");
  }

  @Test
  public void testGetMethodIsPermitted() throws Exception {
    //Given
    String path = "/datasets/" + UUID.randomUUID();

    //When
    ResultActions getDatasetResult =
        mockMvc.perform(get(path).header("Access-Control-Request-Method", "Any value").header("Origin", "Any value"));

    //Then
    getDatasetResult.andExpect(status().isOk());
  }

  @Test
  public void testPutMethodIsPermitted() throws Exception {
    //Given
    String path = "/datasets/" + UUID.randomUUID();
    String body = "{}";

    //When
    ResultActions putDatasetResult = mockMvc.perform(put(path).content(body).contentType(MediaType.APPLICATION_JSON)
        .header("Access-Control-Request-Method", "Any value").header("Origin", "Any value"));

    //Then
    putDatasetResult.andExpect(status().isCreated());
  }

  @Test
  public void testPostMethodIsPermitted() throws Exception {
    //Given
    String path = "/datasets/";
    String body = "{}";

    //When
    ResultActions putDatasetResult = mockMvc.perform(post(path).content(body).contentType(MediaType.APPLICATION_JSON)
        .header("Access-Control-Request-Method", "Any value").header("Origin", "Any value"));

    //Then
    putDatasetResult.andExpect(status().isCreated());
  }

  @Test
  public void testLoginReturnsBearerToken() throws Exception {
    //Given
    String testUsername = "testUsername";
    String testPW = "testPW";
    String encryptedPW = passwordEncoder.encode(testPW);

    R2D2Principal userDetails = new R2D2Principal(testUsername, encryptedPW, new ArrayList<GrantedAuthority>());
    UserAccount userAccount = new UserAccount();
    userAccount.setId(UUID.randomUUID());
    userDetails.setUserAccount(userAccount);

    Mockito.when(this.userDetailsServiceImpl.loadUserByUsername(Mockito.any())).thenReturn(userDetails);

    String path = "/login";
    String body = "{\"username\": \"" + testUsername + "\", \"password\": \"" + testPW + "\"}";

    //When
    ResultActions loginResult = mockMvc.perform(post(path).content(body));

    //Then
    MockHttpServletResponse response =
        loginResult.andExpect(status().isOk()).andExpect(header().exists("Authorization")).andReturn().getResponse();
    String authorizationHeader = response.getHeader("Authorization");

    assertThat(authorizationHeader).startsWith("Bearer");
  }

  @Test
  public void testAuthenticationByAuthorizationToken() throws Exception {
    //Given
    UserAccount userAccount = new UserAccount();
    userAccount.setEmail("email");

    Mockito.when(this.userAccountRepository.findById(Mockito.any())).thenReturn(Optional.of(userAccount));

    String path = "/datasets/" + UUID.randomUUID();
    String tokenPrefix = "Bearer ";
    String token = JWT.create().withSubject("testUsername").withClaim("user_id", UUID.randomUUID().toString())
        .sign(Algorithm.HMAC512(JWTLoginFilter.SECRET));

    //When
    ResultActions putDatasetResult =
        mockMvc.perform(put(path).header("Authorization", tokenPrefix + token).content("{}").contentType(MediaType.APPLICATION_JSON));

    //Then
    putDatasetResult.andExpect(status().isCreated());
    //TODO: Check the authentication is set to the SecurityContext 
  }

}
