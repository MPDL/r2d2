package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.model.FileChunk;

public final class FileChunkBuilder {
  private int number;
  private String clientEtag;
  private String serverEtag;
  private long size;
  private FileChunk.Progress progress = FileChunk.Progress.IN_PROGRESS;

  private FileChunkBuilder() {}

  public static FileChunkBuilder aFileChunk() {
    return new FileChunkBuilder();
  }

  public FileChunkBuilder number(int number) {
    this.number = number;
    return this;
  }

  public FileChunkBuilder clientEtag(String clientEtag) {
    this.clientEtag = clientEtag;
    return this;
  }

  public FileChunkBuilder serverEtag(String serverEtag) {
    this.serverEtag = serverEtag;
    return this;
  }

  public FileChunkBuilder size(long size) {
    this.size = size;
    return this;
  }

  public FileChunkBuilder progress(FileChunk.Progress progress) {
    this.progress = progress;
    return this;
  }

  public FileChunk build() {
    FileChunk fileChunk = new FileChunk();
    fileChunk.setNumber(number);
    fileChunk.setClientEtag(clientEtag);
    fileChunk.setServerEtag(serverEtag);
    fileChunk.setSize(size);
    fileChunk.setProgress(progress);
    return fileChunk;
  }
}
