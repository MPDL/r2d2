package de.mpg.mpdl.r2d2.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.mpg.mpdl.r2d2.db.DatasetVersionRepository;
import de.mpg.mpdl.r2d2.db.UserAccountRepository;
import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.InvalidStateException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.exceptions.ValidationException;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.model.aa.UserAccount.Role;
import de.mpg.mpdl.r2d2.search.dao.DatasetVersionDaoEs;
import de.mpg.mpdl.r2d2.util.BaseIntegrationTest;
import de.mpg.mpdl.r2d2.util.testdata.TestDataBuilder;

/**
 * Integration test for DatasetVersionServiceDbImpl.
 * 
 * @author helk
 *
 */
public class DatasetVersionServiceDbImplIT extends BaseIntegrationTest {

  @Autowired
  private TestDataBuilder testDataBuilder;

  @Autowired
  private UserAccountRepository userAccountRepository;

  @Autowired
  private DatasetVersionRepository datasetVersionRepository;

  //TODO: Use LatestDatasetVersionDaoImpl instead of PublicDatasetVersionDaoImpl !?
  @Autowired
  @Qualifier("PublicDatasetVersionDaoImpl")
  private DatasetVersionDaoEs datasetVersionIndexDao;

  @Autowired
  private DatasetVersionServiceDbImpl datasetVersionServiceDbImpl;

  @Test
  public void testCreateDatasetVersion() throws ValidationException, AuthorizationException, R2d2TechnicalException, InvalidStateException {
    //Given
    String datasetTitle = "datasetTitle";
    DatasetVersion datasetVersion =
        testDataBuilder.newDatasetVersion().setMetadata(datasetTitle).setcurrentCreationAndModificationDate().create();
    UserAccount userAccount = testDataBuilder.newUserAccount().setPerson("FamilyName", "GivenName").setCreatorAndModifier()
        .setcurrentCreationAndModificationDate().setRole(Role.USER).persist();
    R2D2Principal r2d2Principal = testDataBuilder.newR2D2Principal("username", "pw").setUserAccount(userAccount).create();

    //When
    DatasetVersion createdDatasetVersion = this.datasetVersionServiceDbImpl.create(datasetVersion, r2d2Principal);

    //Then
    //TODO: Assert the DatasetVersion returned by the method under test

    DatasetVersion returnedDatasetVersion = this.datasetVersionRepository.findLatestVersion(createdDatasetVersion.getId());
    //QUESTION: Why getVersionID != getID for datasetVersion?
    DatasetVersion returnedDatasetVersionFromIndex = this.datasetVersionIndexDao.get(createdDatasetVersion.getVersionId().toString());
    //FIXME: Maybe use the service or DB-access/SQL/Search-Index directly for verification and don't use the repositories!?

    assertThat(returnedDatasetVersion).isNotNull().extracting("metadata").extracting("title").isEqualTo(datasetTitle);
    assertThat(returnedDatasetVersion).extracting("versionNumber").isEqualTo(1);
    assertThat(returnedDatasetVersionFromIndex).isNotNull().extracting("metadata").extracting("title").isEqualTo(datasetTitle);
    assertThat(returnedDatasetVersionFromIndex).extracting("versionNumber").isEqualTo(1);
  }

}
