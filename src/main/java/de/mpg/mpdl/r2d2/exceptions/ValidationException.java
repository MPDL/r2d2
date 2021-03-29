package de.mpg.mpdl.r2d2.exceptions;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends R2d2ApplicationException {


  public ValidationException(String message) {
    super(message);
    // TODO Auto-generated constructor stub
  }

  public ValidationException() {
    super();
    // TODO Auto-generated constructor stub
  }

  public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
    // TODO Auto-generated constructor stub
  }

  public ValidationException(String message, Throwable cause) {
    super(message, cause);
    // TODO Auto-generated constructor stub
  }

  public ValidationException(Throwable cause) {
    super(cause);
    // TODO Auto-generated constructor stub
  }

}
