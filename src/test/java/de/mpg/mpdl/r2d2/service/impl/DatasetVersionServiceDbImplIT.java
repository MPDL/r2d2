package de.mpg.mpdl.r2d2.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Condition;
import org.assertj.core.util.Strings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mpg.mpdl.r2d2.exceptions.*;
import de.mpg.mpdl.r2d2.model.*;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.search.model.DatasetVersionIto;
import de.mpg.mpdl.r2d2.util.R2D2IntegrationTest;
import de.mpg.mpdl.r2d2.util.testdata.TestDataFactory;
import de.mpg.mpdl.r2d2.util.testdata.TestDataIndexer;
import de.mpg.mpdl.r2d2.util.testdata.TestDataManager;
import de.mpg.mpdl.r2d2.util.testdata.builder.AffiliationBuilder;
import de.mpg.mpdl.r2d2.util.testdata.builder.DatasetVersionMetadataBuilder;
import de.mpg.mpdl.r2d2.util.testdata.builder.PersonBuilder;
import de.mpg.mpdl.r2d2.util.testdata.builder.ReviewTokenBuilder;

/**
 * Integration test for DatasetVersionServiceDbImpl.
 *
 * @author helk
 */
@R2D2IntegrationTest
class DatasetVersionServiceDbImplIT {

  @Autowired
  private DatasetVersionServiceDbImpl datasetVersionServiceDbImpl;

  @Autowired
  private TestDataManager testDataManager;

  @Autowired
  private TestDataIndexer testDataIndexer;

  @Test
  void testCreateDatasetVersion() throws ValidationException, AuthorizationException, R2d2TechnicalException, InvalidStateException {
    //Given
    String datasetTitle = "datasetTitle";

    DatasetVersionMetadata metadata = DatasetVersionMetadataBuilder.aDatasetVersionMetadata().title(datasetTitle).build();
    DatasetVersion datasetVersion = TestDataFactory.aDatasetVersion().metadata(metadata).build();

    UserAccount userAccount = TestDataFactory.anUser().build();
    R2D2Principal r2d2Principal = TestDataFactory.aR2D2Principal().userAccount(userAccount).build();

    this.testDataManager.persist(userAccount);

    //When
    DatasetVersion returnedDatasetVersion = this.datasetVersionServiceDbImpl.create(datasetVersion, r2d2Principal);

    //Then
    List<DatasetVersion> datasetVersionsFromDB = this.testDataManager.findAll(DatasetVersion.class);
    List<DatasetVersionIto> datasetVersionsFromIndex = this.testDataIndexer.searchAll(DatasetVersionIto.class);

    Condition<DatasetVersion> doiNotEmpty = new Condition<>(dv -> !Strings.isNullOrEmpty(dv.getMetadata().getDoi()), "DOI not empty");
    assertThat(returnedDatasetVersion).isNotNull().has(doiNotEmpty)
        .extracting(dv -> dv.getMetadata().getTitle(), DatasetVersion::getVersionNumber).containsExactly(datasetTitle, 1);
    assertThat(datasetVersionsFromDB).hasSize(1).have(doiNotEmpty)
        .extracting(dv -> dv.getMetadata().getTitle(), DatasetVersion::getVersionNumber).containsOnly(tuple(datasetTitle, 1));
    assertThat(datasetVersionsFromIndex).hasSize(1).extracting(dvi -> dvi.getMetadata().getTitle(), DatasetVersionIto::getVersionNumber)
        .containsExactly(tuple(datasetTitle, 1));
  }

