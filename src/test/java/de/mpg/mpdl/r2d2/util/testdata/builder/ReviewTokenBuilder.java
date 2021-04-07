package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.model.ReviewToken;

import java.util.UUID;

public final class ReviewTokenBuilder {
  private String token;
  private UUID dataset;

  private ReviewTokenBuilder() {}

  public static ReviewTokenBuilder aReviewToken() {
    return new ReviewTokenBuilder();
  }

  public ReviewTokenBuilder token(String token) {
    this.token = token;
    return this;
  }

  public ReviewTokenBuilder dataset(UUID dataset) {
    this.dataset = dataset;
    return this;
  }

  public ReviewToken build() {
    ReviewToken reviewToken = new ReviewToken();
    reviewToken.setToken(token);
    reviewToken.setDataset(dataset);
    return reviewToken;
  }
}
