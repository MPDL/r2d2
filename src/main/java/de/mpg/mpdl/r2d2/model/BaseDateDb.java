package de.mpg.mpdl.r2d2.model;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.vladmihalcea.hibernate.type.array.IntArrayType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.OffsetDateTime;

@EntityListeners(AuditingEntityListener.class)

@MappedSuperclass
@TypeDefs({@TypeDef(name = "json", typeClass = JsonStringType.class), @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
    @TypeDef(name = "string-array", typeClass = StringArrayType.class), @TypeDef(name = "int-array", typeClass = IntArrayType.class)})

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BaseDateDb {

  public final static String[] userIgnoreJsonProperties =
      new String[] {"creationDate", "creator", "modificationDate", "modifier", "email", "roles"};



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
