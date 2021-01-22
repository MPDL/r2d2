package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.DatasetVersionMetadata;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;

import java.time.OffsetDateTime;

public final class DatasetVersionBuilder {
  public int versionNumber = 1;
  private OffsetDateTime creationDate;
  private OffsetDateTime modificationDate;
  private UserAccount creator;
  private UserAccount modifier;
  private Dataset.State state = Dataset.State.PRIVATE;
  private OffsetDateTime publicationDate;
  private String publicationComment;
  private DatasetVersionMetadata metadata = new DatasetVersionMetadata();
  private Dataset dataset = new Dataset();

  private DatasetVersionBuilder() {}

  public static DatasetVersionBuilder aDatasetVersion() {
    return new DatasetVersionBuilder();
  }

  public DatasetVersionBuilder creationDate(OffsetDateTime creationDate) {
    this.creationDate = creationDate;
    return this;
  }

  public DatasetVersionBuilder modificationDate(OffsetDateTime modificationDate) {
    this.modificationDate = modificationDate;
    return this;
  }

  public DatasetVersionBuilder creator(UserAccount creator) {
    this.creator = creator;
    return this;
  }

  public DatasetVersionBuilder modifier(UserAccount modifier) {
    this.modifier = modifier;
    return this;
  }

  public DatasetVersionBuilder versionNumber(int versionNumber) {
    this.versionNumber = versionNumber;
    return this;
  }

  public DatasetVersionBuilder state(Dataset.State state) {
    this.state = state;
    return this;
  }

  public DatasetVersionBuilder publicationDate(OffsetDateTime publicationDate) {
    this.publicationDate = publicationDate;
    return this;
  }

  public DatasetVersionBuilder publicationComment(String publicationComment) {
    this.publicationComment = publicationComment;
    return this;
  }

  public DatasetVersionBuilder metadata(DatasetVersionMetadata metadata) {
    this.metadata = metadata;
    return this;
  }

  public DatasetVersionBuilder dataset(Dataset dataset) {
    this.dataset = dataset;
    return this;
  }

  public DatasetVersion build() {
    DatasetVersion datasetVersion = new DatasetVersion();
    datasetVersion.setCreationDate(creationDate);
    datasetVersion.setModificationDate(modificationDate);
    datasetVersion.setCreator(creator);
    datasetVersion.setModifier(modifier);
    datasetVersion.setVersionNumber(versionNumber);
    datasetVersion.setState(state);
    datasetVersion.setPublicationDate(publicationDate);
    datasetVersion.setPublicationComment(publicationComment);
    datasetVersion.setMetadata(metadata);
    datasetVersion.setDataset(dataset);
    return datasetVersion;
  }
}
