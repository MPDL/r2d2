package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.model.VersionId;

public final class VersionIdBuilder {
  private Integer versionNumber;

  private VersionIdBuilder() {}

  public static VersionIdBuilder aVersionId() {
    return new VersionIdBuilder();
  }

  public VersionIdBuilder versionNumber(Integer versionNumber) {
    this.versionNumber = versionNumber;
    return this;
  }

  public VersionId build() {
    VersionId versionId = new VersionId();
    versionId.setVersionNumber(versionNumber);
    return versionId;
  }
}
