package de.mpg.mpdl.r2d2.db;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import de.mpg.mpdl.r2d2.model.ReviewToken;

public interface ReviewTokenRepository extends CrudRepository<ReviewToken, UUID> {

  public Optional<ReviewToken> findByToken(String token);

}
