package de.mpg.mpdl.r2d2.rest.controller.dto;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.File.UploadState;
import de.mpg.mpdl.r2d2.model.VersionId;
import de.mpg.mpdl.r2d2.search.model.FileIto;
import net.schmizz.sshj.xfer.FilePermission;


@JsonIgnoreProperties("internal")
public class FileDto extends FileIto {



}
