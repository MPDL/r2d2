package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.model.BaseDateDb;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;

import java.time.OffsetDateTime;

public final class BaseDateDbBuilder {
  private OffsetDateTime creationDate;
  private OffsetDateTime modificationDate;
  private UserAccount creator;
  private UserAccount modifier;

  private BaseDateDbBuilder() {}

  public static BaseDateDbBuilder aBaseDateDb() {
    return new BaseDateDbBuilder();
  }

  public BaseDateDbBuilder creationDate(OffsetDateTime creationDate) {
    this.creationDate = creationDate;
    return this;
  }

  public BaseDateDbBuilder modificationDate(OffsetDateTime modificationDate) {
    this.modificationDate = modificationDate;
    return this;
  }

  public BaseDateDbBuilder creator(UserAccount creator) {
    this.creator = creator;
    return this;
  }

  public BaseDateDbBuilder modifier(UserAccount modifier) {
    this.modifier = modifier;
    return this;
  }

  public BaseDateDb build() {
    BaseDateDb baseDateDb = new BaseDateDb();
    baseDateDb.setCreationDate(creationDate);
    baseDateDb.setModificationDate(modificationDate);
    baseDateDb.setCreator(creator);
    baseDateDb.setModifier(modifier);
    return baseDateDb;
  }
}
