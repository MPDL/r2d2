package de.mpg.mpdl.r2d2.util;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.mpg.mpdl.r2d2.service.storage.SwiftObjectStoreRepository;

@ExtendWith({SpringExtension.class, DeleteDatabaseExtension.class, DeleteSearchIndexExtension.class})
//Deactivate DummyDataInitializer default data initialization by setting init.data.creation=false
@SpringBootTest(properties = {"init.data.creation=false"})
@ContextConfiguration(initializers = {DataBaseLauncher.Initializer.class, SearchEngineLauncher.Initializer.class})
public abstract class BaseIntegrationTest {

  //Starting/Stopping the Testcontainers:
  // The singleton container is started only once when the base class is loaded.
  // The container can then be used by all inheriting test classes.
  // At the end of the test suite the Ryuk container that is started by Testcontainers core will take care of stopping the singleton container.
  // see: https://www.testcontainers.org/test_framework_integration/manual_lifecycle_control/

  //TODO: Overwrite application.properties defining access to external Services

  //TODO: Enhance/Adapt the mocking of the cloud storage access

  @MockBean
  private SwiftObjectStoreRepository objectStoreRepository;

}
