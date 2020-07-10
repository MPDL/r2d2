package de.mpg.mpdl.r2d2.util;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.mpg.mpdl.r2d2.service.storage.SwiftObjectStoreRepository;

@ExtendWith({SpringExtension.class, DeleteDatabaseExtension.class, DeleteSearchIndexExtension.class})
@SpringBootTest
@ContextConfiguration(initializers = {DataBaseLauncher.Initializer.class, SearchEngineLauncher.Initializer.class})
public abstract class BaseIntegrationTest {

  //TODO: Stop DB/ES container after test?

  //TODO: Enhance/Adapt the mocking of the cloud storage access

  @MockBean
  private SwiftObjectStoreRepository objectStoreRepository;

}
