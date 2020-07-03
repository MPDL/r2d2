package de.mpg.mpdl.r2d2.registration;

import java.util.Locale;

import org.springframework.context.ApplicationEvent;

import de.mpg.mpdl.r2d2.model.aa.LocalUserAccount;

@SuppressWarnings("serial")
public class MailNotificationEvent extends ApplicationEvent {

  private final String requestUrl;
  private final String subject;
  private final String message;
  private final Locale locale;
  private final LocalUserAccount user;

  public MailNotificationEvent(final LocalUserAccount user, final Locale locale, final String url, String subject, String msg) {
    super(user);
    this.user = user;
    this.locale = locale;
    this.requestUrl = url;
    this.subject = subject;
    this.message = msg;
  }

  public String getRequestUrl() {
    return requestUrl;
  }
  
  public String getSubject() {
	  return subject;
  }
  
  public String getMessage() {
	  return message;
  }

  public Locale getLocale() {
    return locale;
  }

  public LocalUserAccount getUser() {
    return user;
  }

}
