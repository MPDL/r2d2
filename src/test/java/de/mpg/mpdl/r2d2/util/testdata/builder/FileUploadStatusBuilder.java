package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.model.FileChunk;
import de.mpg.mpdl.r2d2.model.FileUploadStatus;

import java.util.ArrayList;
import java.util.List;

public final class FileUploadStatusBuilder {
  private String currentChecksum;
  private List<FileChunk> chunks = new ArrayList<>();

  private FileUploadStatusBuilder() {}

  public static FileUploadStatusBuilder aFileUploadStatus() {
    return new FileUploadStatusBuilder();
  }

  public FileUploadStatusBuilder currentChecksum(String currentChecksum) {
    this.currentChecksum = currentChecksum;
    return this;
  }

  public FileUploadStatusBuilder chunks(List<FileChunk> chunks) {
    this.chunks = chunks;
    return this;
  }

  public FileUploadStatus build() {
    FileUploadStatus fileUploadStatus = new FileUploadStatus();
    fileUploadStatus.setCurrentChecksum(currentChecksum);
    fileUploadStatus.setChunks(chunks);
    return fileUploadStatus;
  }
}
