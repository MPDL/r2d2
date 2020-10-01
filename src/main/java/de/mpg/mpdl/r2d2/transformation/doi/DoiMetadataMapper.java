package de.mpg.mpdl.r2d2.transformation.doi;

import java.util.Arrays;
import java.util.List;

import org.mapstruct.*;

import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.Person;
import de.mpg.mpdl.r2d2.transformation.doi.model.DoiCreator;
import de.mpg.mpdl.r2d2.transformation.doi.model.DoiMetadata;
import de.mpg.mpdl.r2d2.transformation.doi.model.DoiTitle;

/**
 * Mapper: {@link DatasetVersion} (Metadata) to {@link DoiMetadata}.
 */
@Mapper(componentModel = "spring")
public abstract class DoiMetadataMapper {

  @Mapping(source = "metadata.title", target = "titles", qualifiedByName = "tileToDoiTitleList")
  @Mapping(source = "metadata.authors", target = "creators")
  @Mapping(source = "publicationDate.year", target = "publicationYear")
  //identifierType, publisher and resourceTypeGeneral are set with default values in DoiMetadata
  public abstract DoiMetadata convertToDoiMetadata(DatasetVersion datasetVersion);

  @Named("tileToDoiTitleList")
  protected List<DoiTitle> tileToDoiTitleList(String title) {
    if (title == null) {
      return null;
    }

    DoiTitle doiTitle = new DoiTitle();
    doiTitle.setTitle(title);

    return Arrays.asList(doiTitle);
  }

  // Map creator.creatorName = author.familyName + ", " + author.givenName
  // multiple sources (in a qualifiedByName-method ) is not supported by mapstruct => Use @AfterMapping
  @AfterMapping
  protected void setCreatorName(Person person, @MappingTarget DoiCreator creator) {
    //TODO: Handle familyName or givenName are empty correctly! Can the names be empty?
    String creatorName = person.getFamilyName() + ", " + person.getGivenName();
    creator.setCreatorName(creatorName);
  }

}
