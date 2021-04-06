package de.mpg.mpdl.r2d2.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ReviewToken {

  @Column(unique = true)
  private String token;

  @Id
  private UUID dataset;

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public UUID getDataset() {
    return dataset;
  }

  public void setDataset(UUID dataset) {
    this.dataset = dataset;
  }

}
