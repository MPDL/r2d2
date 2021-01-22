package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.FileUploadStatus;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class FileBuilder {
  private OffsetDateTime creationDate;
  private OffsetDateTime modificationDate;
  private UserAccount creator;
  private UserAccount modifier;
  //@GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;
  private File.UploadState state = File.UploadState.INITIATED;
  private FileUploadStatus stateInfo = new FileUploadStatus();
  private Set<DatasetVersion> versions = new HashSet();
  private String filename;
  private String storageLocation;
  private String checksum;
  private String format;
  private long size;

  private FileBuilder() {}

  public static FileBuilder aFile() {
    return new FileBuilder();
  }

  public FileBuilder creationDate(OffsetDateTime creationDate) {
    this.creationDate = creationDate;
    return this;
  }

  public FileBuilder modificationDate(OffsetDateTime modificationDate) {
    this.modificationDate = modificationDate;
    return this;
  }

  public FileBuilder creator(UserAccount creator) {
    this.creator = creator;
    return this;
  }

  public FileBuilder modifier(UserAccount modifier) {
    this.modifier = modifier;
    return this;
  }

  public FileBuilder id(UUID id) {
    this.id = id;
    return this;
  }

  public FileBuilder state(File.UploadState state) {
    this.state = state;
    return this;
  }

  public FileBuilder stateInfo(FileUploadStatus stateInfo) {
    this.stateInfo = stateInfo;
    return this;
  }

  public FileBuilder versions(Set<DatasetVersion> versions) {
    this.versions = versions;
    return this;
  }

  public FileBuilder filename(String filename) {
    this.filename = filename;
    return this;
  }

  public FileBuilder storageLocation(String storageLocation) {
    this.storageLocation = storageLocation;
    return this;
  }

  public FileBuilder checksum(String checksum) {
    this.checksum = checksum;
    return this;
  }

  public FileBuilder format(String format) {
    this.format = format;
    return this;
  }

  public FileBuilder size(long size) {
    this.size = size;
    return this;
  }

  public File build() {
    File file = new File();
    file.setCreationDate(creationDate);
    file.setModificationDate(modificationDate);
    file.setCreator(creator);
    file.setModifier(modifier);
    file.setId(id);
    file.setState(state);
    file.setStateInfo(stateInfo);
    file.setVersions(versions);
    file.setFilename(filename);
    file.setStorageLocation(storageLocation);
    file.setChecksum(checksum);
    file.setFormat(format);
    file.setSize(size);
    return file;
  }
}
