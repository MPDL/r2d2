package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersionMetadata;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.search.model.DatasetVersionIto;

import java.time.OffsetDateTime;

public final class DatasetVersionItoBuilder {
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

  private DatasetVersionItoBuilder() {}

  public static DatasetVersionItoBuilder aDatasetVersionIto() {
    return new DatasetVersionItoBuilder();
  }

  public DatasetVersionItoBuilder creationDate(OffsetDateTime creationDate) {
    this.creationDate = creationDate;
    return this;
  }

  public DatasetVersionItoBuilder modificationDate(OffsetDateTime modificationDate) {
    this.modificationDate = modificationDate;
    return this;
  }

  public DatasetVersionItoBuilder creator(UserAccount creator) {
    this.creator = creator;
    return this;
  }

  public DatasetVersionItoBuilder modifier(UserAccount modifier) {
    this.modifier = modifier;
    return this;
  }

  public DatasetVersionItoBuilder versionNumber(int versionNumber) {
    this.versionNumber = versionNumber;
    return this;
  }

  public DatasetVersionItoBuilder state(Dataset.State state) {
    this.state = state;
    return this;
  }

  public DatasetVersionItoBuilder publicationDate(OffsetDateTime publicationDate) {
    this.publicationDate = publicationDate;
    return this;
  }

  public DatasetVersionItoBuilder publicationComment(String publicationComment) {
    this.publicationComment = publicationComment;
    return this;
  }

  public DatasetVersionItoBuilder metadata(DatasetVersionMetadata metadata) {
    this.metadata = metadata;
    return this;
  }

  public DatasetVersionItoBuilder dataset(Dataset dataset) {
    this.dataset = dataset;
    return this;
  }

  public DatasetVersionIto build() {
    DatasetVersionIto datasetVersionIto = new DatasetVersionIto();
    datasetVersionIto.setCreationDate(creationDate);
    datasetVersionIto.setModificationDate(modificationDate);
    datasetVersionIto.setCreator(creator);
    datasetVersionIto.setModifier(modifier);
    datasetVersionIto.setVersionNumber(versionNumber);
    datasetVersionIto.setState(state);
    datasetVersionIto.setPublicationDate(publicationDate);
    datasetVersionIto.setPublicationComment(publicationComment);
    datasetVersionIto.setMetadata(metadata);
    datasetVersionIto.setDataset(dataset);
    return datasetVersionIto;
  }
}
