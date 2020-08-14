package de.mpg.mpdl.r2d2.service.util;

import java.io.InputStream;

import org.springframework.core.io.InputStreamResource;

import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.service.storage.SwiftObjectStoreRepository;

public class FileDownloadWrapper {

  private File file;

  private SwiftObjectStoreRepository storeRepository;


  public FileDownloadWrapper(File file, SwiftObjectStoreRepository repo) {
    this.file = file;
    this.storeRepository = repo;

  }

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public SwiftObjectStoreRepository getStoreRepository() {
    return storeRepository;
  }

  public void setStoreRepository(SwiftObjectStoreRepository storeRepository) {
    this.storeRepository = storeRepository;
  }

  public InputStream readFile() {
    return storeRepository.downloadFile(file.getId().toString(), "content");
  }

}
