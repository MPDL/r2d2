

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.bind.DatatypeConverter;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.r2d2.model.File;


public class MyTest {

  private static Logger Logger = LoggerFactory.getLogger(MyTest.class);

  private static String host = "http://localhost:8080";

  ObjectMapper objectMapper = new ObjectMapper();


  public void testAA() {

  }

  @Test
  public void test() throws Exception {
    objectMapper.findAndRegisterModules();
    //String filePath = "C:\\mytmp\\video.mkv";
    String filePath = "C:\\Users\\haarlae1\\Downloads\\scripts.zip";
    Path p = Paths.get(filePath);
    String token = login();

    //chunkedFileUpload(p, 20, token);

    singleFileUpload(p, token);

    //getData();


  }

  private String chunkedFileUpload(Path filePath, int chunks, String token) throws Exception {

    long size = Files.size(filePath);

    String res = Request.Post(host + "/api/datasets/dataset/a6124f2a-9a06-489d-a7e2-40b583ebbd23/files").addHeader("Authorization", token)
        .addHeader("X-File-Name", filePath.getFileName().toString()).addHeader("X-File-Total-Chunks", Integer.toString(chunks))
        .addHeader("X-File-Total-Size", Long.toString(size)).execute().returnContent().asString();


    Logger.info(res);

    File f = objectMapper.readValue(res, File.class);
    String fileId = f.getId().toString();


    long chunkSize = size / chunks;


    for (int currentChunk = 1; currentChunk <= chunks; currentChunk++) {
      long start = (currentChunk - 1) * chunkSize;
      long end = currentChunk * chunkSize;
      if (currentChunk == chunks) {
        end = 0;

      }
      Logger.info("Upload from bytes " + start + " to " + end);
      InputStream cfis1 = new ChunkedFileInputStream(filePath.toFile(), start, end);

      //FileInputStream cfis1 = new FileInputStream(p.toFile());


      res = Request.Post(host + "/api/datasets/dataset/a6124f2a-9a06-489d-a7e2-40b583ebbd23/files/" + fileId)
          .addHeader("Authorization", token).addHeader("X-File-Chunk-Number", Integer.toString(currentChunk)).bodyStream(cfis1).execute()
          .returnContent().asString();

      Logger.info(res);
    }

    Logger.info("uploaded file " + fileId);

    return fileId;

  }

  public String singleFileUpload(Path filePath, String token) throws Exception {
    InputStream is = new FileInputStream(filePath.toFile());

    //FileInputStream cfis1 = new FileInputStream(p.toFile());
    long size = Files.size(filePath);

    String res = Request.Post(host + "/files").addHeader("Authorization", token).addHeader("X-File-Name", filePath.getFileName().toString())
        .addHeader("X-File-Total-Size", Long.toString(size)).bodyStream(is).execute().returnContent().asString();


    Logger.info(res);

    File f = objectMapper.readValue(res, File.class);
    String fileId = f.getId().toString();

    Logger.info("uploaded file " + fileId);

    return fileId;
  }


  //@Test
  public void getData() throws Exception {

    byte[] b = Files.readAllBytes(Paths.get("C:\\mytmp\\Jellyfish.jpg"));
    byte[] hash = MessageDigest.getInstance("MD5").digest(b);
    Logger.info("MD5 Checksum: " + DatatypeConverter.printHexBinary(hash));

    InputStream res = Request.Get("https://cloud.mpcdf.mpg.de:8080/swift/v1/9f5978ae-02a2-4d93-a0e1-15e3c5f67a8d/content")

        .addHeader("X-Auth-Token", "15ec3437147c42a0a1f061ac4fba1ccd")
        //.addHeader("Range", "bytes=450000-7000000")
        .execute().returnContent().asStream();
    ZipInputStream zipStream = new ZipInputStream(res);

    ZipEntry zipEntry = zipStream.getNextEntry();
    while (zipEntry != null) {
      Logger.info("File found:" + zipEntry.getName());
      //Logger.info("File found:" + zipEntry.);
      zipEntry = zipStream.getNextEntry();
    }
    zipStream.closeEntry();
    zipStream.close();
  }

  private String login() throws Exception {
    //Login
    String token =
        Request.Post(host + "/login").bodyString("{" + "    \"username\" : \"testuser@mpdl.mpg.de\"," + "    \"password\" : \"test\"" + "}",
            ContentType.APPLICATION_JSON).execute().returnResponse().getFirstHeader("Authorization").getValue();

    Logger.info("Retrieved Token: " + token);
    return token;
    //Init upload
  }

}
