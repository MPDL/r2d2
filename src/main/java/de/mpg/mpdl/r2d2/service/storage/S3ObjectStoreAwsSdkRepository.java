package de.mpg.mpdl.r2d2.service.storage;

import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.mpg.mpdl.r2d2.S3ObjectStoreConfigurationProperties;
import de.mpg.mpdl.r2d2.exceptions.NotFoundException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.model.FileChunk;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

@Repository
@ConditionalOnProperty(value = "r2d2.storage", havingValue = "s3awssdk")
public class S3ObjectStoreAwsSdkRepository implements ObjectStoreRepository {

  private static Logger LOGGER = LoggerFactory.getLogger(S3ObjectStoreAwsSdkRepository.class);

  @Autowired
  S3ObjectStoreConfigurationProperties s3Properties;

  private S3Client client;

  private static final String CONTENT = "/content";

  public S3ObjectStoreAwsSdkRepository(S3Client s3Client) {
    this.client = s3Client;
  }

  public String uploadChunk(File sf, FileChunk chunk, InputStream is) {

    LOGGER.info("Uploading Chunk to container " + sf.getId());

    LOGGER.info("S3 store returned etag " + "");
    return "";
  }


  public String uploadFile(File file, InputStream is) throws R2d2TechnicalException {

    LOGGER.info("Uploading single file to container " + file.getId());
    try {
      PutObjectRequest po_request =
          PutObjectRequest.builder().bucket(s3Properties.getBucket()).key(file.getId().toString() + CONTENT).build();

      PutObjectResponse po_response = client.putObject(po_request, RequestBody.fromInputStream(is, file.getSize()));
      LOGGER.info("S3 store returned etag " + po_response.eTag());
      return po_response.eTag();
    } catch (S3Exception s3e) {
      throw new R2d2TechnicalException(s3e);
    }
  }

  public InputStream downloadFile(String container, String name) throws R2d2TechnicalException {

    try {
      GetObjectRequest go_request =
          GetObjectRequest.builder().key(container.concat("/").concat(name)).bucket(s3Properties.getBucket()).build();

      // ResponseBytes<GetObjectResponse> objectBytes = client.get.getObjectAsBytes(go_request);
      ResponseInputStream<GetObjectResponse> go_as_stream = client.getObject(go_request);
      return go_as_stream;
    } catch (S3Exception s3e) {
      throw new R2d2TechnicalException(s3e);
    }
  }

  // not possible due to lack of access rights ...
  public List<Object> listAllContainers() throws NotFoundException, R2d2TechnicalException {

    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.setSerializationInclusion(Include.NON_NULL);
    mapper.setSerializationInclusion(Include.NON_EMPTY);
    try {
      ListObjectsRequest lo_request = ListObjectsRequest.builder().bucket(s3Properties.getBucket())
          //.prefix(prefix)
          .build();

      ListObjectsResponse lo_response = client.listObjects(lo_request);
      // 
      return lo_response.contents().stream().map(s3o -> mapper.valueToTree(s3o.toBuilder())).collect(Collectors.toList());

    } catch (S3Exception e) {
      if (e instanceof NoSuchBucketException) {
        throw new NotFoundException(e);
      } else {
        throw new R2d2TechnicalException(e);
      }
    }
  }

  public List<Object> listContainer(String key) throws R2d2TechnicalException {

    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.setSerializationInclusion(Include.NON_NULL);
    mapper.setSerializationInclusion(Include.NON_EMPTY);
    try {
      HeadObjectRequest objectRequest = HeadObjectRequest.builder().key(key).bucket(s3Properties.getBucket()).build();

      HeadObjectResponse objectHead = client.headObject(objectRequest);
      System.out.println("head 4 key " + key + "   " + objectHead);
      return Collections.singletonList(mapper.valueToTree(objectHead.toBuilder()));

    } catch (S3Exception e) {
      throw new R2d2TechnicalException(e);
    }
  }

  // not possible due to lack of access rights ...
  public boolean deleteContainer(String container) throws NotFoundException {
    return false;
  }

  public String getPublicURI(String container) throws R2d2TechnicalException {
    try {
      GetUrlRequest gu_request = GetUrlRequest.builder().bucket(s3Properties.getBucket()).key(container + CONTENT).build();

      URL url = client.utilities().getUrl(gu_request);
      return url.toString();

    } catch (S3Exception e) {
      throw new R2d2TechnicalException(e);
    }
  }

  public Long getFileSize(String container) throws R2d2TechnicalException {
    try {
      HeadObjectRequest ho_request = HeadObjectRequest.builder().key(container + CONTENT).bucket(s3Properties.getBucket()).build();

      HeadObjectResponse ho_response = client.headObject(ho_request);
      return ho_response.contentLength();

    } catch (S3Exception e) {
      throw new R2d2TechnicalException(e);
    }
  }

  public boolean deleteFile(String container, String name) throws R2d2TechnicalException {

    try {
      DeleteObjectRequest do_request = DeleteObjectRequest.builder().bucket(s3Properties.getBucket()).key(name).build();

      DeleteObjectResponse do_response = client.deleteObject(do_request);
      return do_response.sdkHttpResponse().isSuccessful();
    } catch (S3Exception e) {
      throw new R2d2TechnicalException(e);
    }
  }

  public boolean isFileExist(String container, String name) {

    return false;
  }

  public boolean isContainerExist(String container) {

    return false;
  }

  public String createManifest(String segmentContainer, String segmentPath, String manifestContainer, String contentType) {
    return "n/a";
  }


  public String createManifest(File sf) {

    return "n/a";

  }
}
