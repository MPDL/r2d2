package de.mpg.mpdl.r2d2.search.model;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.DatasetVersionMetadata;
import de.mpg.mpdl.r2d2.model.Dataset.State;
import de.mpg.mpdl.r2d2.rest.controller.dto.DatasetDto;
import de.mpg.mpdl.r2d2.rest.controller.dto.DatasetVersionDto;
import de.mpg.mpdl.r2d2.rest.controller.dto.FileDto;

public class DatasetVersionIto {


  private UUID id;


  private int versionNumber = 1;


  private Dataset.State state = State.PRIVATE;

  private OffsetDateTime creationDate;


  private OffsetDateTime modificationDate;


  private UUID creator;


  private UUID modifier;

  private DatasetDto dataset = new DatasetDto();


  private DatasetVersionMetadata metadata = new DatasetVersionMetadata();


  private DatasetVersion internal;



  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public int getVersionNumber() {
    return versionNumber;
  }

  public void setVersionNumber(int versionNumber) {
    this.versionNumber = versionNumber;
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

  public Dataset.State getState() {
    return state;
  }

  public void setState(Dataset.State state) {
    this.state = state;
  }

  public DatasetVersionMetadata getMetadata() {
    return metadata;
  }

  public void setMetadata(DatasetVersionMetadata metadata) {
    this.metadata = metadata;
  }

  public DatasetDto getDataset() {
    return dataset;
  }

  public void setDataset(DatasetDto dataset) {
    this.dataset = dataset;
  }



  public DatasetVersion getInternal() {
    return internal;
  }

  public void setInternal(DatasetVersion internal) {
    this.internal = internal;
  }



}
