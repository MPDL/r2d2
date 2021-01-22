package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Manually created Builder for R2D2Principal.
 *
 * R2D2Principal inherits from User, which has its own private Builder and no Setter-Methods. =>
 * Create R2D2PrincipalBuilder manually with a initial R2D2Principal-Parameterized-Constructor call.
 */
public class R2D2PrincipalBuilder {

  private R2D2Principal r2D2Principal;

  private R2D2PrincipalBuilder() {}

  public static R2D2PrincipalBuilder aR2D2Principal(String username, String password, Collection<? extends GrantedAuthority> authorities) {
    R2D2PrincipalBuilder r2D2PrincipalBuilder = new R2D2PrincipalBuilder();

    r2D2PrincipalBuilder.r2D2Principal = new R2D2Principal(username, password, authorities);

    return r2D2PrincipalBuilder;
  }

  public R2D2PrincipalBuilder userAccount(UserAccount userAccount) {
    this.r2D2Principal.setUserAccount(userAccount);
    return this;
  }

  public R2D2Principal build() {
    return r2D2Principal;
  }

}
