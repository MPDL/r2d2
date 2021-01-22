package de.mpg.mpdl.r2d2.service.impl;

import de.mpg.mpdl.r2d2.db.DatasetVersionRepository;
import de.mpg.mpdl.r2d2.db.UserAccountRepository;
import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.InvalidStateException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.exceptions.ValidationException;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.DatasetVersionMetadata;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.search.dao.DatasetVersionDaoEs;
import de.mpg.mpdl.r2d2.util.BaseIntegrationTest;
import de.mpg.mpdl.r2d2.util.testdata.TestDataFactory;
import de.mpg.mpdl.r2d2.util.testdata.builder.DatasetVersionMetadataBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for DatasetVersionServiceDbImpl.
 * 
 * @author helk
 *
 */
public class DatasetVersionServiceDbImplIT extends BaseIntegrationTest {

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

    DatasetVersionMetadata metadata = DatasetVersionMetadataBuilder.aDatasetVersionMetadata().title(datasetTitle).build();
    DatasetVersion datasetVersion = TestDataFactory.aDatasetVersionWithCreationAndModificationDate().metadata(metadata).build();

    UserAccount userAccount = TestDataFactory.aUser().build();
    R2D2Principal r2d2Principal = new R2D2Principal("username", "pw", new ArrayList<GrantedAuthority>());
    r2d2Principal.setUserAccount(userAccount);

    //When
    DatasetVersion returnedDatasetVersion = this.datasetVersionServiceDbImpl.create(datasetVersion, r2d2Principal);

    //Then
    DatasetVersion datasetVersionFromDB = this.datasetVersionRepository.findLatestVersion(returnedDatasetVersion.getId());
    //QUESTION: Why getVersionID != getID for datasetVersion?
    DatasetVersion datasetVersionFromIndex = this.datasetVersionIndexDao.get(returnedDatasetVersion.getVersionId().toString());
    //FIXME: Maybe use the service or DB-access/SQL/Search-Index directly for verification and don't use the repositories!?

    List<DatasetVersion> createdDatasetVersions = List.of(returnedDatasetVersion, datasetVersionFromDB, datasetVersionFromIndex);

    assertThat(createdDatasetVersions).doesNotContainNull();
    assertThat(createdDatasetVersions).extracting("metadata").extracting("title").containsOnly(datasetTitle);
    assertThat(createdDatasetVersions).extracting("versionNumber").containsOnly(1);
  }

}
