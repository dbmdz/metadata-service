package de.digitalcollections.cudami.client.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.digitalcollections.cudami.client.CudamiRestClient;
import de.digitalcollections.model.exception.TechnicalException;
import de.digitalcollections.model.view.RenderingTemplate;
import java.net.http.HttpClient;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CudamiRenderingTemplatesClient extends CudamiRestClient<RenderingTemplate> {

  public CudamiRenderingTemplatesClient(
      HttpClient http,
      String serverUrl,
      ObjectMapper mapper,
      Map<String, String> additionalGETHeaders) {
    super(
        http,
        serverUrl,
        RenderingTemplate.class,
        mapper,
        API_VERSION_PREFIX + "/renderingtemplates",
        additionalGETHeaders);
  }

  public List<Locale> getLanguages() throws TechnicalException {
    return doGetRequestForObjectList(baseEndpoint + "/languages", Locale.class);
  }
}
