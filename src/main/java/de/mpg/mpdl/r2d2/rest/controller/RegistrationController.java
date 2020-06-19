package de.mpg.mpdl.r2d2.rest.controller;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import de.mpg.mpdl.r2d2.exceptions.R2d2ApplicationException;
import de.mpg.mpdl.r2d2.model.aa.LocalUserAccount;
import de.mpg.mpdl.r2d2.registration.RegistrationRequest;
import de.mpg.mpdl.r2d2.service.UserService;

@RestController
@RequestMapping(value = "/register")
public class RegistrationController {

  private final Logger LOGGER = LoggerFactory.getLogger(getClass());

  @Autowired
  private UserService userService;

  @Autowired
  ApplicationEventPublisher eventPublisher;

  public RegistrationController() {

  }

  @PostMapping
  public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest userRequest, HttpServletRequest servletRequest) {

    LocalUserAccount user = userService.registerNewUser(userRequest);
    if (user != null) {
      Map<String, String> map = Collections.singletonMap("link", sendConfirmationTokenAsHeader(user, getAppUrl(servletRequest)));
      /*
      eventPublisher.publishEvent(
      		new OnRegistrationCompleteEvent(user, servletRequest.getLocale(), getAppUrl(servletRequest)));
      		*/
      return new ResponseEntity<>(map, HttpStatus.CREATED);
    }

    return null;

    // HttpHeaders headers = new HttpHeaders();
    // headers.add("ConfirmationToken", sendConfirmationTokenAsHeader(user, getAppUrl(servletRequest)));
    // headers.setLocation(
    //		ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri());

  }

  @GetMapping(value = "/confirmation")
  public ResponseEntity<?> confirmRegistration(@RequestParam("token") String token, HttpServletRequest request)
      throws R2d2ApplicationException {
    String valid = userService.checkConfirmationToken(token);
    if (valid.equals("VALID")) {
      return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    throw new R2d2ApplicationException("your confirmation token is " + valid.toLowerCase());
  }

  @DeleteMapping
  public ResponseEntity<?> unregister(@RequestBody LocalUserAccount user) {
    LocalUserAccount u2delete = userService.getByEmail(user.getUser().getEmail());
    if (u2delete == null) {
      throw new EntityNotFoundException("no user registered for " + user.getUser().getEmail());
    }
    if (!user.getPassword().equals(u2delete.getPassword())) {
      throw new IllegalArgumentException("invalid password");
    }
    userService.deleteUser(u2delete);
    return new ResponseEntity<>(HttpStatus.GONE);
  }

  private String getAppUrl(HttpServletRequest request) {
    return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
  }

  private String sendConfirmationTokenAsHeader(LocalUserAccount user, String url) {
    final String token = UUID.randomUUID().toString();
    userService.createConfirmationToken(user, token);
    return url + "/register/confirmation?token=" + token;

  }

}
