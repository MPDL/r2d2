package de.mpg.mpdl.r2d2.aa;

import de.mpg.mpdl.r2d2.R2D2Application;
import de.mpg.mpdl.r2d2.db.UserAccountRepository;
import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.service.impl.DatasetVersionServiceDbImpl;
import de.mpg.mpdl.r2d2.util.testdata.builder.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.ThrowableAssert.ThrowingCallable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

  //TODO: Move jsonObjectMapper() methode from R2D2Application to another class?
  R2D2Application r2D2Application = new R2D2Application();

  @InjectMocks
  private final AuthorizationService authorizationService = new AuthorizationService(r2D2Application.jsonObjectMapper());

  //TODO: UserAccountRepository is never used in AuthorizationService => Remove it from AuthorizationService and this Test (Remove @InjectMocks)
  @Mock
  private UserAccountRepository userAccountRepository;

  //DatasetVersionServiceDbImpl + create + user
  @Test
  void testCheckAuthorizationCreateDatasetVersionAsUser() {
    //Given
    String method = "create";

    R2D2Principal r2D2Principal = R2D2PrincipalBuilder.aR2D2Principal("userName", "pw", new ArrayList<>()).userAccount(UserAccountBuilder
        .anUserAccount().grants(Collections.singletonList(GrantBuilder.aGrant().role(UserAccount.Role.USER).build())).build()).build();

    DatasetVersion datasetVersion = DatasetVersionBuilder.aDatasetVersion().build();

    //When
    ThrowingCallable checkAuthorizationCode = () -> this.authorizationService
        .checkAuthorization(DatasetVersionServiceDbImpl.class.getCanonicalName(), method, r2D2Principal, datasetVersion);

    //Then
    assertThatCode(checkAuthorizationCode).doesNotThrowAnyException();
  }

  //DatasetVersionServiceDbImpl + update + user
  @Test
  void testCheckAuthorizationUpdateDatasetVersionAsUserThrowsException() {
    //Given
    String method = "update";

    R2D2Principal r2D2Principal = R2D2PrincipalBuilder.aR2D2Principal("userName", "pw", new ArrayList<>()).userAccount(UserAccountBuilder
        .anUserAccount().grants(Collections.singletonList(GrantBuilder.aGrant().role(UserAccount.Role.USER).build())).build()).build();

    DatasetVersion datasetVersion = DatasetVersionBuilder.aDatasetVersion().build();

    //When
    ThrowingCallable checkAuthorizationCode = () -> this.authorizationService
        .checkAuthorization(DatasetVersionServiceDbImpl.class.getCanonicalName(), method, r2D2Principal, datasetVersion);

    //Then
    assertThatCode(checkAuthorizationCode).isInstanceOf(AuthorizationException.class);
  }

  //DatasetVersionServiceDbImpl + update + user & creator
  @Test
  void testCheckAuthorizationUpdateDatasetVersionAsUserAndCreator() {
    //Given
    String method = "update";

    UserAccount userAccount = UserAccountBuilder.anUserAccount()
        .grants(Collections.singletonList(GrantBuilder.aGrant().role(UserAccount.Role.USER).build())).id(UUID.randomUUID()).build();
    R2D2Principal r2D2Principal = R2D2PrincipalBuilder.aR2D2Principal("userName", "pw", new ArrayList<>()).userAccount(userAccount).build();

    //TODO: Why must user be creator of the Dataset and not the DatasetVersion in this case?
    Dataset dataset = DatasetBuilder.aDataset().creator(userAccount).build();
    DatasetVersion datasetVersion = DatasetVersionBuilder.aDatasetVersion().dataset(dataset).build();

    //When
    ThrowingCallable checkAuthorizationCode = () -> this.authorizationService
        .checkAuthorization(DatasetVersionServiceDbImpl.class.getCanonicalName(), method, r2D2Principal, datasetVersion);

    //Then
    assertThatCode(checkAuthorizationCode).doesNotThrowAnyException();
  }

  //DatasetVersionServiceDbImpl + update + admin
  @Test
  void testCheckAuthorizationUpdateDatasetVersionAsAdmin() {
    //Given
    String method = "update";

    UserAccount userAccount = UserAccountBuilder.anUserAccount()
        .grants(Collections.singletonList(GrantBuilder.aGrant().role(UserAccount.Role.ADMIN).build())).id(UUID.randomUUID()).build();
    R2D2Principal r2D2Principal = R2D2PrincipalBuilder.aR2D2Principal("userName", "pw", new ArrayList<>()).userAccount(userAccount).build();

    DatasetVersion datasetVersion = DatasetVersionBuilder.aDatasetVersion().build();

    //When
    ThrowingCallable checkAuthorizationCode = () -> this.authorizationService
        .checkAuthorization(DatasetVersionServiceDbImpl.class.getCanonicalName(), method, r2D2Principal, datasetVersion);

    //Then
    assertThatCode(checkAuthorizationCode).doesNotThrowAnyException();
  }

}
