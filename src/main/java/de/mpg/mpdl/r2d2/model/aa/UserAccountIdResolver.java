package de.mpg.mpdl.r2d2.model.aa;

import com.fasterxml.jackson.annotation.ObjectIdGenerator.IdKey;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import de.mpg.mpdl.r2d2.db.UserAccountRepository;
import de.mpg.mpdl.r2d2.util.AutowireHelper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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
