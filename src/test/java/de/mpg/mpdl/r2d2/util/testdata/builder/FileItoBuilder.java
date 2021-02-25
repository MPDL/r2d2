package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.VersionId;
import de.mpg.mpdl.r2d2.search.model.FileIto;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class FileItoBuilder {
  private UUID id;
  private OffsetDateTime creationDate;
  private OffsetDateTime modificationDate;
  private UUID creator;
  private UUID modifier;
  private String filename;
  private String storageLocation;
  private String checksum;
  private String format;
  private long size;
  private File.UploadState state;
  private List<VersionId> datasets = new ArrayList<VersionId>();
  private Dataset internal;

  private FileItoBuilder() {}

  public static FileItoBuilder aFileIto() {
    return new FileItoBuilder();
  }

  public FileItoBuilder id(UUID id) {
    this.id = id;
    return this;
  }

  public FileItoBuilder creationDate(OffsetDateTime creationDate) {
    this.creationDate = creationDate;
    return this;
  }

  public FileItoBuilder modificationDate(OffsetDateTime modificationDate) {
    this.modificationDate = modificationDate;
    return this;
  }

  public FileItoBuilder creator(UUID creator) {
    this.creator = creator;
    return this;
  }

  public FileItoBuilder modifier(UUID modifier) {
    this.modifier = modifier;
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

  public FileItoBuilder state(File.UploadState state) {
    this.state = state;
    return this;
  }

  public FileItoBuilder datasets(List<VersionId> datasets) {
    this.datasets = datasets;
    return this;
  }

  public FileItoBuilder internal(Dataset internal) {
    this.internal = internal;
    return this;
  }

  public FileIto build() {
    FileIto fileIto = new FileIto();
    fileIto.setId(id);
    fileIto.setCreationDate(creationDate);
    fileIto.setModificationDate(modificationDate);
    fileIto.setCreator(creator);
    fileIto.setModifier(modifier);
    fileIto.setFilename(filename);
    fileIto.setStorageLocation(storageLocation);
    fileIto.setChecksum(checksum);
    fileIto.setFormat(format);
    fileIto.setSize(size);
    fileIto.setState(state);
    fileIto.setDatasets(datasets);
    fileIto.setInternal(internal);
    return fileIto;
  }
}
