package de.mpg.mpdl.r2d2.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.annotations.UpdateTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.vladmihalcea.hibernate.type.array.IntArrayType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;

import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.model.aa.UserAccountRO;
import de.mpg.mpdl.r2d2.service.impl.DatasetVersionServiceDbImpl;

@EntityListeners(AuditingEntityListener.class)

@MappedSuperclass
@TypeDefs({@TypeDef(name = "json", typeClass = JsonStringType.class), @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
    @TypeDef(name = "string-array", typeClass = StringArrayType.class), @TypeDef(name = "int-array", typeClass = IntArrayType.class)})

public class BaseDateDb {

  public final static String[] userIgnoreJsonProperties =
      new String[] {"creationDate", "creator", "modificationDate", "modifier", "email", "roles"};

  private static Logger LOGGER = LoggerFactory.getLogger(BaseDateDb.class);


  @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false, updatable = false)
  @CreatedDate
  private OffsetDateTime creationDate;

  @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
  @LastModifiedDate
  private OffsetDateTime modificationDate;


  @ManyToOne(fetch = FetchType.LAZY)
  @CreatedBy
  @JsonIdentityReference(alwaysAsId = true)
  private UserAccount creator;


  @ManyToOne(fetch = FetchType.LAZY)
  @LastModifiedBy
  @JsonIdentityReference(alwaysAsId = true)
  private UserAccount modifier;


  public OffsetDateTime getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(OffsetDateTime creationDate) {
    this.creationDate = creationDate;
  }

  public OffsetDateTime getModificationDate() {
    return modificationDate;
  }

  public void setModificationDate(OffsetDateTime modificationDate) {
    this.modificationDate = modificationDate;
  }

  public UserAccount getCreator() {
    return creator;
  }

  public void setCreator(UserAccount creator) {
    this.creator = creator;
  }

  public UserAccount getModifier() {
    return modifier;
  }

  public void setModifier(UserAccount modifier) {
    this.modifier = modifier;
  }

}
