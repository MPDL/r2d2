package de.mpg.mpdl.r2d2.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;

import de.mpg.mpdl.r2d2.db.DatasetVersionRepository;
import de.mpg.mpdl.r2d2.db.UserAccountRepository;
import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.InvalidStateException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.exceptions.ValidationException;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.DatasetVersionMetadata;
import de.mpg.mpdl.r2d2.model.Person;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.model.aa.UserAccount.Role;
import de.mpg.mpdl.r2d2.model.aa.UserAccountRO;
import de.mpg.mpdl.r2d2.search.dao.DatasetVersionDaoEs;
import de.mpg.mpdl.r2d2.util.BaseIntegrationTest;
import de.mpg.mpdl.r2d2.util.Utils;

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

  @Autowired
  private DatasetVersionDaoEs datasetVersionIndexDao;

  @Autowired
  private DatasetVersionServiceDbImpl datasetVersionServiceDbImpl;

  @Test
  public void testCreateDatasetVersion() throws ValidationException, AuthorizationException, R2d2TechnicalException, InvalidStateException {
    //Given
    DatasetVersion datasetVersion = new DatasetVersion();
    DatasetVersionMetadata datasetVersionMetadata = new DatasetVersionMetadata();
    String datasetTitle = "datasetTitle";
    datasetVersionMetadata.setTitle(datasetTitle);
    datasetVersion.setMetadata(datasetVersionMetadata);

    R2D2Principal r2d2Principal = new R2D2Principal("username", "pw", new ArrayList<GrantedAuthority>());
    UserAccount userAccount = new UserAccount();
    Person person = new Person();
    person.setFamilyName("FamilyName");
    person.setGivenName("GivenName");
    userAccount.setPerson(person);
    r2d2Principal.setUserAccount(userAccount);

    OffsetDateTime currentDateTime = Utils.generateCurrentDateTimeForDatabase();
    userAccount.setCreator(new UserAccountRO(userAccount));
    userAccount.setModifier(new UserAccountRO(userAccount));
    userAccount.setCreationDate(currentDateTime);
    userAccount.setModificationDate(currentDateTime);
    userAccount.getRoles().add(Role.USER);
    //FIXME: Maybe don't use Repositories to create test data. Instead use DB access directly: Spring Data repository, or the EntityManager or the JdbcTemplate!? 
    this.userAccountRepository.save(userAccount);

    //When
    DatasetVersion createdDatasetVersion = this.datasetVersionServiceDbImpl.create(datasetVersion, r2d2Principal);

    //Then
    //TODO: Assert the DatasetVersion returned by the method under test

    DatasetVersion returnedDatasetVersion = this.datasetVersionRepository.findLatestVersion(createdDatasetVersion.getId());
    DatasetVersion returnedDatasetVersionFromIndex = this.datasetVersionIndexDao.get(createdDatasetVersion.getId().toString());
    //FIXME: Maybe use the service or DB-access/SQL/Search-Index directly for verification and don't use the repositories!?

    assertThat(returnedDatasetVersion).isNotNull().extracting("metadata").extracting("title").isEqualTo(datasetTitle);
    assertThat(returnedDatasetVersion).extracting("versionNumber").isEqualTo(1);
    assertThat(returnedDatasetVersionFromIndex).isNotNull().extracting("metadata").extracting("title").isEqualTo(datasetTitle);
    assertThat(returnedDatasetVersionFromIndex).extracting("versionNumber").isEqualTo(1);
  }

}
