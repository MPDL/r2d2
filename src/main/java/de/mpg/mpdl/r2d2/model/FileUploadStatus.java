package de.mpg.mpdl.r2d2.model;

import java.util.ArrayList;
import java.util.List;


public class FileUploadStatus {


  private String currentChecksum;

  private int totalNumberOfChunks;

  private List<FileChunk> chunks = new ArrayList<>();

  public String getCurrentChecksum() {
    return currentChecksum;
  }

  public void setCurrentChecksum(String currentChecksum) {
    this.currentChecksum = currentChecksum;
  }

  public int getExpectedNumberOfChunks() {
    return totalNumberOfChunks;
  }

  public void setExpectedNumberOfChunks(int expectedNumberOfChunks) {
    this.totalNumberOfChunks = expectedNumberOfChunks;
  }

  public List<FileChunk> getChunks() {
    return chunks;
  }

  public void setChunks(List<FileChunk> chunks) {
    this.chunks = chunks;
  }


}
