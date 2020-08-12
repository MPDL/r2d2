package de.mpg.mpdl.r2d2.model;

public class FileChunk {

  private int number;

  private String clientEtag;

  private String serverEtag;

  private long size;

  public enum Progress {
    IN_PROGRESS,
    COMPLETE
  }

  private Progress progress = Progress.IN_PROGRESS;

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }


  public String getClientEtag() {
    return clientEtag;
  }

  public void setClientEtag(String clientEtag) {
    this.clientEtag = clientEtag;
  }

  public String getServerEtag() {
    return serverEtag;
  }

  public void setServerEtag(String serverEtag) {
    this.serverEtag = serverEtag;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public Progress getProgress() {
    return progress;
  }

  public void setProgress(Progress progress) {
    this.progress = progress;
  }

}