  @Test
  void testPublishDatasetVersion() throws InvalidStateException, R2d2TechnicalException, ValidationException, OptimisticLockingException,
      NotFoundException, AuthorizationException {
    //Given
    UserAccount userAccount = TestDataFactory.anUser().build();
    //FIXME: Simplify Grants/Authorization management in tests (and in prod code?)
    R2D2Principal r2d2Principal = TestDataFactory.aR2D2Principal().userAccount(userAccount).build();

    String datasetTitle = "datasetTitle";

    DatasetVersionMetadata metadata = DatasetVersionMetadataBuilder.aDatasetVersionMetadata().title(datasetTitle)
        .authors(Arrays.asList(PersonBuilder.aPerson().familyName("AuthorFamilyName").givenName("AuthorGivenName")
            .affiliations(Arrays.asList(AffiliationBuilder.anAffiliation().organization("Organization").build())).build()))
        .build();
    Dataset dataset = TestDataFactory.aDataset().creator(userAccount).build();
    DatasetVersion datasetVersion =
        TestDataFactory.aDatasetVersion().dataset(dataset).metadata(metadata).state(Dataset.State.PRIVATE).build();

    this.testDataManager.persist(userAccount, datasetVersion);

    //When
    DatasetVersion returnedDatasetVersion =
        this.datasetVersionServiceDbImpl.publish(datasetVersion.getId(), datasetVersion.getModificationDate(), r2d2Principal);

    //Then
    assertThat(returnedDatasetVersion).isNotNull().extracting(DatasetVersion::getState).isEqualTo(Dataset.State.PUBLIC);
    //TODO: Add further assertions
  }

  @Test
  void testUpdatePublicDatasetVersion() throws InvalidStateException, R2d2TechnicalException, ValidationException,
      OptimisticLockingException, NotFoundException, AuthorizationException {
    //Given
    UserAccount userAccount = TestDataFactory.anUser().build();
    R2D2Principal r2d2Principal = TestDataFactory.aR2D2Principal().userAccount(userAccount).build();

    String datasetTitle = "datasetTitle";

    DatasetVersionMetadata metadata = DatasetVersionMetadataBuilder.aDatasetVersionMetadata().title(datasetTitle).build();
    Dataset dataset = TestDataFactory.aDataset().creator(userAccount).build();
    DatasetVersion datasetVersion =
        TestDataFactory.aDatasetVersion().dataset(dataset).metadata(metadata).state(Dataset.State.PUBLIC).build();

    this.testDataManager.persist(userAccount, datasetVersion);

    String newDescription = "New Description";
    datasetVersion.getMetadata().setDescription(newDescription);

    //When
    DatasetVersion returnedDatasetVersion = this.datasetVersionServiceDbImpl.update(datasetVersion.getId(), datasetVersion, r2d2Principal);

    //Then
    assertThat(returnedDatasetVersion).isNotNull().extracting(dv -> dv.getMetadata().getDescription()).isEqualTo(newDescription);
    //TODO: Add further assertions
  }

  @Test
  void testWithdrawDatasetVersion() throws ValidationException, R2d2TechnicalException, AuthorizationException, OptimisticLockingException,
      NotFoundException, InvalidStateException {
    //Given
    UserAccount userAccount = TestDataFactory.anUser().build();
    R2D2Principal r2d2Principal = TestDataFactory.aR2D2Principal().userAccount(userAccount).build();

    Dataset dataset = TestDataFactory.aDataset().creator(userAccount).state(Dataset.State.PUBLIC).build();
    DatasetVersion datasetVersion = TestDataFactory.aDatasetVersion().dataset(dataset).state(Dataset.State.PUBLIC).build();

    this.testDataManager.persist(userAccount, datasetVersion);

    String withdrawComment = "Withdraw comment";

    //When
    this.datasetVersionServiceDbImpl.withdraw(datasetVersion.getId(), datasetVersion.getModificationDate(), withdrawComment, r2d2Principal);

    //Then
    List<DatasetVersion> datasetVersionsFromDB = this.testDataManager.findAll(DatasetVersion.class);
    Dataset datasetFromDB = this.testDataManager.find(Dataset.class, datasetVersion.getId());
    List<DatasetVersionIto> datasetVersionsFromIndex = this.testDataIndexer.searchAll(DatasetVersionIto.class);

    assertThat(datasetVersionsFromDB).isNotEmpty().extracting(DatasetVersion::getState).containsOnly(Dataset.State.WITHDRAWN);
    assertThat(datasetFromDB).isNotNull().extracting(Dataset::getState, Dataset::getWithdrawComment)
        .containsExactly(Dataset.State.WITHDRAWN, withdrawComment);
    assertThat(datasetVersionsFromIndex).hasSize(1).extracting(DatasetVersionIto::getState).containsExactly(Dataset.State.WITHDRAWN);
    //TODO: Add further assertions
  }

