package de.mpg.mpdl.r2d2.rest.controller.dto;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.springframework.web.bind.annotation.Mapping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.Dataset.State;
import de.mpg.mpdl.r2d2.model.aa.UserAccountRO;
import de.mpg.mpdl.r2d2.model.DatasetVersionMetadata;
import de.mpg.mpdl.r2d2.model.File;


public class DatasetVersionDto {

  private UUID id;

  private int versionNumber = 1;

  private Dataset.State state = State.PRIVATE;

  private OffsetDateTime creationDate;

  private OffsetDateTime modificationDate;

  private UUID creator;

  private UUID modifier;

  private DatasetDto dataset = new DatasetDto();

  private DatasetVersionMetadata metadata = new DatasetVersionMetadata();

  private List<FileDto> files = new ArrayList<>();



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

  public List<FileDto> getFiles() {
    return files;
  }

  public void setFiles(List<FileDto> files) {
    this.files = files;
  }

  public DatasetDto getDataset() {
    return dataset;
  }

  public void setDataset(DatasetDto dataset) {
    this.dataset = dataset;
  }


}
