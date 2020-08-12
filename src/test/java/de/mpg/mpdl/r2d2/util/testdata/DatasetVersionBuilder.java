package de.mpg.mpdl.r2d2.util.testdata;

import java.time.OffsetDateTime;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.DatasetVersionMetadata;
import de.mpg.mpdl.r2d2.util.Utils;

@Component
@Scope("prototype")
public class DatasetVersionBuilder {

  //TODO: Check thread safety of the entityManager
  @PersistenceContext
  private EntityManager entityManager;

  private final DatasetVersion datasetVersion = new DatasetVersion();

  public DatasetVersion create() {
    return datasetVersion;
  }

  public DatasetVersionBuilder setDataset(Dataset dataset) {
    this.datasetVersion.setDataset(dataset);

    return this;
  }

  public DatasetVersionBuilder setcurrentCreationAndModificationDate() {
    OffsetDateTime currentDateTime = Utils.generateCurrentDateTimeForDatabase();

    this.datasetVersion.setCreationDate(currentDateTime);
    this.datasetVersion.setModificationDate(currentDateTime);

    return this;
  }

  public DatasetVersionBuilder setVersionNumber(int versionNumber) {
    this.datasetVersion.setVersionNumber(versionNumber);

    return this;
  }

  public DatasetVersionBuilder setMetadata(String title) {
    DatasetVersionMetadata datasetVersionMetadata = new DatasetVersionMetadata();
    datasetVersionMetadata.setTitle(title);
    this.datasetVersion.setMetadata(datasetVersionMetadata);

    return this;
  }

  //TODO: Move the persist-methods of all Builders to a separate save/persist/index-class
  @Transactional
  public DatasetVersion persist() {
    DatasetVersion datasetVersion = this.create();

    //  entityManager.getTransaction().begin();
    //    entityManager.persist(datasetVersion);
    //    entityManager.refresh(datasetVersion);
    //Why merge instead of persist? Because DatasetVersion has ~same ID as Dataset!?
    entityManager.merge(datasetVersion);
    //    entityManager.getTransaction().commit();

    return datasetVersion;
  }


}
