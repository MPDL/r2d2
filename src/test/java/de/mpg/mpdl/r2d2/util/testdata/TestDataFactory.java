package de.mpg.mpdl.r2d2.util.testdata;

import de.mpg.mpdl.r2d2.model.Person;
import de.mpg.mpdl.r2d2.model.aa.Grant;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.util.Utils;
import de.mpg.mpdl.r2d2.util.testdata.builder.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class for creating common Test-Data objects.
 * <p>
 * Implementation of the ObjectMother/Factory Pattern.
 * </p>
 */
public class TestDataFactory {

  //TODO: Create a Factory-Class for each model class?

  /**
   * Creates a default Dataset. Predefined attributes are:
   * <ul>
   * <li>creationDate</li>
   * <li>modificationDate</li>
   * </ul>
   * 
   * @return a DatasetBuilder
   */
  public static DatasetBuilder aDataset() {
    DatasetBuilder datasetBuilder = DatasetBuilder.aDataset().creationDate(Utils.generateCurrentDateTimeForDatabase())
        .modificationDate(Utils.generateCurrentDateTimeForDatabase());

    return datasetBuilder;
  }

  /**
   * Creates a default DatasetVersion. Predefined attributes are:
   * <ul>
   * <li>creationDate</li>
   * <li>modificationDate</li>
   * </ul>
   *
   * @return a DatasetVersionBuilder
   */
  public static DatasetVersionBuilder aDatasetVersion() {
    DatasetVersionBuilder datasetVersionBuilder = DatasetVersionBuilder.aDatasetVersion()
        .creationDate(Utils.generateCurrentDateTimeForDatabase()).modificationDate(Utils.generateCurrentDateTimeForDatabase());

    return datasetVersionBuilder;
  }

  /**
   * Creates a default UserAccount. Predefined attributes are:
   * <ul>
   * <li>creationDate</li>
   * <li>modificationDate</li>
   * <li>email</li>
   * <li>person</li>
   * <ul>
   * <li>givenName</li>
   * <li>familyName</li>
   * </ul>
   * <li>grants</li>
   * <ul>
   * <li>grant</li>
   * <ul>
   * <li>role = USER</li>
   * </ul>
   * </ul>
   * </ul>
   *
   * @return a UserAccountBuilder
   */
  public static UserAccountBuilder anUser() {
    Person person = PersonBuilder.aPerson().givenName("userGivenName").familyName("userFamilyName")
        //nameIdentifier, affiliations NOT defined
        .build();

    Grant grant = GrantBuilder.aGrant().role(UserAccount.Role.USER).build();

    UserAccountBuilder userAccountBuilder = UserAccountBuilder.anUserAccount().creationDate(Utils.generateCurrentDateTimeForDatabase())
        .modificationDate(Utils.generateCurrentDateTimeForDatabase())
        //id, creator, modifier NOT defined
        .email("user@email.org").person(person).grants(Collections.singletonList(grant));

    return userAccountBuilder;
  }

  /**
   * Creates a default File. Predefined attributes are:
   * <ul>
   * <li>creationDate</li>
   * <li>modificationDate</li>
   * </ul>
   *
   * @return a FileBuilder
   */
  public static FileBuilder aFile() {
    FileBuilder fileBuilder = FileBuilder.aFile().creationDate(Utils.generateCurrentDateTimeForDatabase())
        .modificationDate(Utils.generateCurrentDateTimeForDatabase());

    return fileBuilder;
  }

  /**
   * Creates a default R2D2Principal. Predefined attributes are:
   * <ul>
   * <li>username</li>
   * <li>password</li>
   * <li>authorities (empty)</li>
   * </ul>
   *
   * @return a R2D2PrincipalBuilder
   */
  public static R2D2PrincipalBuilder aR2D2Principal() {
    R2D2PrincipalBuilder r2D2PrincipalBuilder =
        R2D2PrincipalBuilder.aR2D2Principal("username", "password", new ArrayList<GrantedAuthority>());

    return r2D2PrincipalBuilder;
  }

}
