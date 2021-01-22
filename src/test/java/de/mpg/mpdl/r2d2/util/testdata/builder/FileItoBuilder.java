package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.FileUploadStatus;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.search.model.FileIto;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class FileItoBuilder {
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

  private FileItoBuilder() {}

  public static FileItoBuilder aFileIto() {
    return new FileItoBuilder();
  }

  public FileItoBuilder creationDate(OffsetDateTime creationDate) {
    this.creationDate = creationDate;
    return this;
  }

  public FileItoBuilder modificationDate(OffsetDateTime modificationDate) {
    this.modificationDate = modificationDate;
    return this;
  }

  public FileItoBuilder creator(UserAccount creator) {
    this.creator = creator;
    return this;
  }

  public FileItoBuilder modifier(UserAccount modifier) {
    this.modifier = modifier;
    return this;
  }

  public FileItoBuilder id(UUID id) {
    this.id = id;
    return this;
  }

  public FileItoBuilder state(File.UploadState state) {
    this.state = state;
    return this;
  }

  public FileItoBuilder stateInfo(FileUploadStatus stateInfo) {
    this.stateInfo = stateInfo;
    return this;
  }

  public FileItoBuilder versions(Set<DatasetVersion> versions) {
    this.versions = versions;
    return this;
  }

  public FileItoBuilder filename(String filename) {
    this.filename = filename;
    return this;
  }

  public FileItoBuilder storageLocation(String storageLocation) {
    this.storageLocation = storageLocation;
    return this;
  }

  public FileItoBuilder checksum(String checksum) {
    this.checksum = checksum;
    return this;
  }

  public FileItoBuilder format(String format) {
    this.format = format;
    return this;
  }

  public FileItoBuilder size(long size) {
    this.size = size;
    return this;
  }

  public FileIto build() {
    FileIto fileIto = new FileIto();
    fileIto.setCreationDate(creationDate);
    fileIto.setModificationDate(modificationDate);
    fileIto.setCreator(creator);
    fileIto.setModifier(modifier);
    fileIto.setId(id);
    fileIto.setState(state);
    fileIto.setStateInfo(stateInfo);
    fileIto.setVersions(versions);
    fileIto.setFilename(filename);
    fileIto.setStorageLocation(storageLocation);
    fileIto.setChecksum(checksum);
    fileIto.setFormat(format);
    fileIto.setSize(size);
    return fileIto;
  }
}
