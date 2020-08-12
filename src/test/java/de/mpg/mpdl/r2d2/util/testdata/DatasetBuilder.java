package de.mpg.mpdl.r2d2.util.testdata;

import java.time.OffsetDateTime;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.util.Utils;

@Component
@Scope("prototype")
public class DatasetBuilder {

  @PersistenceContext
  private EntityManager entityManager;

  private final Dataset dataset = new Dataset();

  public Dataset create() {
    return dataset;
  }

  public DatasetBuilder setcurrentCreationAndModificationDate() {
    OffsetDateTime currentDateTime = Utils.generateCurrentDateTimeForDatabase();

    this.dataset.setCreationDate(currentDateTime);
    this.dataset.setModificationDate(currentDateTime);

    return this;
  }

  @Transactional
  public Dataset persist() {
    Dataset dataset = this.create();

    entityManager.persist(dataset);

    return dataset;
  }

}
