package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.transformation.doi.model.DoiIdentifier;

public final class DoiIdentifierBuilder {
  private String identifier;
  private String identifierType = DoiIdentifier.IDENTIFIER_TYPE_DOI;

  private DoiIdentifierBuilder() {}

  public static DoiIdentifierBuilder aDoiIdentifier() {
    return new DoiIdentifierBuilder();
  }

  public DoiIdentifierBuilder identifier(String identifier) {
    this.identifier = identifier;
    return this;
  }

  public DoiIdentifierBuilder identifierType(String identifierType) {
    this.identifierType = identifierType;
    return this;
  }

  public DoiIdentifier build() {
    DoiIdentifier doiIdentifier = new DoiIdentifier();
    doiIdentifier.setIdentifier(identifier);
    doiIdentifier.setIdentifierType(identifierType);
    return doiIdentifier;
  }
}
