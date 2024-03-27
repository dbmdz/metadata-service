package de.digitalcollections.model.exception.http.client;

import de.digitalcollections.model.exception.http.HttpException;
import org.zalando.problem.Problem;

public class HttpClientException extends HttpException {

  public HttpClientException(String methodKey, int status, String request, Problem problem) {
    super(methodKey, status, request, problem);
  }
}
