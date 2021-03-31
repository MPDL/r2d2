package de.mpg.mpdl.r2d2.service.impl;

import de.mpg.mpdl.r2d2.db.DatasetVersionRepository;
import de.mpg.mpdl.r2d2.db.UserAccountRepository;
import de.mpg.mpdl.r2d2.exceptions.*;
import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.DatasetVersionMetadata;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.search.dao.DatasetVersionDaoEs;
import de.mpg.mpdl.r2d2.search.model.DatasetVersionIto;
import de.mpg.mpdl.r2d2.util.BaseIntegrationTest;
import de.mpg.mpdl.r2d2.util.testdata.TestDataFactory;
import de.mpg.mpdl.r2d2.util.testdata.builder.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for DatasetVersionServiceDbImpl.
 * 
 * @author helk
 *
 */
public class DatasetVersionServiceDbImplIT extends BaseIntegrationTest {

  @Autowired
  private DatasetVersionServiceDbImpl datasetVersionServiceDbImpl;

  @Autowired
  private UserAccountRepository userAccountRepository;

  @Autowired
  private DatasetVersionRepository datasetVersionRepository;

  //TODO: Use LatestDatasetVersionDaoImpl instead of PublicDatasetVersionDaoImpl !?
  @Autowired
  @Qualifier("PublicDatasetVersionDaoImpl")
  private DatasetVersionDaoEs datasetVersionIndexDao;

  @Test
  void testCreateDatasetVersion() throws ValidationException, AuthorizationException, R2d2TechnicalException, InvalidStateException {
    //Given
    String datasetTitle = "datasetTitle";

    DatasetVersionMetadata metadata = DatasetVersionMetadataBuilder.aDatasetVersionMetadata().title(datasetTitle).build();
    DatasetVersion datasetVersion = TestDataFactory.aDatasetVersionWithCreationAndModificationDate().metadata(metadata).build();

    UserAccount userAccount = TestDataFactory.anUser().build();
    R2D2Principal r2d2Principal =
        R2D2PrincipalBuilder.aR2D2Principal("username", "pw", new ArrayList<GrantedAuthority>()).userAccount(userAccount).build();

    this.userAccountRepository.save(userAccount);

    //When
    DatasetVersion returnedDatasetVersion = this.datasetVersionServiceDbImpl.create(datasetVersion, r2d2Principal);

    //Then
    DatasetVersion datasetVersionFromDB = this.datasetVersionRepository.findLatestVersion(returnedDatasetVersion.getId());
    //QUESTION: Why getVersionID != getID for datasetVersion?
    DatasetVersionIto datasetVersionFromIndex = this.datasetVersionIndexDao.get(returnedDatasetVersion.getVersionId().toString());
    //FIXME: Maybe use the service or DB-access/SQL/Search-Index directly for verification and don't use the repositories!?

    List<DatasetVersion> createdDatasetVersions = List.of(returnedDatasetVersion, datasetVersionFromDB);

    assertThat(createdDatasetVersions).doesNotContainNull();
    assertThat(createdDatasetVersions).extracting(DatasetVersion::getMetadata).extracting(DatasetVersionMetadata::getTitle)
        .containsOnly(datasetTitle);
    assertThat(createdDatasetVersions).extracting(DatasetVersion::getMetadata).extracting(DatasetVersionMetadata::getDoi).isNotEmpty();
    assertThat(createdDatasetVersions).extracting(DatasetVersion::getVersionNumber).containsOnly(1);

    assertThat(datasetVersionFromIndex).isNotNull();
    assertThat(datasetVersionFromIndex).extracting(DatasetVersionIto::getMetadata).extracting(DatasetVersionMetadata::getTitle)
        .isEqualTo(datasetTitle);
    assertThat(datasetVersionFromIndex).extracting(DatasetVersionIto::getVersionNumber).isEqualTo(1);
  }

