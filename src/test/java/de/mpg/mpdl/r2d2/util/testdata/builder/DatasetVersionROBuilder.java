package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.model.DatasetVersionRO;

import java.util.UUID;

public final class DatasetVersionROBuilder {
  private UUID id;
  private int versionNumber;

  private DatasetVersionROBuilder() {}

  public static DatasetVersionROBuilder aDatasetVersionRO() {
    return new DatasetVersionROBuilder();
  }

  public DatasetVersionROBuilder id(UUID id) {
    this.id = id;
    return this;
  }

  public DatasetVersionROBuilder versionNumber(int versionNumber) {
    this.versionNumber = versionNumber;
    return this;
  }

  public DatasetVersionRO build() {
    DatasetVersionRO datasetVersionRO = new DatasetVersionRO();
    datasetVersionRO.setId(id);
    datasetVersionRO.setVersionNumber(versionNumber);
    return datasetVersionRO;
  }
}
