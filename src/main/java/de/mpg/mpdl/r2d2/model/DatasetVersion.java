package de.mpg.mpdl.r2d2.model;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import javax.validation.Valid;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.mpg.mpdl.r2d2.model.Dataset.State;

@Entity
@IdClass(VersionId.class)
public class DatasetVersion extends BaseDateDb {


  @Id
  @Column(nullable = false)
  public int versionNumber = 1;

  // newest Version = latestVersion in Dataset

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Dataset.State state = State.PRIVATE;

  // modificationDate is in BaseDateDb
  // creationDate is in BaseDateDb

  @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private OffsetDateTime publicationDate;

  private String publicationComment;

  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb")
  @Valid
  private DatasetVersionMetadata metadata = new DatasetVersionMetadata();

  @Id
  @MapsId("id")
  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "id", nullable = false)
  private Dataset dataset = new Dataset();

  public int getVersionNumber() {
    return versionNumber;
  }

  public void setVersionNumber(int versionNumber) {
    this.versionNumber = versionNumber;
  }

  public Dataset.State getState() {
    return state;
  }

  public void setState(Dataset.State state) {
    this.state = state;
  }

  public Dataset getDataset() {
    return dataset;
  }

  public void setDataset(Dataset dataset) {
    this.dataset = dataset;
  }

  public OffsetDateTime getPublicationDate() {
    return publicationDate;
  }

  public void setPublicationDate(OffsetDateTime publicationDate) {
    this.publicationDate = publicationDate;
  }

  public String getPublicationComment() {
    return publicationComment;
  }

  public void setPublicationComment(String publicationComment) {
    this.publicationComment = publicationComment;
  }

  public DatasetVersionMetadata getMetadata() {
    return metadata;
  }

  public void setMetadata(DatasetVersionMetadata metadata) {
    this.metadata = metadata;
  }

  @JsonIgnore
  public VersionId getVersionId() {
    return new VersionId(this.getId(), this.getVersionNumber());
  }

  public UUID getId() {
    return dataset.getId();
  }



}
