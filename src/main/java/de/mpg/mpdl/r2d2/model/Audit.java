package de.mpg.mpdl.r2d2.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

import de.mpg.mpdl.r2d2.model.aa.UserAccount;

@Entity
public class Audit {



  public enum Action {
    CREATE,
    UPDATE,
    DELETE,
    PUBLISH,
    WITHDRAW,
    ADD_FILES,
    REMOVE_FILES
  }

  @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false, updatable = false)
  @CreationTimestamp
  private OffsetDateTime date;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private UserAccount user;

  //@ManyToOne(fetch = FetchType.LAZY, optional=false)
  //@OnDelete(action = OnDeleteAction.CASCADE)
  private VersionId dataset;

  @Type(type = "string-array")
  @Column(columnDefinition = "jsonb")
  private List<UUID> files;

  @Enumerated(EnumType.STRING)
  private Action action;


  @Id
  //@GeneratedValue(strategy = GenerationType.AUTO)
  @GenericGenerator(name = "UseExistingIdOtherwiseGenerateUsingUUID",
      strategy = "de.mpg.mpdl.r2d2.db.UseExistingIdOtherwiseGenerateUsingUUID")
  @GeneratedValue(generator = "UseExistingIdOtherwiseGenerateUsingUUID")
  private UUID id;



  public Audit(Action action, VersionId dataset, UserAccount user) {
    super();
    this.action = action;
    this.dataset = dataset;
    this.user = user;
  }

  public Audit() {

  }


  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public OffsetDateTime getDate() {
    return date;
  }

  public void setDate(OffsetDateTime date) {
    this.date = date;
  }

  public UserAccount getUser() {
    return user;
  }

  public void setUser(UserAccount user) {
    this.user = user;
  }

  public VersionId getDataset() {
    return dataset;
  }

  public void setDataset(VersionId dataset) {
    this.dataset = dataset;
  }

  public Action getAction() {
    return action;
  }

  public void setAction(Action action) {
    this.action = action;
  }

  public List<UUID> getFiles() {
    return files;
  }

  public void setFiles(List<UUID> files) {
    this.files = files;
  }



}
