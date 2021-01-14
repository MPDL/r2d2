package de.mpg.mpdl.r2d2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.mpg.mpdl.r2d2.model.Dataset.State;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@IdClass(VersionId.class)
public class DatasetVersion extends BaseDateDb {

  @Builder.Default
  @Id
  @Column(nullable = false)
  public int versionNumber = 1;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Dataset.State state = State.PRIVATE;


  @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private OffsetDateTime publicationDate;

  @Builder.Default
  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb")
  private DatasetVersionMetadata metadata = new DatasetVersionMetadata();

  @Builder.Default
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
