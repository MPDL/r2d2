package de.mpg.mpdl.r2d2.transformation.doi.model;

import javax.xml.bind.annotation.*;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class DoiIdentifier {

  public static final String IDENTIFIER_TYPE_DOI = "DOI";

  @XmlValue
  private String identifier;

  @XmlAttribute
  private String identifierType = IDENTIFIER_TYPE_DOI;

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public String getIdentifierType() {
    return identifierType;
  }

  public void setIdentifierType(String identifierType) {
    this.identifierType = identifierType;
  }
}
