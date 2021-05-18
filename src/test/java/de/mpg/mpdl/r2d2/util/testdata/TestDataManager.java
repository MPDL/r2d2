package de.mpg.mpdl.r2d2.util.testdata;

import de.mpg.mpdl.r2d2.model.Audit;
import de.mpg.mpdl.r2d2.model.BaseDateDb;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.File;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;

/**
 * Class to persist Test Data objects.
 * <p>
 * Wrapper class for the EntityManager.
 */
@Component
public class TestDataManager {

  //TODO: Use EntityFactory to get the EntityManager?
  //EntityManager entityManager = entityManagerFactory.createEntityManager()

  //TODO: Check thread safety of the entityManager
  @PersistenceContext
  private EntityManager entityManager;

  @Transactional
  public void persist(Object... objectsToPersist) {
    Arrays.stream(objectsToPersist).forEach(object -> entityManager.persist(object));
  }

  /**
   * Search for all entities of the specified class.
   * <p>
   * Using eager loading: The entities with all their related associations (also LAZY fields!) are
   * loaded and returned.
   * 
   * @param entityClass the entity class
   * @param <T> the class type
   * @return a List of all found entity instances of type T
   */
  @Transactional
  public <T> List<T> findAll(Class<T> entityClass) {
    EntityGraph<T> entityGraphOfLazyNodes = this.createDynamicEntityGraphForLazyAttributes(entityClass);

    List<T> entities = entityManager.createQuery("Select entity from " + entityClass.getSimpleName() + " entity")
        .setHint("javax.persistence.loadgraph", entityGraphOfLazyNodes).getResultList();

    return entities;
  }

  private <T> EntityGraph createDynamicEntityGraphForLazyAttributes(Class<T> entityClass) {
    EntityGraph<T> entityGraph = entityManager.createEntityGraph(entityClass);

    if (DatasetVersion.class == entityClass) {
      entityGraph.addAttributeNodes("dataset");
    } else if (File.class == entityClass) {
      entityGraph.addAttributeNodes("datasets");
    } else if (Audit.class == entityClass) {
      entityGraph.addAttributeNodes("user");
    }
    if (BaseDateDb.class.isAssignableFrom(entityClass)) {
      entityGraph.addAttributeNodes("creator");
      entityGraph.addAttributeNodes("modifier");
    }

    return entityGraph;
  }

}
