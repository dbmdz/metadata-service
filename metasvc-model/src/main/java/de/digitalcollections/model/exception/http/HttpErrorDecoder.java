package de.digitalcollections.model.exception.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.digitalcollections.model.exception.http.client.ForbiddenException;
import de.digitalcollections.model.exception.http.client.HttpClientException;
import de.digitalcollections.model.exception.http.client.ImATeapotException;
import de.digitalcollections.model.exception.http.client.ResourceNotFoundException;
import de.digitalcollections.model.exception.http.client.UnauthorizedException;
import de.digitalcollections.model.exception.http.client.UnavailableForLegalReasonsException;
import de.digitalcollections.model.exception.http.client.UnprocessableEntityException;
import de.digitalcollections.model.exception.http.server.BadGatewayException;
import de.digitalcollections.model.exception.http.server.GatewayTimeOutException;
import de.digitalcollections.model.exception.http.server.HttpServerException;
import de.digitalcollections.model.exception.http.server.HttpVersionNotSupportedException;
import de.digitalcollections.model.exception.http.server.NotImplementedException;
import de.digitalcollections.model.exception.http.server.ServiceUnavailableException;
import de.digitalcollections.model.exception.problem.MetasvcProblem;
import de.digitalcollections.model.jackson.DigitalCollectionsObjectMapper;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.problem.jackson.ProblemModule;

public class HttpErrorDecoder {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpErrorDecoder.class);
  private static final ObjectMapper mapper;

  static {
    mapper = new DigitalCollectionsObjectMapper().registerModule(new ProblemModule());
  }

  private static HttpException clientException(
      String methodKey, int statusCode, String requestUrl, MetasvcProblem problem) {
    switch (statusCode) {
      case 401:
        return new UnauthorizedException(methodKey, statusCode, requestUrl, problem);
      case 403:
        return new ForbiddenException(methodKey, statusCode, requestUrl, problem);
      case 404:
        return new ResourceNotFoundException(methodKey, statusCode, requestUrl, problem);
      case 418:
        return new ImATeapotException(methodKey, statusCode, requestUrl, problem);
      case 422:
        return new UnprocessableEntityException(methodKey, statusCode, requestUrl, problem);
      case 451:
        return new UnavailableForLegalReasonsException(methodKey, statusCode, requestUrl, problem);
      default:
        return new HttpClientException(methodKey, statusCode, requestUrl, problem);
    }
  }

  public static HttpException decode(String methodKey, int statusCode, HttpResponse response) {
    String requestUrl = null;
    MetasvcProblem problem = null;
    if (response != null) {
      requestUrl =
          Optional.ofNullable(response.request())
              .map(HttpRequest::uri)
              .map(
                  uri -> {
                    try {
                      return uri.toURL();
                    } catch (MalformedURLException e) {
                      LOGGER.warn("Invalid request URL for: " + uri.toString());
                      return null;
                    }
                  })
              .map(URL::toString)
              .orElse(null);

      final byte[] body = (byte[]) response.body();
      if (body != null && body.length > 0) {
        try {
          problem = mapper.readerFor(MetasvcProblem.class).readValue(body);
        } catch (Exception e) {
          LOGGER.error(
              "Got response="
                  + new String(body, StandardCharsets.UTF_8)
                  + " but cannot construct problem: "
                  + e,
              e);
        }
      }
    }

    if (400 <= statusCode && statusCode < 500) {
      return clientException(methodKey, statusCode, requestUrl, problem);
    } else if (500 <= statusCode && statusCode < 600) {
      return serverException(methodKey, statusCode, requestUrl, problem);
    } else {
      return genericHttpException(methodKey, statusCode, requestUrl, problem);
    }
  }

  private static HttpException genericHttpException(
      String methodKey, int statusCode, String requestUrl, MetasvcProblem problem) {
    return new HttpException(methodKey, statusCode, requestUrl, problem);
  }

  private static HttpServerException serverException(
      String methodKey, int statusCode, String requestUrl, MetasvcProblem problem) {
    switch (statusCode) {
      case 501:
        return new NotImplementedException(methodKey, statusCode, requestUrl, problem);
      case 502:
        return new BadGatewayException(methodKey, statusCode, requestUrl, problem);
      case 503:
        return new ServiceUnavailableException(methodKey, statusCode, requestUrl, problem);
      case 504:
        return new GatewayTimeOutException(methodKey, statusCode, requestUrl, problem);
      case 505:
        return new HttpVersionNotSupportedException(methodKey, statusCode, requestUrl, problem);
      default:
        return new HttpServerException(methodKey, statusCode, requestUrl, problem);
    }
  }
}
