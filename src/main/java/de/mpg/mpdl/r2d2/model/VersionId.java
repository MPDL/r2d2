package de.mpg.mpdl.r2d2.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
public class VersionId implements Serializable {

  private UUID dataset;

  private Integer versionNumber;

  public VersionId() {

  }

  public VersionId(UUID id) {
    this.dataset = id;
    this.versionNumber = null;

  }

  public VersionId(UUID id, Integer versionNumber) {
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

  public Integer getVersionNumber() {
    return versionNumber;
  }

  public void setVersionNumber(int versionNumber) {
    this.versionNumber = versionNumber;
  }

  @Transient
  public String getVersionId() {
    return toString();
  }


  public String toString() {
    return this.dataset.toString() + "/" + versionNumber;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof VersionId)) {
      return false;
    }

    VersionId otherId = (VersionId) o;
    return Objects.equals(this.dataset, otherId.dataset) && Objects.equals(this.versionNumber, otherId.versionNumber);
  }

}
