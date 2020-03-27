package de.mpg.mpdl.r2d2.util;

import java.security.Principal;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;

public class Utils {

  public static R2D2Principal toCustomPrincipal(Principal p) {
    if (p != null) {
      return (R2D2Principal) ((UsernamePasswordAuthenticationToken) p).getPrincipal();
    }

    return null;
  }

}
