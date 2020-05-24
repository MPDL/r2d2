package de.mpg.mpdl.r2d2.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends R2d2ApplicationException {

  public NotFoundException(String message) {
    super(message);
    // TODO Auto-generated constructor stub
  }

  public NotFoundException() {
    super();
    // TODO Auto-generated constructor stub
  }

  public NotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
    // TODO Auto-generated constructor stub
  }

  public NotFoundException(String message, Throwable cause) {
    super(message, cause);
    // TODO Auto-generated constructor stub
  }

  public NotFoundException(Throwable cause) {
    super(cause);
    // TODO Auto-generated constructor stub
  }

}
