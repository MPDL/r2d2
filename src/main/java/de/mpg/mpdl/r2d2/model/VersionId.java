package de.mpg.mpdl.r2d2.model;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Embeddable;

@Embeddable
public class VersionId implements Serializable {

  private UUID dataset;

  private int versionNumber;

  public VersionId() {

  }

  public VersionId(UUID id, int versionNumber) {
    super();
    this.dataset = id;
    this.versionNumber = versionNumber;
  }

  public UUID getId() {
    return dataset;
  }

  public void setId(UUID id) {
    this.dataset = id;
  }

  public int getVersionNumber() {
    return versionNumber;
  }

  public void setVersionNumber(int versionNumber) {
    this.versionNumber = versionNumber;
  }


  public String toString() {
    return this.dataset.toString() + "/" + versionNumber;
  }

}
