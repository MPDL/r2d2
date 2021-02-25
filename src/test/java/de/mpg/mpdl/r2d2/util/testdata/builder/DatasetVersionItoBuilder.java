package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.DatasetVersionMetadata;
import de.mpg.mpdl.r2d2.rest.controller.dto.DatasetDto;
import de.mpg.mpdl.r2d2.search.model.DatasetVersionIto;

import java.time.OffsetDateTime;
import java.util.UUID;

public final class DatasetVersionItoBuilder {
  private UUID id;
  private int versionNumber = 1;
  private Dataset.State state = Dataset.State.PRIVATE;
  private OffsetDateTime creationDate;
  private OffsetDateTime modificationDate;
  private UUID creator;
  private UUID modifier;
  private DatasetDto dataset = new DatasetDto();
  private DatasetVersionMetadata metadata = new DatasetVersionMetadata();
  private DatasetVersion internal;

  private DatasetVersionItoBuilder() {}

  public static DatasetVersionItoBuilder aDatasetVersionIto() {
    return new DatasetVersionItoBuilder();
  }

  public DatasetVersionItoBuilder id(UUID id) {
    this.id = id;
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

  public DatasetVersionItoBuilder creationDate(OffsetDateTime creationDate) {
    this.creationDate = creationDate;
    return this;
  }

  public DatasetVersionItoBuilder modificationDate(OffsetDateTime modificationDate) {
    this.modificationDate = modificationDate;
    return this;
  }

  public DatasetVersionItoBuilder creator(UUID creator) {
    this.creator = creator;
    return this;
  }

  public DatasetVersionItoBuilder modifier(UUID modifier) {
    this.modifier = modifier;
    return this;
  }

  public DatasetVersionItoBuilder dataset(DatasetDto dataset) {
    this.dataset = dataset;
    return this;
  }

  public DatasetVersionItoBuilder metadata(DatasetVersionMetadata metadata) {
    this.metadata = metadata;
    return this;
  }

  public DatasetVersionItoBuilder internal(DatasetVersion internal) {
    this.internal = internal;
    return this;
  }

  public DatasetVersionIto build() {
    DatasetVersionIto datasetVersionIto = new DatasetVersionIto();
    datasetVersionIto.setId(id);
    datasetVersionIto.setVersionNumber(versionNumber);
    datasetVersionIto.setState(state);
    datasetVersionIto.setCreationDate(creationDate);
    datasetVersionIto.setModificationDate(modificationDate);
    datasetVersionIto.setCreator(creator);
    datasetVersionIto.setModifier(modifier);
    datasetVersionIto.setDataset(dataset);
    datasetVersionIto.setMetadata(metadata);
    datasetVersionIto.setInternal(internal);
    return datasetVersionIto;
  }
}
