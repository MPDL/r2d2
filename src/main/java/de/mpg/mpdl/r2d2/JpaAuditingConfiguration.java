package de.mpg.mpdl.r2d2;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import de.mpg.mpdl.r2d2.db.audit.CurrentUserAuditing;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "offsetDateTimeProvider")
public class JpaAuditingConfiguration {

  public @Bean DateTimeProvider offsetDateTimeProvider() {
    return () -> Optional.of(OffsetDateTime.now().truncatedTo(ChronoUnit.MICROS));
  }

  public @Bean AuditorAware<UserAccount> auditorProvider() {
    return new CurrentUserAuditing();
  }
}
