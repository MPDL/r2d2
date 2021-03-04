package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.search.model.DatasetIto;

import java.time.OffsetDateTime;
import java.util.UUID;

public final class DatasetItoBuilder {
  private Dataset.State state = Dataset.State.PRIVATE;
  private OffsetDateTime creationDate;
  private OffsetDateTime modificationDate;
  private UUID creator;
  private UUID modifier;
  private Integer latestVersion = 1;
  private Integer latestPublicVersion = null;

  private DatasetItoBuilder() {}

  public static DatasetItoBuilder aDatasetIto() {
    return new DatasetItoBuilder();
  }

  public DatasetItoBuilder state(Dataset.State state) {
    this.state = state;
    return this;
  }

  public DatasetItoBuilder creationDate(OffsetDateTime creationDate) {
    this.creationDate = creationDate;
    return this;
  }

  public DatasetItoBuilder modificationDate(OffsetDateTime modificationDate) {
    this.modificationDate = modificationDate;
    return this;
  }

  public DatasetItoBuilder creator(UUID creator) {
    this.creator = creator;
    return this;
  }

  public DatasetItoBuilder modifier(UUID modifier) {
    this.modifier = modifier;
    return this;
  }

  public DatasetItoBuilder latestVersion(Integer latestVersion) {
    this.latestVersion = latestVersion;
    return this;
  }

  public DatasetItoBuilder latestPublicVersion(Integer latestPublicVersion) {
    this.latestPublicVersion = latestPublicVersion;
    return this;
  }

  public DatasetIto build() {
    DatasetIto datasetIto = new DatasetIto();
    datasetIto.setState(state);
    datasetIto.setCreationDate(creationDate);
    datasetIto.setModificationDate(modificationDate);
    datasetIto.setCreator(creator);
    datasetIto.setModifier(modifier);
    datasetIto.setLatestVersion(latestVersion);
    datasetIto.setLatestPublicVersion(latestPublicVersion);
    return datasetIto;
  }
}
