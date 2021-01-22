package de.mpg.mpdl.r2d2.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.Type;

// Item = File
@Entity
public class File extends BaseDb {

  @Enumerated(EnumType.STRING)
  private UploadState state = UploadState.INITIATED;

  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb")
  private FileUploadStatus stateInfo = new FileUploadStatus();

  @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  private Set<DatasetVersion> versions = new HashSet();

  private String filename;

  private String storageLocation;

  private String checksum;

  private String format;

  private long size;

  public String getFilename() {
    return filename;
  }

  //Date uploaded = creationDate in BaseDateDb!?

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

  public UploadState getState() {
    return state;
  }

  public void setState(UploadState state) {
    this.state = state;
  }

  public FileUploadStatus getStateInfo() {
    return stateInfo;
  }

  public void setStateInfo(FileUploadStatus stateInfo) {
    this.stateInfo = stateInfo;
  }

  public Set<DatasetVersion> getVersions() {
    return versions;
  }

  public void setVersions(Set<DatasetVersion> versions) {
    this.versions = versions;
  }

  public enum UploadState {
    INITIATED,
    ONGOING,
    COMPLETE,
    ATTACHED,
    PUBLIC
  }

}
