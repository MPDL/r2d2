package de.mpg.mpdl.r2d2.transformation.doi.model;

import java.util.List;

/**
 * DOI-Metadata representation of the Dataset-Metadata.
 */
public class DoiMetadata {

  //TODO: Add remaining attributes

  //DOI / Identifier

  //Titles = title
  private String title;

  //Creators = authors
  private List<DoiCreator> creators;

  //Publisher ?

  //Publication year =? DatasetVersion.publicationDate
  private int publicationYear;

  //Resource Type
  private DoiResourceType resourceType = new DoiResourceType();

  //RECOMMENDED

  //Description

  //Subjects = Keywords

  //OTHERS

  //RightsList = License ? (not included in DOXI)

  //Language

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<DoiCreator> getCreators() {
    return creators;
  }

  public void setCreators(List<DoiCreator> creators) {
    this.creators = creators;
  }

  public int getPublicationYear() {
    return publicationYear;
  }

  public void setPublicationYear(int publicationYear) {
    this.publicationYear = publicationYear;
  }

  public DoiResourceType getResourceType() {
    return resourceType;
  }

  public void setResourceType(DoiResourceType resourceType) {
    this.resourceType = resourceType;
  }
}
