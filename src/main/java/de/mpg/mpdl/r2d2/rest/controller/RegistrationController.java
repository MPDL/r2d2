package de.mpg.mpdl.r2d2.rest.controller;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import de.mpg.mpdl.r2d2.exceptions.NotFoundException;
import de.mpg.mpdl.r2d2.exceptions.R2d2ApplicationException;
import de.mpg.mpdl.r2d2.model.aa.LocalUserAccount;
import de.mpg.mpdl.r2d2.registration.MailNotificationEvent;
import de.mpg.mpdl.r2d2.registration.PasswordChangeRequest;
import de.mpg.mpdl.r2d2.registration.RegistrationRequest;
import de.mpg.mpdl.r2d2.service.UserService;

@RestController
//@RequestMapping(value = "/register")
public class RegistrationController {

  private final Logger LOGGER = LoggerFactory.getLogger(getClass());
  private final String REGISTRATION_CONFIRMATION_SUBJECT = "registration.confirmation.subject";
  private final String REGISTRATION_CONFIRMATION_MESSAGE = "registration.confirmation.message";
  private final String REGISTRATION_CONFIRMATION_SUCCESS = "registration.confirmation.success";
  private final String REGISTRATION_CONFIRMATION_EXPIRED = "registration.confirmation.expired";
  private final String PASSWORD_FORGOTTEN_SUBJECT = "password.forgotten.subject";
  private final String PASSWORD_FORGOTTEN_MESSAGE = "password.forgotten.message";
  private final String PASSWORD_FORGOTTEN_SUCCESS = "password.forgotten.success";


  @Autowired
  private UserService userService;

  @Autowired
  ApplicationEventPublisher eventPublisher;

  @Autowired
  private MessageSource messages;

  public RegistrationController() {

  }

  @PostMapping(value = "/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest userRequest, HttpServletRequest servletRequest)
      throws EntityExistsException {

    LocalUserAccount user = userService.registerNewUser(userRequest);
    if (user != null) {
      Map<String, String> map = new LinkedHashMap<>();
      map.put("subject", messages.getMessage(REGISTRATION_CONFIRMATION_SUBJECT, null, Locale.getDefault()));
      map.put("message", messages.getMessage(REGISTRATION_CONFIRMATION_MESSAGE, null, Locale.getDefault()));
      map.put("link", sendConfirmationToken(user, servletRequest.getRequestURL().toString()));
      // String message = String.format("Confirmation token sent to %s",
      // user.getUsername());
      // eventPublisher.publishEvent(new MailNotificationEvent(user,
      // servletRequest.getLocale(), servletRequest.getRequestURL().toString() +
      // "/confirmation", REGISTRATION_CONFIRMATION_SUBJECT,
      // REGISTRATION_CONFIRMATION_MESSAGE));

      return new ResponseEntity<>(map, HttpStatus.CREATED);
    }

    return null;

  }

  @GetMapping(value = "/register/confirmation")
  public ResponseEntity<?> confirmRegistration(@RequestParam("token") String token, HttpServletRequest request)
      throws R2d2ApplicationException {

    String valid = userService.validateConfirmationToken(token);
    if (valid != null) {
      throw new R2d2ApplicationException("your confirmation token is " + valid.toLowerCase());
    }
    LocalUserAccount user = userService.activateUser(token);
    Map<String, String> map =
        Collections.singletonMap("SUCCESS", messages.getMessage(REGISTRATION_CONFIRMATION_SUCCESS, null, Locale.getDefault()));
    return new ResponseEntity<>(map, HttpStatus.ACCEPTED);
  }

  @GetMapping(value = "/password/reset")
  public ResponseEntity<?> passwordReset(@RequestParam("email") String email, HttpServletRequest request) throws R2d2ApplicationException {
    LocalUserAccount user = userService.getByEmail(email);
    if (user == null) {
      throw new NotFoundException(String.format("User with email %s NOT FOUND!", email));
    }
    Map<String, String> map = new LinkedHashMap<>();
    map.put("subject", messages.getMessage(PASSWORD_FORGOTTEN_SUBJECT, null, Locale.getDefault()));
    map.put("message", messages.getMessage(PASSWORD_FORGOTTEN_MESSAGE, null, Locale.getDefault()));
    map.put("link", sendPasswordResetToken(user, request.getRequestURL().toString()));
    // String message = String.format("Pasword Reset token sent to %s",
    // user.getUsername());
    // eventPublisher.publishEvent(new MailNotificationEvent(user,
    // servletRequest.getLocale(), servletRequest.getRequestURL().toString() +
    // "/confirmation", PASSWORD_FORGOTTEN_SUBJECT,
    // PASSWORD_FORGOTTEN_MESSAGE));

    return new ResponseEntity<>(map, HttpStatus.OK);
  }

  @GetMapping(value = "/password/reset/confirm")
  public ResponseEntity<?> passwordConfirm(@RequestParam("token") String token, HttpServletRequest request)
      throws R2d2ApplicationException {
    String valid = userService.validateConfirmationToken(token);
    if (valid != null) {
      throw new R2d2ApplicationException("your password reset token is " + valid.toLowerCase());
    }
    Map<String, String> map = Collections.singletonMap("Post your reset request 2: ", request.getRequestURL().toString());
    return new ResponseEntity<>(map, HttpStatus.OK);
  }

  @PostMapping(value = "/password/reset")
  public ResponseEntity<?> passwordSave(@RequestBody @Valid PasswordChangeRequest pcr, HttpServletRequest request)
      throws R2d2ApplicationException {
    String valid = userService.validateConfirmationToken(pcr.getToken());
    if (valid != null) {
      throw new R2d2ApplicationException("your password reset token is " + valid.toLowerCase());
    }
    LocalUserAccount user = userService.getUser(pcr.getToken());
    userService.resetPassword(user, pcr.getNewPass1());
    Map<String, String> map =
        Collections.singletonMap("SUCCESS", messages.getMessage(PASSWORD_FORGOTTEN_SUCCESS, null, Locale.getDefault()));
    return new ResponseEntity<>(map, HttpStatus.ACCEPTED);
  }

  @PostMapping(value = "/password/change")
  @PreAuthorize("hasRole('ROLE_USER')")
  public ResponseEntity<?> passwordChange(@RequestBody @Valid PasswordChangeRequest pcr, HttpServletRequest request)
      throws R2d2ApplicationException {

    LocalUserAccount user = userService.getByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    if (!userService.validateOldPassword(user, pcr.getOldPass())) {
      throw new R2d2ApplicationException("invalid old password!");
    }
    userService.resetPassword(user, pcr.getNewPass1());
    Map<String, String> map =
        Collections.singletonMap("SUCCESS", messages.getMessage(PASSWORD_FORGOTTEN_SUCCESS, null, Locale.getDefault()));
    return new ResponseEntity<>(map, HttpStatus.ACCEPTED);
  }


  private String sendConfirmationToken(LocalUserAccount user, String url) {
    final String token = UUID.randomUUID().toString();
    userService.createConfirmationToken(user, token);
    return url + "/confirmation?token=" + token;

  }

  private String sendPasswordResetToken(LocalUserAccount user, String url) {
    final String token = UUID.randomUUID().toString();
    userService.createConfirmationToken(user, token);
    return url + "/confirm?token=" + token;

  }

}