  @Test
  void testGetLatestDatasetVersionPublic() throws AuthorizationException, NotFoundException, R2d2TechnicalException {
    //Given
    Dataset dataset = TestDataFactory.aDataset().build();

    DatasetVersion datasetVersion1 = TestDataFactory.aDatasetVersion().dataset(dataset).state(Dataset.State.PUBLIC).build();
    DatasetVersion datasetVersion2 = TestDataFactory.aDatasetVersion().dataset(dataset).state(Dataset.State.PUBLIC).versionNumber(2)
        .publicationComment("publication Comment").build();
    DatasetVersion datasetVersion3 =
        TestDataFactory.aDatasetVersion().dataset(dataset).state(Dataset.State.PRIVATE).versionNumber(3).build();

    this.testDataManager.persist(datasetVersion1, datasetVersion2, datasetVersion3);

    //When
    DatasetVersion returnedDatasetVersion = this.datasetVersionServiceDbImpl.getLatest(dataset.getId(), null);

    //Then
    assertThat(returnedDatasetVersion).isNotNull().extracting(DatasetVersion::getVersionNumber).isEqualTo(2);
  }

  @Test
  void testGetLatestDatasetVersionPrivate() throws AuthorizationException, NotFoundException, R2d2TechnicalException {
    //Given
    UserAccount userAccount = TestDataFactory.anUser().build();
    R2D2Principal r2d2Principal = TestDataFactory.aR2D2Principal().userAccount(userAccount).build();

    Dataset dataset = TestDataFactory.aDataset().creator(userAccount).build();

    DatasetVersion datasetVersion1 = TestDataFactory.aDatasetVersion().dataset(dataset).state(Dataset.State.PUBLIC).build();
    DatasetVersion datasetVersion2 = TestDataFactory.aDatasetVersion().dataset(dataset).state(Dataset.State.PUBLIC).versionNumber(2)
        .publicationComment("publication Comment").build();
    DatasetVersion datasetVersion3 =
        TestDataFactory.aDatasetVersion().dataset(dataset).state(Dataset.State.PRIVATE).versionNumber(3).build();

    this.testDataManager.persist(userAccount, datasetVersion1, datasetVersion2, datasetVersion3);

    //When
    DatasetVersion returnedDatasetVersion = this.datasetVersionServiceDbImpl.getLatest(dataset.getId(), r2d2Principal);

    //Then
    assertThat(returnedDatasetVersion).isNotNull().extracting(DatasetVersion::getVersionNumber).isEqualTo(3);
  }

  @Test
  void testGetDatasetVersion() throws AuthorizationException, NotFoundException, R2d2TechnicalException {
    //Given
    UserAccount userAccount = TestDataFactory.anUser().build();
    R2D2Principal r2d2Principal = TestDataFactory.aR2D2Principal().userAccount(userAccount).build();

    Dataset dataset = TestDataFactory.aDataset().creator(userAccount).build();

    DatasetVersion datasetVersion1 = TestDataFactory.aDatasetVersion().dataset(dataset).build();
    DatasetVersion datasetVersion2 = TestDataFactory.aDatasetVersion().dataset(dataset).versionNumber(2).build();
    DatasetVersion datasetVersion3 = TestDataFactory.aDatasetVersion().dataset(dataset).versionNumber(3).build();

    this.testDataManager.persist(userAccount, datasetVersion1, datasetVersion2, datasetVersion3);

    //When
    DatasetVersion returnedDatasetVersion = this.datasetVersionServiceDbImpl.get(datasetVersion2.getVersionId(), r2d2Principal);

    //Then
    assertThat(returnedDatasetVersion).isNotNull().extracting(DatasetVersion::getVersionNumber, DatasetVersion::getVersionId)
        .containsExactly(2, datasetVersion2.getVersionId());
  }

