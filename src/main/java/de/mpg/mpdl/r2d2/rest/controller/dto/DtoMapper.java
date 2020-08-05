package de.mpg.mpdl.r2d2.rest.controller.dto;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.r2d2.model.DatasetVersion;

@Component
public class DtoMapper {


  @Autowired
  private ModelMapper modelMapper;


  public DatasetVersionDto convertToDatasetVersionDto(DatasetVersion dv) {
    DatasetVersionDto dvDto = modelMapper.map(dv, DatasetVersionDto.class);
    return dvDto;
  }

  public DatasetVersion convertToDatasetVersion(DatasetVersionDto dvDto) {
    DatasetVersion dv = modelMapper.map(dvDto, DatasetVersion.class);
    return dv;
  }


}
