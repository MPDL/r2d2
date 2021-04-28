package de.mpg.mpdl.r2d2.util.testdata;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.Person;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.util.Utils;
import de.mpg.mpdl.r2d2.util.testdata.builder.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Class for creating common Test-Data objects.
 *
 * Implementation of the ObjectMother/Factory Pattern.
 *
 */
public class TestDataFactory {

  //TODO: Create a Factory-Class for each model class?

  public static DatasetBuilder aDatasetWithCreationAndModificationDate() {
    DatasetBuilder datasetBuilder = DatasetBuilder.aDataset().creationDate(Utils.generateCurrentDateTimeForDatabase())
        .modificationDate(Utils.generateCurrentDateTimeForDatabase());

    return datasetBuilder;
  }

  public static DatasetVersionBuilder aDatasetVersionWithCreationAndModificationDate() {
    DatasetVersionBuilder datasetVersionBuilder = DatasetVersionBuilder.aDatasetVersion()
        .creationDate(Utils.generateCurrentDateTimeForDatabase()).modificationDate(Utils.generateCurrentDateTimeForDatabase());

    return datasetVersionBuilder;
  }

  public static UserAccountBuilder anUser() {
    Person person1 = PersonBuilder.aPerson().givenName("usersGivenName").familyName("usersFamilyName")
        //nameIdentifier, affiliations NOT defined
        .build();

    UserAccountBuilder userAccountBuilder = UserAccountBuilder.anUserAccount().creationDate(Utils.generateCurrentDateTimeForDatabase())
        .modificationDate(Utils.generateCurrentDateTimeForDatabase())
        //id, creator, modifier NOT defined
        .email("test@email.org").person(person1)
        .grants(Collections.singletonList(GrantBuilder.aGrant().role(UserAccount.Role.USER).build()));

    return userAccountBuilder;
  }

  public static FileBuilder aFileWithDates() {
    FileBuilder fileBuilder = FileBuilder.aFile().creationDate(Utils.generateCurrentDateTimeForDatabase())
        .modificationDate(Utils.generateCurrentDateTimeForDatabase());

    return fileBuilder;
  }

  public static R2D2PrincipalBuilder aR2D2Principal() {
    R2D2PrincipalBuilder r2D2PrincipalBuilder =
        R2D2PrincipalBuilder.aR2D2Principal("username", "password", new ArrayList<GrantedAuthority>());

    return r2D2PrincipalBuilder;
  }

}
