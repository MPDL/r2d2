package de.mpg.mpdl.r2d2.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import de.mpg.mpdl.r2d2.exceptions.*;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.search.model.FileIto;
import de.mpg.mpdl.r2d2.service.storage.SwiftObjectStoreRepository;
import de.mpg.mpdl.r2d2.util.R2D2IntegrationTest;
import de.mpg.mpdl.r2d2.util.testdata.TestDataFactory;
import de.mpg.mpdl.r2d2.util.testdata.TestDataIndexer;
import de.mpg.mpdl.r2d2.util.testdata.TestDataManager;

@R2D2IntegrationTest
class FileUploadServiceIT {

  @Autowired
  FileUploadService fileUploadService;

  @Autowired
  TestDataManager testDataManager;

  @Autowired
  TestDataIndexer testDataIndexer;

  @Autowired
  SwiftObjectStoreRepository swiftObjectStoreRepository;

  @Test
  void tesUploadSingleFile() throws ValidationException, R2d2TechnicalException, AuthorizationException, OptimisticLockingException,
      NotFoundException, InvalidStateException {
    //Given
    String fileName = "fileName";
    File file = TestDataFactory.aFile().filename(fileName).build();
    InputStream inputStream = Mockito.mock(InputStream.class);
    UserAccount user = TestDataFactory.anUser().build();
    R2D2Principal r2D2Principal = TestDataFactory.aR2D2Principal().userAccount(user).build();

    this.testDataManager.persist(user);

    String checksum = "eTag";
    Mockito.when(this.swiftObjectStoreRepository.uploadFile(file, inputStream)).thenReturn(checksum);

    //When
    File returnedFile = this.fileUploadService.uploadSingleFile(file, inputStream, r2D2Principal);

    //Then
    List<File> filesFromDB = this.testDataManager.findAll(File.class);
    List<FileIto> filesFromIndex = this.testDataIndexer.searchAll(FileIto.class);

    assertThat(returnedFile).isNotNull().extracting(File::getFilename, File::getChecksum).containsExactly(fileName, checksum);
    assertThat(filesFromDB).hasSize(1).first().usingRecursiveComparison().isEqualTo(returnedFile);
    //    assertThat(filesFromIndex).hasSize(1).first().usingRecursiveComparison().ignoringFields("internal").isEqualTo(returnedFile);
    Mockito.verify(swiftObjectStoreRepository).uploadFile(file, inputStream);
  }

}
