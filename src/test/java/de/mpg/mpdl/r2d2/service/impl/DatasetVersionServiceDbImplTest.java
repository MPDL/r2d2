package de.mpg.mpdl.r2d2.service.impl;

import de.mpg.mpdl.r2d2.aa.AuthorizationService;
import de.mpg.mpdl.r2d2.db.DatasetVersionRepository;
import de.mpg.mpdl.r2d2.db.FileRepository;
import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.InvalidStateException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.exceptions.ValidationException;
import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.DatasetVersionMetadata;
import de.mpg.mpdl.r2d2.model.Person;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.search.service.impl.IndexingService;
import de.mpg.mpdl.r2d2.service.storage.SwiftObjectStoreRepository;
import de.mpg.mpdl.r2d2.util.DtoMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for DatasetVersionServiceDbImpl.
 * 
 * @author helk
 *
 */
@ExtendWith(MockitoExtension.class)
public class DatasetVersionServiceDbImplTest {

  @Mock
  private DatasetVersionRepository datasetVersionRepository;

  @Mock
  private FileRepository fileRepository;

  @Mock
  private IndexingService indexingService;

  @Mock
  private SwiftObjectStoreRepository objectStoreRepository;

  @Mock
  private AuthorizationService aaService;

  @InjectMocks
  private DatasetVersionServiceDbImpl datasetVersionServiceDbImpl = new DatasetVersionServiceDbImpl();

  @Test
  public void testCreateMethodDelegationAndControlFlow()
      throws ValidationException, AuthorizationException, R2d2TechnicalException, InvalidStateException {
    //Given
    DatasetVersionMetadata datasetVersionMetadata = DatasetVersionMetadata.builder().title("datasetTitle").build();
    DatasetVersion datasetVersion = DatasetVersion.builder().metadata(datasetVersionMetadata).build();

    UserAccount userAccount =
        UserAccount.builder().person(Person.builder().givenName("GivenName").familyName("FamilyName").build()).build();
    R2D2Principal r2d2Principal = new R2D2Principal("username", "pw", new ArrayList<GrantedAuthority>());
    r2d2Principal.setUserAccount(userAccount);

    DatasetVersion savedDatasetVersion =
        DatasetVersion.builder().dataset(Dataset.builder().id(UUID.randomUUID()).build()).versionNumber(1).build();
    Mockito.when(this.datasetVersionRepository.saveAndFlush(Mockito.any())).thenReturn(savedDatasetVersion);

    //When
    this.datasetVersionServiceDbImpl.create(datasetVersion, r2d2Principal);

    //Then
    InOrder inOrder = Mockito.inOrder(aaService, datasetVersionRepository, indexingService);

    ArgumentCaptor<Object> objectArguments = ArgumentCaptor.forClass(Object.class);
    ArgumentCaptor<DatasetVersion> datasetVersionArgument = ArgumentCaptor.forClass(DatasetVersion.class);

    inOrder.verify(aaService).checkAuthorization(Mockito.eq(DatasetVersionServiceDbImpl.class.getCanonicalName()), Mockito.eq("create"),
        objectArguments.capture());
    assertThat(objectArguments.getAllValues()).size().isEqualTo(2);
    assertThat(objectArguments.getAllValues()).first().isEqualTo(r2d2Principal);
    assertThat(objectArguments.getAllValues()).last().isInstanceOf(DatasetVersion.class);

    inOrder.verify(datasetVersionRepository).saveAndFlush(datasetVersionArgument.capture());
    assertThat(datasetVersionArgument.getValue()).extracting("metadata").isEqualTo(datasetVersionMetadata);

    inOrder.verify(indexingService).reindexDataset(Mockito.eq(savedDatasetVersion.getId()), Mockito.eq(true));
  }

}
