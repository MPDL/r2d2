package de.mpg.mpdl.r2d2.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.array.IntArrayType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;

import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.model.aa.UserAccountRO;

@MappedSuperclass
@TypeDefs({@TypeDef(name = "json", typeClass = JsonStringType.class), @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
    @TypeDef(name = "string-array", typeClass = StringArrayType.class), @TypeDef(name = "int-array", typeClass = IntArrayType.class)})

public class BaseDateDb {

  public final static String[] userIgnoreJsonProperties =
      new String[] {"creationDate", "creator", "modificationDate", "modifier", "email", "roles"};



  @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false, updatable = false)
  private OffsetDateTime creationDate;

  @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
  private OffsetDateTime modificationDate;


  @Embedded
  @AttributeOverrides({@AttributeOverride(name = "id", column = @Column(name = "creator_id")),
      @AttributeOverride(name = "name", column = @Column(name = "creator_name"))})
  private UserAccountRO creator;


  @Embedded
  @AttributeOverrides({@AttributeOverride(name = "id", column = @Column(name = "modifier_id")),
      @AttributeOverride(name = "name", column = @Column(name = "modifier_name"))})
  private UserAccountRO modifier;



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

  public UserAccountRO getCreator() {
    return creator;
  }

  public void setCreator(UserAccountRO creator) {
    this.creator = creator;
  }

  public UserAccountRO getModifier() {
    return modifier;
  }

  public void setModifier(UserAccountRO modifier) {
    this.modifier = modifier;
  }

}
