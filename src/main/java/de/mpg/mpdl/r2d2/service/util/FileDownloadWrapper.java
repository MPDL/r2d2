package de.mpg.mpdl.r2d2.service.util;

import java.io.InputStream;

import org.springframework.core.io.InputStreamResource;

import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.service.storage.ObjectStoreRepository;
import de.mpg.mpdl.r2d2.service.storage.SwiftObjectStoreRepository;

public class FileDownloadWrapper {

  private File file;

  private ObjectStoreRepository storeRepository;


  public FileDownloadWrapper(File file, ObjectStoreRepository objectStoreRepository) {
    this.file = file;
    this.storeRepository = objectStoreRepository;

  }

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public ObjectStoreRepository getStoreRepository() {
    return storeRepository;
  }

  public void setStoreRepository(ObjectStoreRepository storeRepository) {
    this.storeRepository = storeRepository;
  }

  public InputStream readFile() {
    return storeRepository.downloadFile(file.getId().toString(), "content");
  }

}
