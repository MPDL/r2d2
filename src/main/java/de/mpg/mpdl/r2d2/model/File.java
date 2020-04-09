package de.mpg.mpdl.r2d2.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class File extends BaseDb {

  public enum UploadState {
    INITIATED,
    ONGOING,
    COMPLETE
  }

  @Enumerated(EnumType.STRING)
  private File.UploadState state = UploadState.INITIATED;

  private String filename;

  private String storageLocation;

  private String checksum;

  private String format;

  private long size;

  private int totalParts;

  private int completedParts;

  // Date uploaded = BaseDb.creationDate

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getStorageLocation() {
    return storageLocation;
  }

  public void setStorageLocation(String storageLocation) {
    this.storageLocation = storageLocation;
  }

  public String getChecksum() {
    return checksum;
  }

  public void setChecksum(String checksum) {
    this.checksum = checksum;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public File.UploadState getState() {
    return state;
  }

  public void setState(File.UploadState state) {
    this.state = state;
  }

  public int getTotalParts() {
    return totalParts;
  }

  public void setTotalParts(int totalParts) {
    this.totalParts = totalParts;
  }

  public int getCompletedParts() {
    return completedParts;
  }

  public void setCompletedParts(int completedParts) {
    this.completedParts = completedParts;
  }

}
