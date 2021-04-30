package de.mpg.mpdl.r2d2.service.storage;

import java.io.InputStream;

import org.jclouds.blobstore.domain.Blob;

import de.mpg.mpdl.r2d2.exceptions.NotFoundException;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.FileChunk;

public interface ObjectStoreRepository {

  public String uploadChunk(File file, FileChunk chunk, InputStream inputStream);

  public String uploadFile(File file, InputStream inputStream);

  public InputStream downloadFile(String container, String name);

  public boolean deleteContainer(String container) throws NotFoundException;

  public Blob getFile(String container, String name);

  public String getPublicURI(String container);

  public Long getFileSize(String container);

  public boolean deleteFile(String container, String name);

  public boolean isFileExist(String container, String name);

  public boolean isContainerExist(String container);

  public boolean createContainer(String name);

  public String createManifest(File file);

}
