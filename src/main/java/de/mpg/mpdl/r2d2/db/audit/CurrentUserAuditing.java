package de.mpg.mpdl.r2d2.db.audit;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;

public class CurrentUserAuditing implements AuditorAware<UserAccount> {

  @Override
  public Optional<UserAccount> getCurrentAuditor() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      return Optional.empty();
    }
    UserAccount ua = ((R2D2Principal) authentication.getPrincipal()).getUserAccount();
    return Optional.of(ua);
  }

}
