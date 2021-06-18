package de.mpg.mpdl.r2d2.service.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.io.Payload;
import org.jclouds.io.payloads.ByteArrayPayload;
import org.jclouds.io.payloads.InputStreamPayload;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.domain.Container;
import org.jclouds.openstack.swift.v1.domain.ObjectList;
import org.jclouds.openstack.swift.v1.domain.Segment;
import org.jclouds.openstack.swift.v1.features.ContainerApi;
import org.jclouds.openstack.swift.v1.features.StaticLargeObjectApi;
import org.jclouds.s3.S3Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import de.mpg.mpdl.r2d2.S3ObjectStoreConfigurationProperties;
import de.mpg.mpdl.r2d2.SwiftStorageConfigurationProperties;
import de.mpg.mpdl.r2d2.exceptions.NotFoundException;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.FileChunk;
import de.mpg.mpdl.r2d2.model.File;

@Repository
@ConditionalOnProperty(value = "r2d2.storage", havingValue = "s3jclouds")
public class S3ObjectStoreJCloudsRepository implements ObjectStoreRepository {

  private static Logger LOGGER = LoggerFactory.getLogger(S3ObjectStoreJCloudsRepository.class);

  @Autowired
  S3ObjectStoreConfigurationProperties s3Properties;

  private BlobStoreContext context;

  private BlobStore store;

  private S3Client client;


  private static final String CONTENT = "/content";
  private static final String SEGMENTS = "/segments";


  public S3ObjectStoreJCloudsRepository(BlobStoreContext context) {
    this.context = context;
    store = context.getBlobStore();
    client = context.unwrapApi(S3Client.class);
  }

  public BlobStoreContext getContext() {
    return context;
  }


  public String uploadChunk(File sf, FileChunk chunk, InputStream is) {


    LOGGER.info("Uploading Chunk to container " + sf.getId());
    Payload payload = new InputStreamPayload(is);
    if (chunk.getClientEtag() != null) {
      payload.getContentMetadata().setContentMD5(HashCode.fromString(chunk.getClientEtag()));
    }

    String name = sf.getId().toString() + SEGMENTS + "/" + String.format("%06d", chunk.getNumber());
    Map<String, String> userMetadata = new HashMap<String, String>();
    Blob blob = store.blobBuilder(name).payload(payload).userMetadata(userMetadata).build();
    String eTag = store.putBlob(s3Properties.getBucket(), blob);
    LOGGER.info("S3 store returned etag " + eTag);
    return eTag;
  }


  public String uploadFile(File file, InputStream is) {


    LOGGER.info("Uploading single file to container " + file.getId());
    Payload payload = new InputStreamPayload(is);
    if (file.getChecksum() != null) {
      payload.getContentMetadata().setContentMD5(HashCode.fromString(file.getChecksum()));
    }
    payload.getContentMetadata().setContentLength(file.getSize());
    payload.getContentMetadata().setContentType(file.getFormat());
    payload.getContentMetadata().setContentDisposition(file.getFilename());
    /*
     * TODO provide metadata
     */

    Map<String, String> userMetadata = new HashMap<String, String>();
    Blob blob = store.blobBuilder(file.getId().toString() + CONTENT).payload(payload).userMetadata(userMetadata).build();
    String eTag = store.putBlob(s3Properties.getBucket(), blob);
    LOGGER.info("S3 store returned etag " + eTag);
    return eTag;
  }

  public InputStream downloadFile(String container, String name) {

    InputStream inputStream = null;
    String file2downlod = container + "/" + name;
    Blob blob = store.getBlob(s3Properties.getBucket(), file2downlod);
    try {
      inputStream = blob.getPayload().openStream();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return inputStream;
  }

  public List<Container> listAllContainers() {

    return Collections.emptyList();
  }

  public List<Object> listContainer(String container) throws NotFoundException {

    return Collections.emptyList();
  }

  public boolean deleteContainer(String container) throws NotFoundException {

    return false;
  }

  public String getPublicURI(String container) {
    return store.blobMetadata(s3Properties.getBucket(), container + CONTENT).getUri().toString();
  }

  public Long getFileSize(String container) {
    return store.blobMetadata(s3Properties.getBucket(), container + CONTENT).getSize();
  }

  public boolean deleteFile(String container, String name) {

    boolean isFileRemoved = false;
    String fileName = container + "/" + name;
    store.removeBlob(s3Properties.getBucket(), fileName);
    if (!isFileExist(container, name)) {
      isFileRemoved = true;
    }
    return isFileRemoved;
  }

  public boolean isFileExist(String container, String name) {

    boolean isExist = false;
    String fileName = container + "/" + name;
    isExist = store.blobExists(s3Properties.getBucket(), fileName);
    return isExist;
  }

  public boolean isContainerExist(String container) {

    boolean isExist = false;
    isExist = store.list(s3Properties.getBucket()).parallelStream().anyMatch(meta -> meta.getName().equals(container.concat("/")));
    return isExist;
  }

  public String createManifest(String segmentContainer, String segmentPath, String manifestContainer, String contentType) {
    return null;
  }


  public String createManifest(File sf) {
    String contentType = "application/octet-stream";
    if (sf.getFormat() != null) {
      contentType = sf.getFormat();
    }

    return createManifest(sf.getId().toString(), SEGMENTS, sf.getId().toString(), contentType);

  }
}
