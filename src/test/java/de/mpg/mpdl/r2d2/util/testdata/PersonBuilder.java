package de.mpg.mpdl.r2d2.util.testdata;

import de.mpg.mpdl.r2d2.model.Person;

public class PersonBuilder {

  private final Person person = new Person();

  public Person create() {
    return this.person;
  }

  public PersonBuilder setName(String givenName, String familyName) {
    this.person.setGivenName(givenName);
    this.person.setFamilyName(familyName);

    return this;
  }

}
