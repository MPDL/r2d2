package de.mpg.mpdl.r2d2.registration.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import de.mpg.mpdl.r2d2.registration.RegistrationRequest;


public class PasswordMatchValidator implements ConstraintValidator<PasswordMatches, Object> {

  @Override
  public void initialize(PasswordMatches annotation) {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean isValid(Object obj, ConstraintValidatorContext ctx) {
    final RegistrationRequest user = (RegistrationRequest) obj;
    return user.getPass().equals(user.getMatch());
  }

}
