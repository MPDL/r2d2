package de.mpg.mpdl.r2d2.registration.validation;

import java.lang.reflect.Field;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanUtils;

import de.mpg.mpdl.r2d2.exceptions.R2d2ApplicationException;
import de.mpg.mpdl.r2d2.registration.RegistrationRequest;


public class PasswordMatchValidator implements ConstraintValidator<PasswordMatches, Object> {

  private String pattern;
  private String match;

  @Override
  public void initialize(PasswordMatches annotation) {
    pattern = annotation.pattern();
    match = annotation.match();

  }

  @Override
  public boolean isValid(Object obj, ConstraintValidatorContext ctx) {
    try {
      Field patternField = obj.getClass().getDeclaredField(pattern);
      patternField.setAccessible(true);
      Field matchField = obj.getClass().getDeclaredField(match);
      matchField.setAccessible(true);
      Object patternValue = patternField.get(obj);
      Object matchValue = matchField.get(obj);

      return patternValue.equals(matchValue);
    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
      return false;
    }
  }

}
