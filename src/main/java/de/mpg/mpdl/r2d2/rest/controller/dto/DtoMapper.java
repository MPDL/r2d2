package de.mpg.mpdl.r2d2.rest.controller.dto;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.search.SearchResult;

@Mapper(componentModel = "spring")
public abstract class DtoMapper {



  @Mapping(source = "dataset.id", target = "id")
  @Mapping(source = "creator.id", target = "creator")
  @Mapping(source = "modifier.id", target = "modifier")
  public abstract DatasetVersionDto convertToDatasetVersionDto(DatasetVersion dv);

  @InheritInverseConfiguration
  public abstract DatasetVersion convertToDatasetVersion(DatasetVersionDto dvDto);


  @Mapping(source = "creator.id", target = "creator")
  @Mapping(source = "modifier.id", target = "modifier")
  public abstract DatasetDto convertToDatasetDto(Dataset dv);


  @InheritInverseConfiguration
  public abstract Dataset convertToDataset(DatasetDto dvDto);


  @Mapping(source = "creator.id", target = "creator")
  @Mapping(source = "modifier.id", target = "modifier")
  public abstract FileDto convertToFileDto(File dv);


  @InheritInverseConfiguration
  public abstract File convertToFile(FileDto dvDto);


  public abstract SearchResult<DatasetVersionDto> convertToSearchResultDto(SearchResult<DatasetVersion> sr);



}
