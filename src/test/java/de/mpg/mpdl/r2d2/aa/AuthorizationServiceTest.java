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
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import static de.mpg.mpdl.r2d2.model.aa.UserAccount.Role;
import static de.mpg.mpdl.r2d2.model.aa.UserAccount.Role.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

  //TODO: Move jsonObjectMapper() methode from R2D2Application to another class?
  R2D2Application r2D2Application = new R2D2Application();

  @InjectMocks
  private final AuthorizationService authorizationService = new AuthorizationService(r2D2Application.jsonObjectMapper());

  //TODO: UserAccountRepository is never used in AuthorizationService => Remove it from AuthorizationService and this Test (Remove @InjectMocks)
  @Mock
  private UserAccountRepository userAccountRepository;

  private static Stream<Arguments> provideArgumentsForAuthorization() {
    String datasetVersionServiceName = DatasetVersionServiceDbImpl.class.getCanonicalName();

    return Stream.of(
        //authorized, serviceName, methodName, isCreator(?), Role, Grants???
        Arguments.of(true, datasetVersionServiceName, "create", false, USER),
        Arguments.of(false, datasetVersionServiceName, "update", false, USER),
        Arguments.of(true, datasetVersionServiceName, "update", true, USER),
        Arguments.of(true, datasetVersionServiceName, "update", false, ADMIN));
  }

  @ParameterizedTest
  @MethodSource("provideArgumentsForAuthorization")
  void testCheckAuthorizationDatasetVersionServiceNoException(boolean authorized, String serviceName, String methodName, boolean isCreator, Role role) {
    //Given
    UserAccount userAccount =
        UserAccountBuilder.anUserAccount().grants(Collections.singletonList(GrantBuilder.aGrant().role(role).build())).id(UUID.randomUUID())
            .build();
    R2D2Principal r2D2Principal = R2D2PrincipalBuilder.aR2D2Principal("userName", "pw", new ArrayList<>()).userAccount(userAccount).build();

    DatasetVersion datasetVersion = DatasetVersionBuilder.aDatasetVersion().build();
    if (isCreator) {
      //TODO: Why must user be creator of the Dataset and not the DatasetVersion in this case?
      Dataset dataset = DatasetBuilder.aDataset().creator(userAccount).build();
      datasetVersion.setDataset(dataset);
    }

    //When
    ThrowingCallable checkAuthorizationCode = () -> this.authorizationService.checkAuthorization(serviceName, methodName, r2D2Principal, datasetVersion);

    //Then
    if (authorized) {
      assertThatCode(checkAuthorizationCode).doesNotThrowAnyException();
    } else {
      assertThatCode(checkAuthorizationCode).isInstanceOf(AuthorizationException.class);
    }
  }

}
