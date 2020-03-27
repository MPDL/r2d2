package de.mpg.mpdl.r2d2.db;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentityGenerator;
import org.hibernate.id.UUIDGenerator;

/**
 * This identity generator is used to allow the use of ids, which are already set in an entity. Only
 * if the id is null, the system generates an id.
 * 
 * @author haarlae1
 *
 */
public class UseExistingIdOtherwiseGenerateUsingUUID extends UUIDGenerator {

  @Override
  public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
    Serializable id = session.getEntityPersister(null, object).getClassMetadata().getIdentifier(object, session);
    return id != null ? id : super.generate(session, object);
  }

}
