package de.mpg.mpdl.r2d2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.mpg.mpdl.r2d2.model.aa.UserAccountRO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@JsonPropertyOrder(value = {"id", "state"})
public class Dataset extends BaseDb {


  public enum State {
    PUBLIC,
    PRIVATE,
    WITHDRAWN
  }

  @Builder.Default
  @Enumerated(EnumType.STRING)
  private Dataset.State state = State.PRIVATE;

  @Builder.Default
  //@Type(type = "jsonb")
  //@Column(columnDefinition = "jsonb")
  private Integer latestVersion = 1;

  @Builder.Default
  //@Type(type = "jsonb")
  //@Column(columnDefinition = "jsonb")
  private Integer latestPublicVersion = null;

  @Builder.Default
  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb")
  private List<UserAccountRO> datamanager = new ArrayList<UserAccountRO>();


  public List<UserAccountRO> getDatamanager() {
    return datamanager;
  }

  public void setDatamanager(List<UserAccountRO> datamanager) {
    this.datamanager = datamanager;
  }

  public Dataset.State getState() {
    return state;
  }

  public void setState(Dataset.State state) {
    this.state = state;
  }

  public Integer getLatestVersion() {
    return latestVersion;
  }

  public void setLatestVersion(Integer latestVersion) {
    this.latestVersion = latestVersion;
  }

  public Integer getLatestPublicVersion() {
    return latestPublicVersion;
  }

  public void setLatestPublicVersion(Integer latestPublicVersion) {
    this.latestPublicVersion = latestPublicVersion;
  }


  @JsonIgnore
  @Transient
  public VersionId getLatestVersionId() {
    if (latestVersion != null) {
      return new VersionId(this.getId(), latestVersion);
    }
    return null;
  }

  @JsonIgnore
  @Transient
  public VersionId getLatestPublicVersionId() {
    if (latestPublicVersion != null) {
      return new VersionId(this.getId(), latestPublicVersion);
    }

    return null;
  }


}
