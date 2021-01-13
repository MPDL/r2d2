package de.mpg.mpdl.r2d2.util.testdata;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Class to persist Test Data objects.
 *
 * Wrapper class for the EntityManager.
 *
 */
@Component
public class EntityManagerWrapper {

  //TODO: Use EntityFactory to get the EntityManager?
  //EntityManager entityManager = entityManagerFactory.createEntityManager()

  //TODO: Check thread safety of the entityManager
  @PersistenceContext
  private EntityManager entityManager;

  @Transactional
  public void persist(Object objectToPersist) {
    entityManager.persist(objectToPersist);
  }

  @Transactional
  public <T> T merge(T objectToMerge) {
    return entityManager.merge(objectToMerge);
  }

  public EntityManager getEntityManager() {
    return this.entityManager;
  }

}
