package de.mpg.mpdl.r2d2.transformation.doi;

import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.transformation.doi.model.DoiMetadata;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper: {@link DatasetVersion} (Metadata) to {@link DoiMetadata}.
 */
@Mapper(componentModel = "spring")
public abstract class DoiMetadataMapper {

  @Mapping(source = "metadata.title", target = "title")
  @Mapping(source = "metadata.authors", target = "creators")
  @Mapping(source = "publicationDate.year", target = "publicationYear")
  //resourceTypeGeneral is set to 'Dataset' by default in DoiMetadata
  public abstract DoiMetadata convertToDoiMetadata(DatasetVersion datasetVersion);

}
