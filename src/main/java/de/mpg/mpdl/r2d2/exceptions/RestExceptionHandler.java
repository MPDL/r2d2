package de.mpg.mpdl.r2d2.exceptions;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityExistsException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("time", LocalDateTime.now());
    body.put("status", status.value());

    List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(x -> x.getDefaultMessage()).collect(Collectors.toList());

    body.put("errors", errors);

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<Object> handleAllExceptions(Exception ex, HttpServletRequest request) {
    return new ResponseEntity<Object>(details(ex, request), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler({RuntimeException.class})
  public ResponseEntity<Object> handleRuntimeException(Exception ex, HttpServletRequest request) {
    return new ResponseEntity<Object>(details(ex, request), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private Map<String, Object> details(Exception ex, HttpServletRequest req) {
    Map<String, Object> errors = new LinkedHashMap<>();
    errors.put("time", LocalDateTime.now());
    errors.put("cause", ex.getClass().getSimpleName());
    errors.put("message", ex.getMessage());
    errors.put("uri", req.getRequestURL());
    return errors;
  }

}
