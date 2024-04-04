package de.digitalcollections.model.exception.http.server;

import org.zalando.problem.Problem;

public class GatewayTimeOutException extends HttpServerException {

  public GatewayTimeOutException(String methodKey, int status, String request, Problem problem) {
    super(methodKey, status, request, problem);
  }
}
