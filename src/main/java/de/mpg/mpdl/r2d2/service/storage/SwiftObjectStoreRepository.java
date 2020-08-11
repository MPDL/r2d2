package de.mpg.mpdl.r2d2.service.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.io.Payload;
import org.jclouds.io.payloads.ByteArrayPayload;
import org.jclouds.io.payloads.InputStreamPayload;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.domain.Container;
import org.jclouds.openstack.swift.v1.domain.ObjectList;
import org.jclouds.openstack.swift.v1.domain.Segment;
import org.jclouds.openstack.swift.v1.features.ContainerApi;
import org.jclouds.openstack.swift.v1.features.StaticLargeObjectApi;
import org.jclouds.openstack.swift.v1.options.ListContainerOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import de.mpg.mpdl.r2d2.SwiftStorageConfigurationProperties;
import de.mpg.mpdl.r2d2.exceptions.NotFoundException;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.FileChunk;
import de.mpg.mpdl.r2d2.model.StagingFile;

@Repository
public class SwiftObjectStoreRepository {

  private static Logger LOGGER = LoggerFactory.getLogger(SwiftObjectStoreRepository.class);

  @Autowired
  SwiftStorageConfigurationProperties swiftProperties;

  private BlobStoreContext context;

  private BlobStore store;


  private static final String CONTENT = "content";
  private static final String SEGMENTS = "segments";


  public SwiftObjectStoreRepository(BlobStoreContext context) {
    this.context = context;
    store = context.getBlobStore();
  }

  public BlobStoreContext getContext() {
    return context;
  }


  public String uploadChunk(StagingFile sf, FileChunk chunk, InputStream is) {


    LOGGER.info("Uploading Chunk to container " + sf.getId());
    boolean containerCreated = createContainer(sf.getId().toString());
    Payload payload = new InputStreamPayload(is);
    //payload.getContentMetadata().setContentLength(f.getSize());
    if (chunk.getClientEtag() != null) {
      payload.getContentMetadata().setContentMD5(HashCode.fromString(chunk.getClientEtag()));
    }

    //payload.getContentMetadata().setContentType(contentType);
    //payload.getContentMetadata().setContentDisposition(fileName);
    /*
     * TODO provide metadata
     */

    String name = SEGMENTS + "/" + String.format("%06d", chunk.getNumber());
    Map<String, String> userMetadata = new HashMap<String, String>();
    //userMetadata.put("X-Detect-Content-Type", "true");
    // @formatter:off
    Blob blob = store.blobBuilder(name).payload(payload).userMetadata(userMetadata).build();
    // @formatter:on
    String eTag = store.putBlob(sf.getId().toString(), blob);
    LOGGER.info("Cloud server returned etag " + eTag);
    return eTag;
  }


  public String uploadFile(StagingFile file, InputStream is) {


    LOGGER.info("Uploading single file to container " + file.getId());
    Payload payload = new InputStreamPayload(is);
    //payload.getContentMetadata().setContentLength(f.getSize());
    if (file.getChecksum() != null) {
      payload.getContentMetadata().setContentMD5(HashCode.fromString(file.getChecksum()));
    }

    //payload.getContentMetadata().setContentType(contentType);
    //payload.getContentMetadata().setContentDisposition(fileName);
    /*
     * TODO provide metadata
     */

    Map<String, String> userMetadata = new HashMap<String, String>();
    //userMetadata.put("X-Detect-Content-Type", "true");
    // @formatter:off
    Blob blob = store.blobBuilder(CONTENT).payload(payload).userMetadata(userMetadata).build();
    // @formatter:on
    String eTag = store.putBlob(file.getId().toString(), blob);
    LOGGER.info("Cloud server returned etag " + eTag);
    return eTag;
  }



  public String uploadFile(String container, byte[] bytes, String name, String fileName, String contentType) {

    HashCode md5 = Hashing.md5().hashBytes(bytes);

    Payload payload = new ByteArrayPayload(bytes);
    payload.getContentMetadata().setContentLength((long) bytes.length);
    payload.getContentMetadata().setContentMD5(md5);
    payload.getContentMetadata().setContentType(contentType);
    payload.getContentMetadata().setContentDisposition(fileName);
    /*
     * TODO provide metadata
     */
    Map<String, String> userMetadata = new HashMap<String, String>();
    // @formatter:off
    Blob blob = store.blobBuilder(name).payload(payload).userMetadata(userMetadata).build();
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

  public List<Container> listAllContainers() {
    PageSet<? extends StorageMetadata> set = store.list();
    SwiftApi api = context.unwrapApi(SwiftApi.class);
    ContainerApi capi = api.getContainerApi("region1");
    List<Container> list = capi.list().toList();
    return list;
    // return set.stream().map(smd -> smd.getName()).collect(Collectors.toList());

  }

  public List<Object> listContainer(String container) throws NotFoundException {
    if (!isContainerExist(container)) {
      throw new NotFoundException(String.format("Container with id %s does not exist.", container));
    }
    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.setSerializationInclusion(Include.NON_NULL);
    mapper.setSerializationInclusion(Include.NON_EMPTY);
    PageSet<? extends StorageMetadata> set =
        store.list(container, org.jclouds.blobstore.options.ListContainerOptions.Builder.recursive().withDetails());
    return set.stream().map(smd -> mapper.valueToTree(smd)).collect(Collectors.toList());
  }

  public boolean deleteContainer(String container) throws NotFoundException {
    if (!isContainerExist(container)) {
      throw new NotFoundException(String.format("Container with id %s does not exist.", container));
    }
    boolean isContainerGone = false;
    store.deleteContainer(container);
    if (!isContainerExist(container)) {
      isContainerGone = true;
    }
    return isContainerGone;
  }

  public Blob getFile(String container, String name) {
    return store.getBlob(container, name);
  }

  public String getPublicURI(String container) {
    return store.blobMetadata(container, CONTENT).getPublicUri().toString();
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

  public String createManifest(String segmentContainer, String segmentPath, String manifestContainer, String contentType) {
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

    Map<String, String> metadata = ImmutableMap.of("parts_in", segmentContainer + "/" + segmentPath);
    Map<String, String> headers = ImmutableMap.of("Content-Type", contentType);
    return slo.replaceManifest("content", parts, metadata, headers);
  }


  public String createManifest(StagingFile sf) {
    String contentType = "application/octet-stream";
    if (sf.getFormat() != null) {
      contentType = sf.getFormat();
    }

    return createManifest(sf.getId().toString(), SEGMENTS, sf.getId().toString(), contentType);

  }
}
