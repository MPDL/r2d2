package de.mpg.mpdl.r2d2.model.aa;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * Wrapper object for UserAccount to support authorization mechanisms. Can be extended at a later
 * stage, e.g. for IP-based stuff etc.
 * 
 * @author haarlae1
 *
 */
public class R2D2Principal extends User {

  public R2D2Principal(String username, String password, Collection<? extends GrantedAuthority> authorities) {
    super(username, password, authorities);
    // TODO Auto-generated constructor stub
  }

  public R2D2Principal(String username, String password, boolean enabled, Collection<? extends GrantedAuthority> authorities) {
    super(username, password, enabled, true, true, true, authorities);
  }

  private UserAccount userAccount;

  private String reviewToken;

  public UserAccount getUserAccount() {
    return userAccount;
  }

  public void setUserAccount(UserAccount userAccount) {
    this.userAccount = userAccount;
  }

  public String getReviewToken() {
    return reviewToken;
  }

  public void setReviewToken(String readToken) {
    this.reviewToken = readToken;
  }

  public boolean hasAuthentication() {
    return userAccount != null || reviewToken != null;
  }

}
