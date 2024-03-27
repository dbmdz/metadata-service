package de.digitalcollections.model.exception.http.server;

import org.zalando.problem.Problem;

public class BadGatewayException extends HttpServerException {

  public BadGatewayException(String methodKey, int status, String request, Problem problem) {
    super(methodKey, status, request, problem);
  }
}
