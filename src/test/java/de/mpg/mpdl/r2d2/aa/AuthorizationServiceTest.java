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
import static de.mpg.mpdl.r2d2.model.Dataset.State;
import static de.mpg.mpdl.r2d2.model.Dataset.State.*;

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
        //authorized, serviceName, methodName, State (of the DatasetVersion), isCreator(?), Grant (Role for the DatasetVersion)
        Arguments.of(true, datasetVersionServiceName, "create", PRIVATE, false, USER),
        Arguments.of(true, datasetVersionServiceName, "create", PUBLIC, false, USER),
        Arguments.of(false, datasetVersionServiceName, "get", PRIVATE, false, USER),
        Arguments.of(true, datasetVersionServiceName, "get", PRIVATE, true, USER),
        Arguments.of(true, datasetVersionServiceName, "get", PRIVATE, false, DATAMANAGER),
        Arguments.of(true, datasetVersionServiceName, "get", PRIVATE, false, ADMIN),
        Arguments.of(true, datasetVersionServiceName, "get", PUBLIC, false, USER),
        Arguments.of(false, datasetVersionServiceName, "update", PRIVATE, false, USER),
        Arguments.of(true, datasetVersionServiceName, "update", PRIVATE, true, USER),
        Arguments.of(true, datasetVersionServiceName, "update", PRIVATE, false, DATAMANAGER),
        Arguments.of(true, datasetVersionServiceName, "update", PRIVATE, false, ADMIN),
        Arguments.of(false, datasetVersionServiceName, "delete", PRIVATE, false, USER),
        Arguments.of(true, datasetVersionServiceName, "delete", PRIVATE, true, USER),
        Arguments.of(true, datasetVersionServiceName, "delete", PRIVATE, false, DATAMANAGER),
        Arguments.of(true, datasetVersionServiceName, "delete", PRIVATE, false, ADMIN),
        Arguments.of(false, datasetVersionServiceName, "withdraw", PUBLIC, false, USER),
        Arguments.of(true, datasetVersionServiceName, "withdraw", PUBLIC, true, USER),
        Arguments.of(true, datasetVersionServiceName, "withdraw", PUBLIC, false, DATAMANAGER),
        Arguments.of(true, datasetVersionServiceName, "withdraw", PUBLIC, false, ADMIN),
        Arguments.of(false, datasetVersionServiceName, "publish", PRIVATE, false, USER),
        Arguments.of(true, datasetVersionServiceName, "publish", PRIVATE, true, USER),
        Arguments.of(true, datasetVersionServiceName, "publish", PRIVATE, false, DATAMANAGER),
        Arguments.of(true, datasetVersionServiceName, "publish", PRIVATE, false, ADMIN)
    );
  }

  @ParameterizedTest
  @MethodSource("provideArgumentsForAuthorization")
  void testCheckAuthorizationForDatasetVersionService(boolean authorized, String serviceName, String methodName, State state,
      boolean isCreator, Role role) {
    //Given
    DatasetVersion datasetVersion = DatasetVersionBuilder.aDatasetVersion().state(state).build();
    Dataset dataset = DatasetBuilder.aDataset().state(state).id(UUID.randomUUID()).build();

    UserAccount userAccount = UserAccountBuilder.anUserAccount()
        .grants(Collections.singletonList(GrantBuilder.aGrant().role(role).dataset(dataset.getId()).build()))
        .id(UUID.randomUUID())
        .build();
    R2D2Principal r2D2Principal = R2D2PrincipalBuilder.aR2D2Principal("userName", "pw", new ArrayList<>()).userAccount(userAccount).build();

    //TODO: Why must user be creator of the Dataset and not the DatasetVersion in this case?
    if (isCreator) {
      dataset.setCreator(userAccount);
    }
    datasetVersion.setDataset(dataset);

    //When
    ThrowingCallable checkAuthorizationCode =
        () -> this.authorizationService.checkAuthorization(serviceName, methodName, r2D2Principal, datasetVersion);

    //Then
    if (authorized) {
      assertThatCode(checkAuthorizationCode).doesNotThrowAnyException();
    } else {
      assertThatCode(checkAuthorizationCode).isInstanceOf(AuthorizationException.class);
    }
  }

}
