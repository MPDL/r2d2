package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.transformation.doi.model.*;

import java.util.List;

public final class DoiMetadataBuilder {
  //DOI / Identifier
  private DoiIdentifier identifier = new DoiIdentifier();
  //Titles -> title
  private List<DoiTitle> titles;
  //Creators = authors
  private List<DoiCreator> creators;
  //Publisher
  private String publisher = DoiMetadata.PUBLISHER_MPG;
  //Publication year =? DatasetVersion.publicationDate
  private int publicationYear;
  //Resource Type
  private DoiResourceType resourceType = new DoiResourceType();

  private DoiMetadataBuilder() {}

  public static DoiMetadataBuilder aDoiMetadata() {
    return new DoiMetadataBuilder();
  }

  public DoiMetadataBuilder identifier(DoiIdentifier identifier) {
    this.identifier = identifier;
    return this;
  }

  public DoiMetadataBuilder titles(List<DoiTitle> titles) {
    this.titles = titles;
    return this;
  }

  public DoiMetadataBuilder creators(List<DoiCreator> creators) {
    this.creators = creators;
    return this;
  }

  public DoiMetadataBuilder publisher(String publisher) {
    this.publisher = publisher;
    return this;
  }

  public DoiMetadataBuilder publicationYear(int publicationYear) {
    this.publicationYear = publicationYear;
    return this;
  }

  public DoiMetadataBuilder resourceType(DoiResourceType resourceType) {
    this.resourceType = resourceType;
    return this;
  }

  public DoiMetadata build() {
    DoiMetadata doiMetadata = new DoiMetadata();
    doiMetadata.setIdentifier(identifier);
    doiMetadata.setTitles(titles);
    doiMetadata.setCreators(creators);
    doiMetadata.setPublisher(publisher);
    doiMetadata.setPublicationYear(publicationYear);
    doiMetadata.setResourceType(resourceType);
    return doiMetadata;
  }
}
