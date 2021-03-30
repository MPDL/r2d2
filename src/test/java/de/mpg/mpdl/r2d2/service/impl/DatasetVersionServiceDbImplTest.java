package de.mpg.mpdl.r2d2.service.impl;

import de.mpg.mpdl.r2d2.aa.AuthorizationService;
import de.mpg.mpdl.r2d2.db.AuditRepository;
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
import de.mpg.mpdl.r2d2.model.validation.SaveConstraintGroup;
import de.mpg.mpdl.r2d2.search.service.impl.IndexingService;
import de.mpg.mpdl.r2d2.service.doi.DoiRepository;
import de.mpg.mpdl.r2d2.service.storage.SwiftObjectStoreRepository;
import de.mpg.mpdl.r2d2.util.DtoMapper;
import de.mpg.mpdl.r2d2.util.testdata.builder.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;

import javax.validation.Validator;
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
  private AuditRepository auditRepository;

  @Mock
  private IndexingService indexingService;

  @Mock
  private SwiftObjectStoreRepository objectStoreRepository;

  @Mock
  private AuthorizationService aaService;

  @Mock
  private Validator beanValidator;

  @InjectMocks
  private DatasetVersionServiceDbImpl datasetVersionServiceDbImpl = new DatasetVersionServiceDbImpl();

  @Test
  public void testCreateMethodDelegationAndControlFlow()
      throws ValidationException, AuthorizationException, R2d2TechnicalException, InvalidStateException {
    //Given
    DatasetVersionMetadata datasetVersionMetadata = DatasetVersionMetadataBuilder.aDatasetVersionMetadata().title("datasetTitle").build();
    DatasetVersion datasetVersion = DatasetVersionBuilder.aDatasetVersion().metadata(datasetVersionMetadata).build();

    UserAccount userAccount =
        UserAccountBuilder.anUserAccount().person(PersonBuilder.aPerson().givenName("GivenName").familyName("FamilyName").build()).build();
    R2D2Principal r2d2Principal =
        R2D2PrincipalBuilder.aR2D2Principal("username", "pw", new ArrayList<GrantedAuthority>()).userAccount(userAccount).build();

    DatasetVersion savedDatasetVersion =
        DatasetVersionBuilder.aDatasetVersion().dataset(DatasetBuilder.aDataset().id(UUID.randomUUID()).build()).versionNumber(1).build();
    Mockito.when(this.datasetVersionRepository.saveAndFlush(Mockito.any())).thenReturn(savedDatasetVersion);

    //When
    this.datasetVersionServiceDbImpl.create(datasetVersion, r2d2Principal);

    //Then
    //TODO: Add Check for Audit Log call
    InOrder inOrder = Mockito.inOrder(aaService, beanValidator, datasetVersionRepository, indexingService);

    ArgumentCaptor<Object> objectArguments = ArgumentCaptor.forClass(Object.class);
    ArgumentCaptor<DatasetVersion> datasetVersionArgument = ArgumentCaptor.forClass(DatasetVersion.class);

    inOrder.verify(aaService).checkAuthorization(Mockito.eq(DatasetVersionServiceDbImpl.class.getCanonicalName()), Mockito.eq("create"),
        objectArguments.capture());
    assertThat(objectArguments.getAllValues()).size().isEqualTo(2);
    assertThat(objectArguments.getAllValues()).first().isEqualTo(r2d2Principal);
    assertThat(objectArguments.getAllValues()).last().isInstanceOf(DatasetVersion.class);

    inOrder.verify(beanValidator).validate(datasetVersionArgument.capture(), Mockito.eq(SaveConstraintGroup.class));
    assertThat(datasetVersionArgument.getValue()).extracting(DatasetVersion::getMetadata).isEqualTo(datasetVersionMetadata);

    inOrder.verify(datasetVersionRepository).saveAndFlush(datasetVersionArgument.capture());
    assertThat(datasetVersionArgument.getValue()).extracting(DatasetVersion::getMetadata).isEqualTo(datasetVersionMetadata);

    inOrder.verify(indexingService).reindexDataset(Mockito.eq(savedDatasetVersion.getId()), Mockito.eq(true));
  }

}
