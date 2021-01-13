package de.mpg.mpdl.r2d2.util.testdata;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class TestDataBuilder {

  @Lookup
  public DatasetBuilder newDataset() {
    return null;
  }

  @Lookup
  public UserAccountBuilder newUserAccount() {
    return null;
  }

  @Lookup
  public R2D2PrincipalBuilder newR2D2Principal(String username, String password, Collection<? extends GrantedAuthority> authorities) {
    return null;
  }

  @Lookup
  public R2D2PrincipalBuilder newR2D2Principal(String username, String password) {
    return null;
  }

}
