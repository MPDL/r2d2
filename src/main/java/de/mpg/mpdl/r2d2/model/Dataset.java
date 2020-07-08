package de.mpg.mpdl.r2d2.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.model.aa.UserAccountRO;

@Entity
public class Dataset extends BaseDb {


  public enum State {
    PUBLIC,
    PRIVATE,
    WITHDRAWN
  }

  @Enumerated(EnumType.STRING)
  private Dataset.State state = State.PRIVATE;

  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb")
  private List<UserAccountRO> datamanager = new ArrayList<UserAccountRO>();


  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb")
  private List<DatasetVersionRO> versions = new ArrayList<DatasetVersionRO>();
  
  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb")
  private DatasetVersionRO latestPublicVersion;

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

  public List<DatasetVersionRO> getVersions() {
    return versions;
  }

  public void setVersions(List<DatasetVersionRO> versions) {
    this.versions = versions;
  }

  public DatasetVersionRO getLatestPublicVersion() {
    return latestPublicVersion;
  }

  public void setLatestPublicVersion(DatasetVersionRO latestPublicVersion) {
    this.latestPublicVersion = latestPublicVersion;
  }



}
