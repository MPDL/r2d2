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

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.vladmihalcea.hibernate.type.array.IntArrayType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;

import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.model.aa.UserAccountRO;

@MappedSuperclass
public class BaseDb extends BaseDateDb {


  @Id
  //@GeneratedValue(strategy = GenerationType.AUTO)
  @GenericGenerator(name = "UseExistingIdOtherwiseGenerateUsingUUID",
      strategy = "de.mpg.mpdl.r2d2.db.UseExistingIdOtherwiseGenerateUsingUUID")
  @GeneratedValue(generator = "UseExistingIdOtherwiseGenerateUsingUUID")
  private UUID id;


  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

}
