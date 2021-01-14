package de.mpg.mpdl.r2d2.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUploadStatus {


  private String currentChecksum;

  private int totalNumberOfChunks;

  @Builder.Default
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
