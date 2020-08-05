package de.mpg.mpdl.r2d2.rest.controller.dto;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.aa.UserAccountRO;


public class DatasetDto {


  private Dataset.State state = Dataset.State.PRIVATE;

  private OffsetDateTime creationDate;

  private OffsetDateTime modificationDate;

  private UUID creator;

  private UUID modifier;


  //@Type(type = "jsonb")
  //@Column(columnDefinition = "jsonb")
  private Integer latestVersion = 1;

  //@Type(type = "jsonb")
  //@Column(columnDefinition = "jsonb")
  private Integer latestPublicVersion = null;


  public Dataset.State getState() {
    return state;
  }

  public void setState(Dataset.State state) {
    this.state = state;
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

  public Integer getLatestVersion() {
    return latestVersion;
  }

  public void setLatestVersion(Integer latestVersion) {
    this.latestVersion = latestVersion;
  }

  public Integer getLatestPublicVersion() {
    return latestPublicVersion;
  }

  public void setLatestPublicVersion(Integer latestPublicVersion) {
    this.latestPublicVersion = latestPublicVersion;
  }


}
