package de.mpg.mpdl.r2d2.model.aa;

/**
 * Wrapper object for UserAccount to support authorization mechanisms. Can be extended at a later
 * stage, e.g. for IP-based stuff etc.
 * 
 * @author haarlae1
 *
 */
public class Principal {

  private UserAccount userAccount;

  public Principal(UserAccount userAccount) {
    super();
    this.userAccount = userAccount;
  }

  public UserAccount getUserAccount() {
    return userAccount;
  }

  public void setUserAccount(UserAccount userAccount) {
    this.userAccount = userAccount;
  }



}
