package de.mpg.mpdl.r2d2.util.testdata;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.util.Utils;

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

}
