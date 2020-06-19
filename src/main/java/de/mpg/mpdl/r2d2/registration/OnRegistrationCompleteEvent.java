package de.mpg.mpdl.r2d2.registration;

import java.util.Locale;

import org.springframework.context.ApplicationEvent;

import de.mpg.mpdl.r2d2.model.aa.LocalUserAccount;

@SuppressWarnings("serial")
public class OnRegistrationCompleteEvent extends ApplicationEvent {

  private final String appUrl;
  private final Locale locale;
  private final LocalUserAccount user;

  public OnRegistrationCompleteEvent(final LocalUserAccount user, final Locale locale, final String appUrl) {
    super(user);
    this.user = user;
    this.locale = locale;
    this.appUrl = appUrl;
  }

  public String getAppUrl() {
    return appUrl;
  }

  public Locale getLocale() {
    return locale;
  }

  public LocalUserAccount getUser() {
    return user;
  }

}
