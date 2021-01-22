package de.mpg.mpdl.r2d2.util.testdata.builder;

import de.mpg.mpdl.r2d2.model.Affiliation;
import de.mpg.mpdl.r2d2.model.Person;

import java.util.ArrayList;
import java.util.List;

public final class PersonBuilder {
  private String givenName;
  private String familyName;
  // ORCID (The id part of the ORCID-URI)
  private String orcid;
  // Affiliation contains department/group
  private List<Affiliation> affiliations = new ArrayList<>();

  private PersonBuilder() {}

  public static PersonBuilder aPerson() {
    return new PersonBuilder();
  }

  public PersonBuilder givenName(String givenName) {
    this.givenName = givenName;
    return this;
  }

  public PersonBuilder familyName(String familyName) {
    this.familyName = familyName;
    return this;
  }

  public PersonBuilder orcid(String orcid) {
    this.orcid = orcid;
    return this;
  }

  public PersonBuilder affiliations(List<Affiliation> affiliations) {
    this.affiliations = affiliations;
    return this;
  }

  public Person build() {
    Person person = new Person();
    person.setGivenName(givenName);
    person.setFamilyName(familyName);
    person.setOrcid(orcid);
    person.setAffiliations(affiliations);
    return person;
  }
}
