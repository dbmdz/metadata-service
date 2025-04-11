package de.digitalcollections.cudami.client.identifiable.resource;

import static de.digitalcollections.cudami.client.CudamiRestClient.API_VERSION_PREFIX;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.digitalcollections.model.exception.TechnicalException;
import de.digitalcollections.model.identifiable.resource.FileResource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;

public class CudamiFileResourcesBinaryClient {

  protected final ObjectMapper mapper;
  protected final URI serverUri;

  public CudamiFileResourcesBinaryClient(String serverUrl, ObjectMapper mapper) {
    this.mapper = mapper;
    this.serverUri = URI.create(serverUrl);
  }

  private FileResource doPost(HttpEntity entity) throws TechnicalException {
    try {
      HttpPost post = new HttpPost(serverUri + API_VERSION_PREFIX + "/files");
      post.setEntity(entity);
      try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
        FileResource result =
            client.execute(
                post,
                response -> {
                  if (response.getCode() == HttpStatus.SC_OK) {
                    FileResource fileResource =
                        mapper.readValue(response.getEntity().getContent(), FileResource.class);
                    return fileResource;
                  }
                  return null;
                });
        if (result == null) throw new TechnicalException("Error saving uploaded file data");
        return result;
      }
    } catch (IOException ex) {
      throw new TechnicalException("Error posting data to server", ex);
    }
  }

  public FileResource upload(InputStream inputStream, String filename, String contentType)
      throws TechnicalException {
    try {
      filename =
          URLEncoder.encode(
              filename,
              StandardCharsets.UTF_8.toString()); // filenames with umlauts caused exception...
      HttpEntity entity =
          MultipartEntityBuilder.create()
              .addBinaryBody(contentType, inputStream, ContentType.create(contentType), filename)
              .build();
      return doPost(entity);
    } catch (Exception ex) {
      throw new TechnicalException("Error saving uploaded file data", ex);
    }
  }

  public FileResource upload(byte[] bytes, String filename, String contentType)
      throws TechnicalException {
    try {
      filename =
          URLEncoder.encode(
              filename,
              StandardCharsets.UTF_8.toString()); // filenames with umlauts caused exception...
      HttpEntity entity =
          MultipartEntityBuilder.create()
              .addBinaryBody(contentType, bytes, ContentType.create(contentType), filename)
              .build();
      return doPost(entity);
    } catch (Exception ex) {
      throw new TechnicalException("Error saving uploaded file data", ex);
    }
  }
}
