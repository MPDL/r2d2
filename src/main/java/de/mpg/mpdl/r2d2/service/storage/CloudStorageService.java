package de.mpg.mpdl.r2d2.service.storage;

import java.io.IOException;
import java.util.List;

import org.apache.commons.fileupload.FileItemStream;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.domain.Location;
import org.jclouds.io.payloads.InputStreamPayload;
import org.jclouds.openstack.swift.v1.SwiftApiMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.google.common.collect.Iterables;
import de.mpg.mpdl.r2d2.db.FileRepository;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.File;

@Service
public class CloudStorageService implements StorageService {

  BlobStoreContext blobStoreContext;

  FileRepository fileRepository;

  @Autowired
  public CloudStorageService(BlobStoreContext bsc, FileRepository repo) {
    this.blobStoreContext = bsc;
    this.fileRepository = repo;
  }

  @Override
  public File store(String containerId, FileItemStream item) throws R2d2TechnicalException, IOException {
    BlobStore blobStore = blobStoreContext.getBlobStore();
    ApiMetadata apiMetadata = blobStoreContext.unwrap().getProviderMetadata().getApiMetadata();
    Location location = null;
    if (apiMetadata instanceof SwiftApiMetadata) {
      location = Iterables.getFirst(blobStore.listAssignableLocations(), null);
    }
    File file = new File();
    file.setFilename(item.getName());
    file.setFormat(item.getContentType());
    file = fileRepository.save(file);

    InputStreamPayload streamPayload = new InputStreamPayload(item.openStream());
    Blob blob = null;
    try (streamPayload) {
      blob = blobStore.blobBuilder(file.getId().toString()).payload(streamPayload)
          .contentDisposition("attachment; filename=" + item.getName()).contentType(item.getContentType()).build();
      if (!blobStore.containerExists(containerId)) {
        blobStore.createContainerInLocation(location, containerId);
      }
      String eTag = blobStore.putBlob(containerId, blob);
      Blob uploaded = blobStore.getBlob(containerId, file.getId().toString());
      file.setChecksum(eTag);
      file.setStorageLocation(uploaded.getMetadata().getPublicUri().toString());
      file.setSize(uploaded.getMetadata().getSize());
      file = fileRepository.save(file);
      return file;
    }

  }

  @Override
  public Resource get(String containerId, String fileId) throws R2d2TechnicalException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<File> list(String containerId) throws R2d2TechnicalException {
    // TODO Auto-generated method stub
    return null;
  }

}
