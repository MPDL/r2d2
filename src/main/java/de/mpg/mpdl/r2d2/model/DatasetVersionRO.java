package de.mpg.mpdl.r2d2.model;

import java.util.UUID;

public class DatasetVersionRO {

  private UUID id;

  private int versionNumber;


  public DatasetVersionRO() {

  }

  public DatasetVersionRO(DatasetVersion dv) {
    this.id = dv.getId();
    this.versionNumber = dv.getVersionNumber();
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



}
