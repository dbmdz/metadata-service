package de.digitalcollections.cudami.client.identifiable.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.digitalcollections.cudami.client.identifiable.CudamiIdentifiablesClient;
import de.digitalcollections.model.identifiable.resource.ImageFileResource;
import java.net.http.HttpClient;
import java.util.Map;

public class CudamiImageFileResourcesClient extends CudamiIdentifiablesClient<ImageFileResource> {

  public CudamiImageFileResourcesClient(
      HttpClient http,
      String serverUrl,
      ObjectMapper mapper,
      Map<String, String> additionalGETHeaders) {
    super(
        http,
        serverUrl,
        ImageFileResource.class,
        mapper,
        API_VERSION_PREFIX + "/imagefileresources",
        additionalGETHeaders);
  }
}