  @Test
  void testGetFileForDataset() throws AuthorizationException, R2d2TechnicalException, NotFoundException {
    //Given
    UserAccount userAccount = TestDataFactory.anUser().build();
    R2D2Principal r2d2Principal = TestDataFactory.aR2D2Principal().userAccount(userAccount).build();

    Dataset dataset = TestDataFactory.aDataset().creator(userAccount).build();
    DatasetVersion datasetVersion = TestDataFactory.aDatasetVersion().dataset(dataset).build();

    File file = TestDataFactory.aFile().datasets(Collections.singleton(datasetVersion)).build();

    this.testDataManager.persist(userAccount, datasetVersion, file);

    //When
    File returnedFile = this.datasetVersionServiceDbImpl.getFileForDataset(datasetVersion.getVersionId(), file.getId(), r2d2Principal);

    //Then
    assertThat(returnedFile).isNotNull().extracting(File::getId).isEqualTo(file.getId());
  }

  @Test
  void testAddFile() throws ValidationException, R2d2TechnicalException, AuthorizationException, OptimisticLockingException,
      NotFoundException, InvalidStateException {
    //Given
    UserAccount userAccount = TestDataFactory.anUser().build();
    R2D2Principal r2d2Principal = TestDataFactory.aR2D2Principal().userAccount(userAccount).build();

    Dataset dataset = TestDataFactory.aDataset().creator(userAccount).build();
    DatasetVersion datasetVersion = TestDataFactory.aDatasetVersion().dataset(dataset).build();

    File file = TestDataFactory.aFile().creator(userAccount).state(File.UploadState.COMPLETE).build();

    this.testDataManager.persist(userAccount, datasetVersion, file);

    //When
    File returnedFile =
        this.datasetVersionServiceDbImpl.addFile(dataset.getId(), file.getId(), datasetVersion.getModificationDate(), r2d2Principal);

    //Then
    List<File> files = this.testDataManager.findAll(File.class);

    assertThat(returnedFile).isNotNull().extracting(File::getId).isEqualTo(file.getId());
    assertThat(files).hasSize(1).extracting(File::getId).containsOnly(file.getId());
  }

  @Test
  void testRemoveFile() throws ValidationException, R2d2TechnicalException, AuthorizationException, OptimisticLockingException,
      NotFoundException, InvalidStateException {
    //Given
    UserAccount userAccount = TestDataFactory.anUser().build();
    R2D2Principal r2d2Principal = TestDataFactory.aR2D2Principal().userAccount(userAccount).build();

    Dataset dataset = TestDataFactory.aDataset().creator(userAccount).build();
    DatasetVersion datasetVersion = TestDataFactory.aDatasetVersion().dataset(dataset).build();

    File file = TestDataFactory.aFile().state(File.UploadState.ATTACHED).datasets(Collections.singleton(datasetVersion)).build();

    this.testDataManager.persist(userAccount, datasetVersion, file);

    //When
    File returnedFile =
        this.datasetVersionServiceDbImpl.removeFile(dataset.getId(), file.getId(), datasetVersion.getModificationDate(), r2d2Principal);

    //Then
    List<File> files = this.testDataManager.findAll(File.class);

    assertThat(returnedFile).isNotNull().extracting(File::getId).isEqualTo(file.getId());
    assertThat(files).hasSize(1).extracting(File::getId).containsOnly(file.getId());
    assertThat(files).flatExtracting(File::getDatasets).extracting(DatasetVersion::getId).isEmpty();
  }

