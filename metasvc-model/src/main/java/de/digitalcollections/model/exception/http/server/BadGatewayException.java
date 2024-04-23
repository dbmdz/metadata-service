package de.digitalcollections.model.exception.http.server;

import de.digitalcollections.model.exception.problem.MetasvcProblem;

public class BadGatewayException extends HttpServerException {

  public BadGatewayException(String methodKey, int status, String request, MetasvcProblem problem) {
    super(methodKey, status, request, problem);
  }
}
