package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;

import java.time.OffsetDateTime;
import java.util.UUID;

public final class DatasetBuilder {
  private OffsetDateTime creationDate;
  private OffsetDateTime modificationDate;
  private UserAccount creator;
  private UserAccount modifier;
  //@GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;
  private Dataset.State state = Dataset.State.PRIVATE;
  //@Type(type = "jsonb")
  //@Column(columnDefinition = "jsonb")
  private Integer latestVersion = 1;
  //@Type(type = "jsonb")
  //@Column(columnDefinition = "jsonb")
  private Integer latestPublicVersion = null;

  private DatasetBuilder() {}

  public static DatasetBuilder aDataset() {
    return new DatasetBuilder();
  }

  public DatasetBuilder creationDate(OffsetDateTime creationDate) {
    this.creationDate = creationDate;
    return this;
  }

  public DatasetBuilder modificationDate(OffsetDateTime modificationDate) {
    this.modificationDate = modificationDate;
    return this;
  }

  public DatasetBuilder creator(UserAccount creator) {
    this.creator = creator;
    return this;
  }

  public DatasetBuilder modifier(UserAccount modifier) {
    this.modifier = modifier;
    return this;
  }

  public DatasetBuilder id(UUID id) {
    this.id = id;
    return this;
  }

  public DatasetBuilder state(Dataset.State state) {
    this.state = state;
    return this;
  }

  public DatasetBuilder latestVersion(Integer latestVersion) {
    this.latestVersion = latestVersion;
    return this;
  }

  public DatasetBuilder latestPublicVersion(Integer latestPublicVersion) {
    this.latestPublicVersion = latestPublicVersion;
    return this;
  }

  public Dataset build() {
    Dataset dataset = new Dataset();
    dataset.setCreationDate(creationDate);
    dataset.setModificationDate(modificationDate);
    dataset.setCreator(creator);
    dataset.setModifier(modifier);
    dataset.setId(id);
    dataset.setState(state);
    dataset.setLatestVersion(latestVersion);
    dataset.setLatestPublicVersion(latestPublicVersion);
    return dataset;
  }
}
