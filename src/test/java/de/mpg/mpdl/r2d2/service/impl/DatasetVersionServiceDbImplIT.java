package de.mpg.mpdl.r2d2.service.impl;

import de.mpg.mpdl.r2d2.db.DatasetVersionRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    //When
    DatasetVersion returnedDatasetVersion = this.datasetVersionServiceDbImpl.create(datasetVersion, r2d2Principal);

    //Then
    DatasetVersion datasetVersionFromDB = this.datasetVersionRepository.findLatestVersion(returnedDatasetVersion.getId());
    //QUESTION: Why getVersionID != getID for datasetVersion?
    DatasetVersionIto datasetVersionFromIndex = this.datasetVersionIndexDao.get(returnedDatasetVersion.getVersionId().toString());
    //FIXME: Maybe use the service or DB-access/SQL/Search-Index directly for verification and don't use the repositories!?

    List<DatasetVersion> createdDatasetVersions = List.of(returnedDatasetVersion, datasetVersionFromDB);

    assertThat(datasetVersionFromIndex).isNotNull();
    assertThat(createdDatasetVersions).doesNotContainNull();
    
    
    assertThat(createdDatasetVersions).extracting(DatasetVersion::getMetadata).extracting(DatasetVersionMetadata::getTitle)
        .containsOnly(datasetTitle);
    
    assertThat(datasetVersionFromIndex.getMetadata().getTitle()).isEqualTo(datasetTitle);
    
    assertThat(createdDatasetVersions).extracting(DatasetVersion::getVersionNumber).containsOnly(1);
  }

  @Test
  void testGetLatestDatasetVersionPublic() throws AuthorizationException, NotFoundException, R2d2TechnicalException {
    //Given
    Dataset dataset = DatasetBuilder.aDataset().id(UUID.randomUUID()).build();

    DatasetVersion datasetVersion1 = DatasetVersionBuilder.aDatasetVersion().dataset(dataset).state(Dataset.State.PUBLIC).build();
    DatasetVersion datasetVersion2 = DatasetVersionBuilder.aDatasetVersion().dataset(dataset).state(Dataset.State.PUBLIC).versionNumber(2)
        .publicationComment("publication Comment").build();
    DatasetVersion datasetVersion3 =
        DatasetVersionBuilder.aDatasetVersion().dataset(dataset).state(Dataset.State.PRIVATE).versionNumber(3).build();

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
