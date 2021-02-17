package de.mpg.mpdl.r2d2.transformation.doi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

@Component
public class DoiDataJsonConverter {

  public enum DoiDataType {
    DOIS;

    @Override
    public String toString() {
      return super.toString().toLowerCase();
    }
  }

  public enum DoiEvent {
    PUBLISH,
    REGISTER,
    HIDE;

    @Override
    public String toString() {
      return super.toString().toLowerCase();
    }
  }

  //TODO: Check base64Encoding

  public String createJsonForDoiCreation(DoiDataType doiDataType, String doiPrefix, String url, String doiMetadataXmlBase64Encoded)
      throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    ObjectNode attributesNode = mapper.createObjectNode();
    //        attributesNode.put("event", doiEvent.toString());
    //        attributesNode.put("doi", "");
    attributesNode.put("prefix", doiPrefix);
    attributesNode.put("url", url);
    attributesNode.put("xml", doiMetadataXmlBase64Encoded);

    ObjectNode dataNode = mapper.createObjectNode();
    //        dataNode.put("id","");
    dataNode.put("type", doiDataType.toString());
    dataNode.set("attributes", attributesNode);

    ObjectNode doiDataRootNode = mapper.createObjectNode();
    doiDataRootNode.set("data", dataNode);

    String doiDataJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(doiDataRootNode);

    return doiDataJson;
  }

  public String createJsonForDoiUpdate(DoiEvent doiEvent, String url, String doiMetadataXmlBase64Encoded) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    ObjectNode attributesNode = mapper.createObjectNode();
    attributesNode.put("event", doiEvent.toString());
    attributesNode.put("url", url);
    attributesNode.put("xml", doiMetadataXmlBase64Encoded);

    ObjectNode dataNode = mapper.createObjectNode();
    dataNode.set("attributes", attributesNode);

    ObjectNode doiDataRootNode = mapper.createObjectNode();
    doiDataRootNode.set("data", dataNode);

    String doiDataJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(doiDataRootNode);

    return doiDataJson;
  }

}
