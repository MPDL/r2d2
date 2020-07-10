package de.mpg.mpdl.r2d2.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import de.mpg.mpdl.r2d2.model.aa.UserAccountRO;

@Entity
@JsonPropertyOrder(value = {"id", "state"})
public class Dataset extends BaseDb {


  public enum State {
    PUBLIC,
    PRIVATE,
    WITHDRAWN
  }

  @Enumerated(EnumType.STRING)
  private Dataset.State state = State.PRIVATE;

  //@Type(type = "jsonb")
  //@Column(columnDefinition = "jsonb")
  private Integer latestVersion = 1;

  //@Type(type = "jsonb")
  //@Column(columnDefinition = "jsonb")
  private Integer latestPublicVersion = null;


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