  @Test
  void testPublishDatasetVersion() throws InvalidStateException, R2d2TechnicalException, ValidationException, OptimisticLockingException,
      NotFoundException, AuthorizationException {
    //Given
    UserAccount userAccount = TestDataFactory.anUser().build();

    String datasetTitle = "datasetTitle";

    DatasetVersionMetadata metadata = DatasetVersionMetadataBuilder.aDatasetVersionMetadata()
        .title(datasetTitle).authors(Arrays.asList(PersonBuilder.aPerson().
            familyName("AuthorFamilyName").givenName("AuthorGivenName")
            .affiliations(Arrays.asList(AffiliationBuilder.anAffiliation()
                .organization("Organization").build())).build()))
        .build();
    Dataset dataset = TestDataFactory.aDatasetWithCreationAndModificationDate().creator(userAccount).build();
    DatasetVersion datasetVersion =
        TestDataFactory.aDatasetVersionWithCreationAndModificationDate()
            .dataset(dataset).creator(userAccount).metadata(metadata).state(Dataset.State.PRIVATE).build();

    this.userAccountRepository.save(userAccount);
    DatasetVersion savedDatasetVersion = this.datasetVersionRepository.save(datasetVersion);

    //FIXME: Simplify Grants/Authorization management in tests (and in prod code?)
    userAccount.setGrants(Collections.singletonList(GrantBuilder.aGrant()
        .role(UserAccount.Role.USER).dataset(savedDatasetVersion.getDataset().getId()).build()));
    R2D2Principal r2d2Principal =
        R2D2PrincipalBuilder.aR2D2Principal("username", "pw", new ArrayList<GrantedAuthority>()).userAccount(userAccount).build();

    //When
    DatasetVersion returnedDatasetVerion =
        this.datasetVersionServiceDbImpl.publish(savedDatasetVersion.getId(), savedDatasetVersion.getModificationDate(), r2d2Principal);

    //Then
    assertThat(returnedDatasetVerion).isNotNull();
    assertThat(returnedDatasetVerion).extracting(DatasetVersion::getState).isEqualTo(Dataset.State.PUBLIC);
    //TODO: Add further assertions
  }

  @Test
  void testUpdatePublicDatasetVersion()
      throws InvalidStateException, R2d2TechnicalException, ValidationException, OptimisticLockingException, NotFoundException,
      AuthorizationException {
    //Given
    UserAccount userAccount = TestDataFactory.anUser().build();

    String datasetTitle = "datasetTitle";

    DatasetVersionMetadata metadata = DatasetVersionMetadataBuilder.aDatasetVersionMetadata()
        .title(datasetTitle).build();
    Dataset dataset = TestDataFactory.aDatasetWithCreationAndModificationDate().creator(userAccount).build();
    DatasetVersion existingDatasetVersion =
        TestDataFactory.aDatasetVersionWithCreationAndModificationDate()
            .dataset(dataset).creator(userAccount).metadata(metadata).state(Dataset.State.PUBLIC).build();

    this.userAccountRepository.save(userAccount);
    DatasetVersion savedDatasetVersion = this.datasetVersionRepository.save(existingDatasetVersion);

    userAccount.setGrants(Collections.singletonList(GrantBuilder.aGrant()
        .role(UserAccount.Role.USER).dataset(savedDatasetVersion.getDataset().getId()).build()));
    R2D2Principal r2d2Principal =
        R2D2PrincipalBuilder.aR2D2Principal("username", "pw", new ArrayList<GrantedAuthority>()).userAccount(userAccount).build();

    DatasetVersion updatedDatasetVerion = savedDatasetVersion;
    DatasetVersionMetadata upDatedMetadata = updatedDatasetVerion.getMetadata();
    String updatedDescription = "New updated Description";
    upDatedMetadata.setDescription(updatedDescription);
    updatedDatasetVerion.setMetadata(upDatedMetadata);

    //When
    DatasetVersion returnedDatasetVersion =
        this.datasetVersionServiceDbImpl.update(savedDatasetVersion.getId(), updatedDatasetVerion, r2d2Principal);

    //Then
    assertThat(returnedDatasetVersion).isNotNull();
    assertThat(returnedDatasetVersion).extracting(DatasetVersion::getMetadata)
        .extracting(DatasetVersionMetadata::getDescription).isEqualTo(updatedDescription);
    //TODO: Add further assertions
  }

  @Test
  void testGetLatestDatasetVersionPublic() throws AuthorizationException, NotFoundException, R2d2TechnicalException {
    //Given
    Dataset dataset = TestDataFactory.aDatasetWithCreationAndModificationDate().id(UUID.randomUUID()).build();

    DatasetVersion datasetVersion1 =
        TestDataFactory.aDatasetVersionWithCreationAndModificationDate().dataset(dataset).state(Dataset.State.PUBLIC).build();
    DatasetVersion datasetVersion2 = TestDataFactory.aDatasetVersionWithCreationAndModificationDate().dataset(dataset)
        .state(Dataset.State.PUBLIC).versionNumber(2).publicationComment("publication Comment").build();
    DatasetVersion datasetVersion3 = TestDataFactory.aDatasetVersionWithCreationAndModificationDate().dataset(dataset)
        .state(Dataset.State.PRIVATE).versionNumber(3).build();

    R2D2Principal r2D2Principal = R2D2PrincipalBuilder.aR2D2Principal("username", "pw", new ArrayList<>())
        .userAccount(UserAccountBuilder.anUserAccount().build()).build();

    datasetVersionRepository.save(datasetVersion1);
    datasetVersionRepository.save(datasetVersion2);
    datasetVersionRepository.save(datasetVersion3);

    //When
    DatasetVersion returnedDatasetVersion = this.datasetVersionServiceDbImpl.getLatest(dataset.getId(), r2D2Principal);

    //Then
    assertThat(returnedDatasetVersion).isNotNull();
    assertThat(returnedDatasetVersion.getVersionNumber()).isEqualTo(2);
  }

}