  @Test
  void testUpdateFiles() throws ValidationException, R2d2TechnicalException, AuthorizationException, OptimisticLockingException,
      NotFoundException, InvalidStateException {
    //Given
    UserAccount userAccount = TestDataFactory.anUser().build();
    R2D2Principal r2d2Principal = TestDataFactory.aR2D2Principal().userAccount(userAccount).build();

    Dataset dataset = TestDataFactory.aDataset().creator(userAccount).build();
    DatasetVersion datasetVersion = TestDataFactory.aDatasetVersion().dataset(dataset).build();

    File attachedFileToRemove = TestDataFactory.aFile().creator(userAccount).state(File.UploadState.ATTACHED)
        .datasets(Collections.singleton(datasetVersion)).build();
    File unattachedFileToAdd = TestDataFactory.aFile().creator(userAccount).state(File.UploadState.COMPLETE).build();
    File attachedFileToRemain = TestDataFactory.aFile().creator(userAccount).state(File.UploadState.ATTACHED)
        .datasets(Collections.singleton(datasetVersion)).build();

    this.testDataManager.persist(userAccount, datasetVersion, attachedFileToRemove, unattachedFileToAdd, attachedFileToRemain);

    //When
    List<File> returnedFiles = this.datasetVersionServiceDbImpl.updateFiles(dataset.getId(),
        Arrays.asList(unattachedFileToAdd.getId(), attachedFileToRemain.getId()), datasetVersion.getModificationDate(), r2d2Principal);

    //Then
    File removedFile = this.testDataManager.find(File.class, attachedFileToRemove.getId());
    File addedFile = this.testDataManager.find(File.class, unattachedFileToAdd.getId());
    File remainingFile = this.testDataManager.find(File.class, attachedFileToRemain.getId());

    assertThat(returnedFiles).isNotNull().extracting(File::getId).containsOnly(attachedFileToRemove.getId(), unattachedFileToAdd.getId());
    assertThat(removedFile.getDatasets()).extracting(DatasetVersion::getId).isEmpty();
    assertThat(addedFile.getDatasets()).extracting(DatasetVersion::getId).containsOnly(datasetVersion.getId());
    assertThat(remainingFile.getDatasets()).extracting(DatasetVersion::getId).containsOnly(datasetVersion.getId());
  }

  @Test
  void testCreateReviewToken() throws ValidationException, R2d2TechnicalException, AuthorizationException, OptimisticLockingException,
      NotFoundException, InvalidStateException {
    //Given
    UserAccount userAccount = TestDataFactory.anUser().build();
    R2D2Principal r2d2Principal = TestDataFactory.aR2D2Principal().userAccount(userAccount).build();

    Dataset dataset = TestDataFactory.aDataset().creator(userAccount).build();
    DatasetVersion datasetVersion = TestDataFactory.aDatasetVersion().dataset(dataset).build();

    this.testDataManager.persist(userAccount, datasetVersion);

    //When
    ReviewToken returnedReviewToken = this.datasetVersionServiceDbImpl.createReviewToken(dataset.getId(), r2d2Principal);

    //Then
    List<ReviewToken> reviewTokens = this.testDataManager.findAll(ReviewToken.class);

    Condition<ReviewToken> tokenStringNotEmpty = new Condition<>(t -> !Strings.isNullOrEmpty(t.getToken()), "Token String not empty");
    assertThat(returnedReviewToken).isNotNull().has(tokenStringNotEmpty).extracting(ReviewToken::getDataset).isEqualTo(dataset.getId());
    assertThat(reviewTokens).hasSize(1).first().has(tokenStringNotEmpty).extracting(ReviewToken::getDataset).isEqualTo(dataset.getId());
  }

  @Test
  void testGetReviewToken() throws ValidationException, R2d2TechnicalException, AuthorizationException, OptimisticLockingException,
      NotFoundException, InvalidStateException {
    //Given
    UserAccount userAccount = TestDataFactory.anUser().build();
    R2D2Principal r2d2Principal = TestDataFactory.aR2D2Principal().userAccount(userAccount).build();

    Dataset dataset = TestDataFactory.aDataset().creator(userAccount).build();
    DatasetVersion datasetVersion = TestDataFactory.aDatasetVersion().dataset(dataset).build();

    this.testDataManager.persist(userAccount, datasetVersion);

    String tokenString = "TheTokenString";
    ReviewToken reviewToken = ReviewTokenBuilder.aReviewToken().dataset(dataset.getId()).token(tokenString).build();

    this.testDataManager.persist(reviewToken);

    //When
    ReviewToken returnedReviewToken = this.datasetVersionServiceDbImpl.getReviewToken(dataset.getId(), r2d2Principal);

    //Then
    assertThat(returnedReviewToken).isNotNull().extracting(ReviewToken::getToken, ReviewToken::getDataset).containsExactly(tokenString,
        dataset.getId());
  }

}
