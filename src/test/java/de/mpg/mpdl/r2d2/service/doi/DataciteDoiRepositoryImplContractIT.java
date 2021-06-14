package de.mpg.mpdl.r2d2.service.doi;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import de.mpg.mpdl.r2d2.service.storage.SwiftObjectStoreRepository;
import de.mpg.mpdl.r2d2.util.R2D2IntegrationTest;

@R2D2IntegrationTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@Tag("ContractTest") //Tag to exclude this Test from default execution (see: pom.xml -> excludedGroups)
public class DataciteDoiRepositoryImplContractIT extends DataciteDoiRepositoryImplAbstractTest {

  //TODO: How to start only relevant context? Only initialization of the application-test.properties for this test.
  // to remove not needed DB/ES initialization

  @MockBean
  private SwiftObjectStoreRepository objectStoreRepository;

  @BeforeAll
  void setupDoiDataCreator(@Autowired DataciteDoiRepositoryImpl doiRepository) throws IOException {
    this.doiRepository = doiRepository;
  }

}
