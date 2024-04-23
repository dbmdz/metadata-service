package de.digitalcollections.model.exception.http.client;

import de.digitalcollections.model.exception.problem.MetasvcProblem;

public class UnauthorizedException extends HttpClientException {

  public UnauthorizedException(
      String methodKey, int status, String request, MetasvcProblem problem) {
    super(methodKey, status, request, problem);
  }
}
