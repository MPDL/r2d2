package de.mpg.mpdl.r2d2.util.testdata;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;

@Component
@Scope("prototype")
public class R2D2PrincipalBuilder {

  private final R2D2Principal r2D2Principal;

  public R2D2PrincipalBuilder(String username, String password, Collection<? extends GrantedAuthority> authorities) {
    this.r2D2Principal = new R2D2Principal(username, password, authorities);
  }

  public R2D2PrincipalBuilder(String username, String password) {
    this.r2D2Principal = new R2D2Principal(username, password, new ArrayList<GrantedAuthority>());
  }

  public R2D2Principal create() {
    return this.r2D2Principal;
  }

  public R2D2PrincipalBuilder setUserAccount(UserAccount userAccount) {
    this.r2D2Principal.setUserAccount(userAccount);

    return this;
  }

}
