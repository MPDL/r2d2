package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.transformation.doi.model.DoiCreator;

public final class DoiCreatorBuilder {
  private String creatorName;
  private String givenName;
  private String familyName;

  private DoiCreatorBuilder() {}

  public static DoiCreatorBuilder aDoiCreator() {
    return new DoiCreatorBuilder();
  }

  public DoiCreatorBuilder creatorName(String creatorName) {
    this.creatorName = creatorName;
    return this;
  }

  public DoiCreatorBuilder givenName(String givenName) {
    this.givenName = givenName;
    return this;
  }

  public DoiCreatorBuilder familyName(String familyName) {
    this.familyName = familyName;
    return this;
  }

  public DoiCreator build() {
    DoiCreator doiCreator = new DoiCreator();
    doiCreator.setCreatorName(creatorName);
    doiCreator.setGivenName(givenName);
    doiCreator.setFamilyName(familyName);
    return doiCreator;
  }
}
