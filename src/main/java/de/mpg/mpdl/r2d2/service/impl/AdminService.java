package de.mpg.mpdl.r2d2.service.impl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminService {

  public String test() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication.getName() + authentication.getAuthorities();
  }

}
