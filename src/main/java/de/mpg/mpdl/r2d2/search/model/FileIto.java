package de.mpg.mpdl.r2d2.search.model;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.VersionId;
import de.mpg.mpdl.r2d2.rest.controller.dto.FileDto;

public class FileIto {

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

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public OffsetDateTime getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(OffsetDateTime creationDate) {
    this.creationDate = creationDate;
  }

  public OffsetDateTime getModificationDate() {
    return modificationDate;
  }

  public void setModificationDate(OffsetDateTime modificationDate) {
    this.modificationDate = modificationDate;
  }

  public UUID getCreator() {
    return creator;
  }

  public void setCreator(UUID creator) {
    this.creator = creator;
  }

  public UUID getModifier() {
    return modifier;
  }

  public void setModifier(UUID modifier) {
    this.modifier = modifier;
  }

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

  public List<VersionId> getDatasets() {
    return datasets;
  }

  public void setDatasets(List<VersionId> datasets) {
    this.datasets = datasets;
  }



  public Dataset getInternal() {
    return internal;
  }

  public void setInternal(Dataset internal) {
    this.internal = internal;
  }

}
