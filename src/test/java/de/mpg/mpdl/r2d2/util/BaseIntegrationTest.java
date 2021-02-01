package de.mpg.mpdl.r2d2.util;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.mpg.mpdl.r2d2.service.storage.SwiftObjectStoreRepository;

/**
 * <p>
 * Base class for all Integration Tests. <br>
 * - Initialize the complete Spring Context <br>
 * - Starting DB & ES, each in a single Testcontainer at the beginning of a Test-Run <br>
 * - Clears DB & ES after each Test <br>
 * - Stopping the Testcontainers for DB & ES at the end of a Test-Run <br>
 * </p>
 *
 * <p>
 * Starting/Stopping the Testcontainers: <br>
 * A singleton container is started only once when the base class is loaded.
 * The container can then be used by all inheriting test classes.
 * At the end of the test suite the Ryuk container that is started by Testcontainers core will take care of stopping the singleton container.
 * see: https://www.testcontainers.org/test_framework_integration/manual_lifecycle_control/
 * </p>
 *
 */
@ExtendWith({SpringExtension.class, DeleteDatabaseExtension.class, DeleteSearchIndexExtension.class})
//Deactivate DummyDataInitializer default data initialization by setting init.data.creation=false
@SpringBootTest(properties = {"init.data.creation=false"})
@ContextConfiguration(initializers = {DataBaseLauncher.Initializer.class, SearchEngineLauncher.Initializer.class})
public abstract class BaseIntegrationTest {

  //TODO: Overwrite application.properties defining access to external Services

  //TODO: Enhance/Adapt the mocking of the cloud storage access

  @MockBean
  private SwiftObjectStoreRepository objectStoreRepository;

}
