package de.mpg.mpdl.r2d2.service.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.io.Payload;
import org.jclouds.io.payloads.ByteArrayPayload;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.domain.ObjectList;
import org.jclouds.openstack.swift.v1.domain.Segment;
import org.jclouds.openstack.swift.v1.features.StaticLargeObjectApi;
import org.jclouds.openstack.swift.v1.options.ListContainerOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.google.common.collect.ImmutableMap;

import de.mpg.mpdl.r2d2.SwiftStorageConfigurationProperties;

@Repository
public class FileStorageService {

  private static Logger logger = LoggerFactory.getLogger(FileStorageService.class);

  @Autowired
  SwiftStorageConfigurationProperties swiftProperties;

  private BlobStoreContext context;

  private BlobStore store;

  public BlobStoreContext getContext() {
    return context;
  }

  public void setContext(BlobStoreContext context) {
    this.context = context;
    this.store = getContext().getBlobStore();
  }

  public String uploadFile(String container, byte[] bytes, String name, String contentType) {

    Payload payload = new ByteArrayPayload(bytes);
    /*
     * TODO provide metadata
     */
    Map<String, String> userMetadata = new HashMap<String, String>();
    // @formatter:off
    Blob blob = store.blobBuilder(name).payload(payload).contentType(contentType).userMetadata(userMetadata).build();
    // @formatter:on
    String eTag = store.putBlob(container, blob);
    return eTag;
  }

  public InputStream downloadFile(String container, String name) {

    InputStream inputStream = null;
    Blob blob = store.getBlob(container, name);
    try {
      inputStream = blob.getPayload().openStream();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return inputStream;
  }

  public boolean deleteFile(String container, String name) {

    boolean isFileRemoved = false;
    store.removeBlob(container, name);
    if (!isFileExist(container, name)) {
      isFileRemoved = true;
    }
    return isFileRemoved;
  }

  public boolean isFileExist(String container, String name) {

    boolean isExist = false;
    isExist = store.blobExists(container, name);
    return isExist;
  }

  public boolean isContainerExist(String container) {

    boolean isExist = false;
    isExist = store.containerExists(container);
    return isExist;
  }

  public boolean createContainer(String name) {

    boolean success = false;
    if (!isContainerExist(name)) {
      success = store.createContainerInLocation(null, name);
    }
    return success;
  }

  public void creatreManifest(String segmentContainer, String segmentPath, String manifestContainer, String manifest) {
    SwiftApi swiftApi = getContext().unwrapApi(SwiftApi.class);
    StaticLargeObjectApi slo = swiftApi.getStaticLargeObjectApi(swiftProperties.getRegion(), manifestContainer);
    List<Segment> parts = new ArrayList<>();
    ObjectList list =
        swiftApi.getObjectApi(swiftProperties.getRegion(), segmentContainer).list(ListContainerOptions.Builder.path(segmentPath));
    list.forEach(so -> {
      long size = so.getPayload().getContentMetadata().getContentLength();
      Segment s = Segment.builder().path(segmentContainer + "/" + so.getName()).etag(so.getETag()).sizeBytes(size).build();
      parts.add(s);
    });
    slo.replaceManifest(manifest, parts, ImmutableMap.of("parts_in", segmentContainer + "/" + segmentPath));
  }
}
