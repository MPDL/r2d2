package de.mpg.mpdl.r2d2.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.mpg.mpdl.r2d2.model.Affiliation;
import de.mpg.mpdl.r2d2.model.Person;
import de.mpg.mpdl.r2d2.model.aa.LocalUserAccount;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.registration.RegistrationRequest;
import de.mpg.mpdl.r2d2.util.BaseIntegrationTest;
import de.mpg.mpdl.r2d2.util.testdata.TestDataManager;
import de.mpg.mpdl.r2d2.util.testdata.builder.AffiliationBuilder;
import de.mpg.mpdl.r2d2.util.testdata.builder.LocalUserAccountBuilder;
import de.mpg.mpdl.r2d2.util.testdata.builder.PersonBuilder;
import de.mpg.mpdl.r2d2.util.testdata.builder.UserAccountBuilder;

class UserServiceImplIT extends BaseIntegrationTest {

  @Autowired
  UserServiceImpl userServiceImpl;

  @Autowired
  TestDataManager testDataManager;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Test
  void testRegisterNewUser() {
    //Given
    String firstName = "FirstName";
    String lastName = "LastName";
    String email = "Email";
    String password = "Password";
    String gridId = "GridId";
    String organization = "Organization";
    String department = "Department";
    List<Affiliation> affiliations =
        List.of(AffiliationBuilder.anAffiliation().id(gridId).organization(organization).department(department).build());
    String orcid = "ORCID";

    RegistrationRequest registrationRequest = new RegistrationRequest();
    registrationRequest.setFirst(firstName);
    registrationRequest.setLast(lastName);
    registrationRequest.setEmail(email);
    registrationRequest.setPass(password);
    registrationRequest.setAffiliations(affiliations);
    registrationRequest.setOrcid(orcid);

    //When
    LocalUserAccount returnedLocalUserAccount = userServiceImpl.registerNewUser(registrationRequest);

    //Then
    List<LocalUserAccount> localUserAccountsFromDB = testDataManager.findAll(LocalUserAccount.class);

    Person expectedPerson =
        PersonBuilder.aPerson().givenName(firstName).familyName(lastName).affiliations(affiliations).orcid(orcid).build();
    UserAccount expectedUserAccount = UserAccountBuilder.anUserAccount().email(email).person(expectedPerson).grants(null).build();
    LocalUserAccount expectedLocalUserAccount =
        LocalUserAccountBuilder.aLocalUserAccount().username(email).user(expectedUserAccount).build();

    assertThat(localUserAccountsFromDB).hasSize(1).first().usingRecursiveComparison().isEqualTo(returnedLocalUserAccount)
        .ignoringExpectedNullFields().isEqualTo(expectedLocalUserAccount);
    //Check that the password was encrypted by the R2D2Application passwordEncoder and that it matches the entered password.
    assertThat(passwordEncoder.matches(password, localUserAccountsFromDB.get(0).getPassword())).isTrue();
  }

}
