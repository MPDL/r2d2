package de.mpg.mpdl.r2d2.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
//@NoArgsConstructor is already defined
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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
