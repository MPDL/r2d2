package de.mpg.mpdl.r2d2.rest.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.R2d2ApplicationException;
import de.mpg.mpdl.r2d2.service.impl.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {

  @Autowired
  AdminService service;

  @GetMapping(value = "/test")
  public ResponseEntity<?> testTheController(HttpServletRequest request) throws AuthorizationException {
    String testInfo = service.test();
    return new ResponseEntity<>(testInfo, HttpStatus.OK);
  }

}
