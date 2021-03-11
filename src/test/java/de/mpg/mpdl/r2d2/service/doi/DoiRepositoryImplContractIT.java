package de.mpg.mpdl.r2d2.service.doi;

import de.mpg.mpdl.r2d2.service.storage.SwiftObjectStoreRepository;
import de.mpg.mpdl.r2d2.util.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

@ExtendWith({SpringExtension.class, DeleteDatabaseExtension.class, DeleteSearchIndexExtension.class})
@SpringBootTest(properties = {"init.data.creation=false"})
@ContextConfiguration(initializers = {DataBaseLauncher.Initializer.class, SearchEngineLauncher.Initializer.class})

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@Tag("ContractTest") //Tag to exclude this Test from default execution (see: pom.xml -> excludedGroups)
public class DoiRepositoryImplContractIT extends DoiRepositoryImplAbstractTest {

  //TODO: How to start only relevant context? Only initialization of the application-test.properties for this test.
  // to remove not needed DB/ES initialization

  @MockBean
  private SwiftObjectStoreRepository objectStoreRepository;

  @BeforeAll
  void setupDoiDataCreator(@Autowired DoiRepositoryImpl doiRepository) throws IOException {
    this.doiRepository = doiRepository;
  }

}
