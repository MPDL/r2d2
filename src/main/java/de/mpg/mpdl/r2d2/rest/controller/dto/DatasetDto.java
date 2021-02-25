package de.mpg.mpdl.r2d2.rest.controller.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.search.model.DatasetIto;

@JsonIgnoreProperties("internal")
public class DatasetDto extends DatasetIto {



}
