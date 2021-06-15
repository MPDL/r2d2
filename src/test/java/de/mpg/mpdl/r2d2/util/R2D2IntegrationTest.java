package de.mpg.mpdl.r2d2.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.mpg.mpdl.r2d2.service.storage.SwiftObjectStoreRepository;

/**
 * <p>
 * Annotation for R2D2 Integration Test classes, providing the following features: <br>
 * - Initialize the complete Spring Context <br>
 * - Mock all component classes with external dependencies <br>
 * - Starting DB & ES, each in a single Testcontainer at the beginning of a Test-Run <br>
 * - Clears DB & ES after each Test <br>
 * - Stopping the Testcontainers for DB & ES at the end of a Test-Run <br>
 * </p>
 * <br>
 * <p>
 * Details on Starting/Stopping containers by Testcontainers: <br>
 * The Testcontainers of this Annotation are started in a static way => The containers are started
 * only once (when the first class applying this Annotation is loaded) and then are used by all test
 * classes which apply this Annotation. At the end of the test suite the Ryuk container that is
 * started by Testcontainers core will take care of stopping the singleton container. See:
 * https://www.testcontainers.org/test_framework_integration/manual_lifecycle_control/
 * </p>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith({SpringExtension.class, DeleteDatabaseExtension.class, DeleteSearchIndexExtension.class})
//Deactivate DummyDataInitializer default data initialization by setting init.data.creation=false
@SpringBootTest(properties = {"init.data.creation=false"})
@ContextConfiguration(initializers = {DataBaseLauncher.Initializer.class, SearchEngineLauncher.Initializer.class})
@MockBean(SwiftObjectStoreRepository.class)
public @interface R2D2IntegrationTest {

  //TODO:
  // - Overwrite application-test.properties defining access to external Services
  // - Enhance/Adapt the mocking of the cloud storage access

}
