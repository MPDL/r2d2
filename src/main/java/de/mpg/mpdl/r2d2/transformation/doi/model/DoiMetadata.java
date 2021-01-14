package de.mpg.mpdl.r2d2.transformation.doi.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * DOI-Metadata representation of the Dataset-Metadata.
 */
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@XmlRootElement(name = "resource")
@XmlType(propOrder = {"identifier", "creators", "titles", "publisher", "publicationYear", "resourceType"})
public class DoiMetadata {

  //TODO: Is default publisher = 'Max Planck Society' correct?
  public static final String PUBLISHER_MPG = "Max Planck Society";

  //TODO: Add remaining attributes

  @Builder.Default
  //DOI / Identifier
  private DoiIdentifier identifier = new DoiIdentifier();

  //Titles -> title
  private List<DoiTitle> titles;

  //Creators = authors
  private List<DoiCreator> creators;

  @Builder.Default
  //Publisher
  private String publisher = PUBLISHER_MPG;

  //Publication year =? DatasetVersion.publicationDate
  private int publicationYear;

  @Builder.Default
  //Resource Type
  private DoiResourceType resourceType = new DoiResourceType();

  //RECOMMENDED

  //Description

  //Subjects = Keywords

  //OTHERS

  //RightsList = License ? (not included in DOXI)

  //Language


  public DoiIdentifier getIdentifier() {
    return identifier;
  }

  public void setIdentifier(DoiIdentifier identifier) {
    this.identifier = identifier;
  }

  public List<DoiTitle> getTitles() {
    return titles;
  }

  public void setTitles(List<DoiTitle> titles) {
    this.titles = titles;
  }

  @XmlElementWrapper(name = "creators")
  @XmlElement(name = "creator")
  public List<DoiCreator> getCreators() {
    return creators;
  }

  public void setCreators(List<DoiCreator> creators) {
    this.creators = creators;
  }

  public String getPublisher() {
    return publisher;
  }

  public void setPublisher(String publisher) {
    this.publisher = publisher;
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
