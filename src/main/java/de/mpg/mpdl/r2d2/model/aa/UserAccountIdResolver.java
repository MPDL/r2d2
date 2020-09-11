package de.mpg.mpdl.r2d2.model.aa;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.ObjectIdGenerator.IdKey;
import com.fasterxml.jackson.annotation.ObjectIdResolver;

import de.mpg.mpdl.r2d2.db.DatasetVersionRepository;
import de.mpg.mpdl.r2d2.db.UserAccountRepository;
import de.mpg.mpdl.r2d2.exceptions.R2d2ApplicationException;
import de.mpg.mpdl.r2d2.util.AutowireHelper;

public class UserAccountIdResolver implements ObjectIdResolver {

	@Autowired
  private UserAccountRepository users;

  @Override
  public void bindItem(IdKey id, Object pojo) {

  }

  @Override
  public Object resolveId(IdKey id) {
	  AutowireHelper.autowire(this, this.users);
	  UserAccount ua = users.findById((UUID) id.key).orElseThrow(() -> new RuntimeException("SHIT HAPPENS!"));
    return ua;
  }

  @Override
  public ObjectIdResolver newForDeserialization(Object context) {
    return this;
  }

  @Override
  public boolean canUseFor(ObjectIdResolver resolverType) {
    return getClass().isAssignableFrom(resolverType.getClass());
  }

}
