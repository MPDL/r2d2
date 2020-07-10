package de.mpg.mpdl.r2d2.model;

import java.io.Serializable;
import java.util.UUID;

public class VersionId implements Serializable {

  private UUID id;

  private int versionNumber;

  public VersionId() {

  }

  public VersionId(UUID id, int versionNumber) {
    super();
    this.id = id;
    this.versionNumber = versionNumber;
  }

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


  public String toString() {
    return this.id.toString() + "/" + versionNumber;
  }

}
