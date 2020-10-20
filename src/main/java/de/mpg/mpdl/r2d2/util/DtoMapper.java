package de.mpg.mpdl.r2d2.util;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.rest.controller.dto.DatasetDto;
import de.mpg.mpdl.r2d2.rest.controller.dto.DatasetVersionDto;
import de.mpg.mpdl.r2d2.rest.controller.dto.FileDto;
import de.mpg.mpdl.r2d2.search.model.DatasetVersionIto;
import de.mpg.mpdl.r2d2.search.model.FileIto;
import de.mpg.mpdl.r2d2.search.model.SearchResult;

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


  public abstract DatasetVersionIto convertToDatasetVersionIto(DatasetVersion dv);

  @InheritInverseConfiguration
  public abstract DatasetVersion convertToDatasetVersion(DatasetVersionIto dv);


  public abstract FileIto convertToFileIto(File f);

  @InheritInverseConfiguration
  public abstract File convertToFile(FileIto fi);



  public abstract SearchResult<FileDto> convertToFileSearchResultDto(SearchResult<FileIto> sr);

  public abstract SearchResult<DatasetVersionDto> convertToSearchResultDto(SearchResult<DatasetVersionIto> sr);



}
