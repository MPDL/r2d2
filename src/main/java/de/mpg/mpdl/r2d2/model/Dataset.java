package de.mpg.mpdl.r2d2.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.mpg.mpdl.r2d2.model.aa.User;

@Entity
public class Dataset extends BaseDb {


  public enum State {
    PUBLIC,
    PRIVATE,
    WITHDRAWN
  }

  @Enumerated(EnumType.STRING)
  private Dataset.State state = State.PRIVATE;

  @ManyToMany(fetch = FetchType.EAGER)
  @JsonIgnoreProperties(value = {"creationDate", "creator", "modificationDate", "modifier", "email", "roles"})
  private List<User> datamanager;


  private List<User> getDatamanager() {
    return datamanager;
  }

  private void setDatamanager(List<User> datamanager) {
    this.datamanager = datamanager;
  }

  public Dataset.State getState() {
    return state;
  }

  public void setState(Dataset.State state) {
    this.state = state;
  }



}
