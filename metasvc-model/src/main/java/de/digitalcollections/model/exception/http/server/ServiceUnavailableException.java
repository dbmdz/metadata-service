package de.digitalcollections.model.exception.http.server;

import de.digitalcollections.model.exception.problem.MetasvcProblem;

public class ServiceUnavailableException extends HttpServerException {

  public ServiceUnavailableException(
      String methodKey, int status, String request, MetasvcProblem problem) {
    super(methodKey, status, request, problem);
  }
}
