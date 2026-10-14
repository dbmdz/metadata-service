package de.digitalcollections.cudami.client.identifiable.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.digitalcollections.model.identifiable.entity.Event;
import java.net.http.HttpClient;
import java.util.Map;

public class CudamiEventsClient extends CudamiEntitiesClient<Event> {

  public CudamiEventsClient(
      HttpClient http,
      String serverUrl,
      ObjectMapper mapper,
      Map<String, String> additionalGETHeaders) {
    super(
        http, serverUrl, Event.class, mapper, API_VERSION_PREFIX + "/events", additionalGETHeaders);
  }
}
