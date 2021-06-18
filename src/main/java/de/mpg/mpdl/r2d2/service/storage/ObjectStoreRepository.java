package de.mpg.mpdl.r2d2.service.storage;

import java.io.InputStream;

import de.mpg.mpdl.r2d2.exceptions.NotFoundException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.FileChunk;

public interface ObjectStoreRepository {

  public String uploadChunk(File file, FileChunk chunk, InputStream inputStream);

  public String uploadFile(File file, InputStream inputStream) throws R2d2TechnicalException;

  public InputStream downloadFile(String container, String name) throws R2d2TechnicalException;

  public boolean deleteContainer(String container) throws NotFoundException;

  public String getPublicURI(String container) throws R2d2TechnicalException;

  public Long getFileSize(String container) throws R2d2TechnicalException;

  public boolean deleteFile(String container, String name) throws R2d2TechnicalException;

  public boolean isFileExist(String container, String name);

  public boolean isContainerExist(String container);

  public String createManifest(File file);

}
