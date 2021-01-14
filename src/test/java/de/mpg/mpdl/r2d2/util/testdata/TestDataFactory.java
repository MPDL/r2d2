package de.mpg.mpdl.r2d2.util.testdata;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.Person;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.util.Utils;

import java.util.Arrays;

/**
 * Class for creating common Test-Data objects.
 *
 * Implementation of the ObjectMother/Factory Pattern.
 *
 */
public class TestDataFactory {

  //TODO: Create a Factory-Class for each model class?

  public static Dataset newDatasetWithCreationAndModificationDate() {
    Dataset dataset = new Dataset();

    dataset.setCreationDate(Utils.generateCurrentDateTimeForDatabase());
    dataset.setModificationDate(Utils.generateCurrentDateTimeForDatabase());

    return dataset;
  }

  public static DatasetVersion newDatasetVersionWithCreationAndModificationDate() {
    DatasetVersion datasetVersion = new DatasetVersion();

    datasetVersion.setCreationDate(Utils.generateCurrentDateTimeForDatabase());
    datasetVersion.setModificationDate(Utils.generateCurrentDateTimeForDatabase());
    //versionNumber, state, dataset are set per default

    return datasetVersion;
  }

  public static UserAccount newUser() {
    UserAccount userAccount = new UserAccount();

    userAccount.setCreationDate(Utils.generateCurrentDateTimeForDatabase());
    userAccount.setModificationDate(Utils.generateCurrentDateTimeForDatabase());
    //id, creator, modifier NOT defined

    userAccount.setEmail("test@email.org");
    Person person = new Person();
    person.setGivenName("usersGivenName");
    person.setFamilyName("usersFamilyName");
    //nameIdentifier, affiliations NOT defined
    userAccount.setPerson(person);
    userAccount.setRoles(Arrays.asList(UserAccount.Role.USER));

    return userAccount;
  }

}
