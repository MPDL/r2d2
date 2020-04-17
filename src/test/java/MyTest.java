

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

import javax.xml.bind.DatatypeConverter;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.mpg.mpdl.r2d2.model.File;


public class MyTest {

  private static Logger Logger = LoggerFactory.getLogger(MyTest.class);

  private static String host = "http://localhost:8080";
  
  ObjectMapper objectMapper = new ObjectMapper();

  //@Test
  public void testFileUpload() throws Exception {

   objectMapper.findAndRegisterModules();
    //Login
    String token =
        Request.Post(host + "/login").bodyString("{" + "    \"username\" : \"testuser@mpdl.mpg.de\"," + "    \"password\" : \"test\"" + "}",
            ContentType.APPLICATION_JSON).execute().returnResponse().getFirstHeader("Authorization").getValue();

    //Init upload
    String res = Request.Post(host + "/api/datasets/dataset/a6124f2a-9a06-489d-a7e2-40b583ebbd23/files")
        .addHeader("Authorization", token).addHeader("X-File-Name", "test.jpg").addHeader("X-File-Total-Chunks", "2").execute()
        .returnContent().asString();

    
    Logger.info(res);
    
    File f = objectMapper.readValue(res, File.class);
    String fileId = f.getId().toString();
    
    String filePath = "C:\\mytmp";
    Path p = Paths.get(filePath, "Jellyfish.jpg");
    
    ChunkedFileInputStream cfis1 = new ChunkedFileInputStream(p.toFile(), 0, 500 * 1024);
    
    //FileInputStream cfis1 = new FileInputStream(p.toFile());
    
    
    res = Request.Post(host + "/api/datasets/dataset/a6124f2a-9a06-489d-a7e2-40b583ebbd23/files/" + fileId)
        .addHeader("Authorization", token).addHeader("X-File-Chunk-Number", "1").bodyStream(cfis1).execute()
        .returnContent().asString();

    Logger.info(res);
    
    
    
    ChunkedFileInputStream cfis2 = new ChunkedFileInputStream(p.toFile(), 500*1024, 0);
    
    
    res = Request.Post(host + "/api/datasets/dataset/a6124f2a-9a06-489d-a7e2-40b583ebbd23/files/" + fileId)
        .addHeader("Authorization", token).addHeader("X-File-Chunk-Number", "2").bodyStream(cfis2).execute()
        .returnContent().asString();
    
    Logger.info(res);
    

    Logger.info("uploaded file " + fileId);
    
  }
  
  
  @Test
  public void getData() throws Exception
  {
    
    byte[] b = Files.readAllBytes(Paths.get("C:\\mytmp\\Jellyfish.jpg"));
    byte[] hash = MessageDigest.getInstance("MD5").digest(b);
    Logger.info("MD5 Checksum: " + DatatypeConverter.printHexBinary(hash));
    
    String res = Request.Head("https://cloud.mpcdf.mpg.de:8080/swift/v1/25ed2abc-1112-4809-8bc9-3d6c7314a660/content")
        
        .addHeader("X-Auth-Token", "6e1235ed88384aa1b531dbe595b34407")
        //.addHeader("Range", "bytes=450000-7000000")
        .execute().returnContent().asString();
    Logger.info(res);
  }

}
