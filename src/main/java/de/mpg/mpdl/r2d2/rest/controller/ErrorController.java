package de.mpg.mpdl.r2d2.rest.controller;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// @RestController
@RequestMapping({ErrorController.ERROR_PATH})
public class ErrorController extends AbstractErrorController {

  static final String ERROR_PATH = "/error";

  public ErrorController(ErrorAttributes errorAttributes) {
    super(errorAttributes, Collections.emptyList());
  }

  @RequestMapping
  public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
    Map<String, Object> body = this.getErrorAttributes(request, ErrorAttributeOptions.defaults());
    HttpStatus status = this.getStatus(request);
    return new ResponseEntity<>(body, status);
  }

  @Override
  public String getErrorPath() {
    return ERROR_PATH;
  }

}
