package de.mpg.mpdl.r2d2.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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
