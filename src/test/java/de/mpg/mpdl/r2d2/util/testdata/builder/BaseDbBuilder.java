package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.model.BaseDb;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;

import java.time.OffsetDateTime;
import java.util.UUID;

public final class BaseDbBuilder {
  private OffsetDateTime creationDate;
  private OffsetDateTime modificationDate;
  private UserAccount creator;
  private UserAccount modifier;
  //@GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  private BaseDbBuilder() {}

  public static BaseDbBuilder aBaseDb() {
    return new BaseDbBuilder();
  }

  public BaseDbBuilder creationDate(OffsetDateTime creationDate) {
    this.creationDate = creationDate;
    return this;
  }

  public BaseDbBuilder modificationDate(OffsetDateTime modificationDate) {
    this.modificationDate = modificationDate;
    return this;
  }

  public BaseDbBuilder creator(UserAccount creator) {
    this.creator = creator;
    return this;
  }

  public BaseDbBuilder modifier(UserAccount modifier) {
    this.modifier = modifier;
    return this;
  }

  public BaseDbBuilder id(UUID id) {
    this.id = id;
    return this;
  }

  public BaseDb build() {
    BaseDb baseDb = new BaseDb();
    baseDb.setCreationDate(creationDate);
    baseDb.setModificationDate(modificationDate);
    baseDb.setCreator(creator);
    baseDb.setModifier(modifier);
    baseDb.setId(id);
    return baseDb;
  }
}
