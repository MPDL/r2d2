package de.mpg.mpdl.r2d2.registration;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.r2d2.model.aa.LocalUserAccount;
import de.mpg.mpdl.r2d2.service.UserService;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

  @Autowired
  private UserService service;

  @Autowired
  private MessageSource messages;

  @Autowired(required = false)
  private JavaMailSender mailSender;

  @Value("${support.email}")
  private String from;

  @Override
  public void onApplicationEvent(final OnRegistrationCompleteEvent event) {
    this.confirmRegistration(event);
  }

  private void confirmRegistration(final OnRegistrationCompleteEvent event) {
    final LocalUserAccount user = event.getUser();
    final String token = UUID.randomUUID().toString();
    service.createConfirmationToken(user, token);

    final SimpleMailMessage email = composeMail(event, user, token);
    mailSender.send(email);
  }

  private final SimpleMailMessage composeMail(final OnRegistrationCompleteEvent event, final LocalUserAccount user, final String token) {
    final String recipientAddress = user.getUser().getEmail();
    final String subject = messages.getMessage("registration.email.subject", null, event.getLocale());
    final String confirmationUrl = event.getAppUrl() + "/registration/confirmation?token=" + token;
    final String message = messages.getMessage("registration.email.body", null, event.getLocale());
    final SimpleMailMessage email = new SimpleMailMessage();
    email.setTo(recipientAddress);
    email.setSubject(subject);
    email.setText(message + " \r\n" + confirmationUrl);
    email.setFrom(from);
    return email;
  }

}
