package de.mpg.mpdl.r2d2.service.storage;

import java.io.IOException;
import java.util.List;

import org.apache.commons.fileupload.FileItemStream;
import org.springframework.core.io.Resource;

import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.File;

public interface StorageService {

  public File store(String containerId, FileItemStream item) throws R2d2TechnicalException, IOException;

  public Resource get(String containerId, String fileId) throws R2d2TechnicalException;

  public List<File> list(String containerId) throws R2d2TechnicalException;

}
