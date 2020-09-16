package de.mpg.mpdl.r2d2.rest.controller.dto;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SetFilesDto {

  private OffsetDateTime modificationDate;

  private List<UUID> files = new ArrayList<UUID>();



  public List<UUID> getFiles() {
    return files;
  }

  public void setFiles(List<UUID> files) {
    this.files = files;
  }

  public OffsetDateTime getModificationDate() {
    return modificationDate;
  }

  public void setModificationDate(OffsetDateTime modificationDate) {
    this.modificationDate = modificationDate;
  }



}
