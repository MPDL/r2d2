package de.mpg.mpdl.r2d2.util.testdata;

import java.util.Arrays;

import de.mpg.mpdl.r2d2.transformation.doi.model.DoiCreator;
import de.mpg.mpdl.r2d2.transformation.doi.model.DoiMetadata;
import de.mpg.mpdl.r2d2.transformation.doi.model.DoiTitle;

public class DoiMetadataBuilder {

  private final DoiMetadata doiMetadata = new DoiMetadata();

  public DoiMetadata create() {
    return this.doiMetadata;
  }

  public DoiMetadataBuilder setCreator(String creatorName, String givenName, String familyName) {
    DoiCreator doiCreator = new DoiCreator();
    doiCreator.setCreatorName(creatorName);
    doiCreator.setFamilyName(givenName);
    doiCreator.setGivenName(familyName);
    this.doiMetadata.setCreators(Arrays.asList(doiCreator));

    return this;
  }

  public DoiMetadataBuilder setTitle(String title) {
    DoiTitle doiTitle = new DoiTitle();
    doiTitle.setTitle(title);
    this.doiMetadata.setTitles(Arrays.asList(doiTitle));

    return this;
  }

  public DoiMetadataBuilder setPublisher(String publisher) {
    this.doiMetadata.setPublisher(publisher);

    return this;
  }

  public DoiMetadataBuilder setPublicationYear(int publicationYear) {
    this.doiMetadata.setPublicationYear(publicationYear);

    return this;
  }

}
