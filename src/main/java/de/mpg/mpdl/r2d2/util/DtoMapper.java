package de.mpg.mpdl.r2d2.util;

import java.util.List;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.VersionId;
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


  @Mapping(source = "dataset.id", target = "id")
  @Mapping(source = "creator.id", target = "creator")
  @Mapping(source = "modifier.id", target = "modifier")
  @Mapping(source = ".", target = "internal")
  public abstract DatasetVersionIto convertToDatasetVersionIto(DatasetVersion dv);

  @InheritInverseConfiguration(name = "convertToDatasetVersionIto")
  public abstract DatasetVersion convertToDatasetVersion(DatasetVersionIto dv);

  @InheritInverseConfiguration(name = "convertToDatasetVersionDto")
  public abstract DatasetVersion convertToDatasetVersion(DatasetVersionDto dv);

  public abstract DatasetVersionDto convertToDatasetVersionDto(DatasetVersionIto dv);

  @Mapping(source = "creator.id", target = "creator")
  @Mapping(source = "modifier.id", target = "modifier")
  public abstract DatasetDto convertToDatasetDto(Dataset dv);


  @InheritInverseConfiguration
  public abstract Dataset convertToDataset(DatasetDto dvDto);


  @Mapping(source = "creator.id", target = "creator")
  @Mapping(source = "modifier.id", target = "modifier")
  @Mapping(source = "datasets", target = "datasets")
  public abstract FileDto convertToFileDto(File f);

  @Mapping(source = "creator.id", target = "creator")
  @Mapping(source = "modifier.id", target = "modifier")
  @Mapping(source = "datasets", target = "datasets")
  @Mapping(target = "internal",
      expression = "java(f.getDatasets().iterator().hasNext() ? f.getDatasets().iterator().next().getDataset() : null)")
  public abstract FileIto convertToFileIto(File f);


  @InheritInverseConfiguration(name = "convertToFileIto")
  public abstract File convertToFile(FileIto fDto);

  @InheritInverseConfiguration(name = "convertToFileDto")
  public abstract File convertToFile(FileDto fDto);

  public abstract FileDto convertToFileDto(FileIto dv);

  public abstract List<FileDto> convertToFileDtoList(List<File> dv);


  public abstract VersionId toId(DatasetVersion version);


  public abstract SearchResult<FileDto> convertToFileSearchResultDto(SearchResult<FileIto> sr);

  public abstract SearchResult<DatasetVersionDto> convertToSearchResultDto(SearchResult<DatasetVersionIto> sr);



}
