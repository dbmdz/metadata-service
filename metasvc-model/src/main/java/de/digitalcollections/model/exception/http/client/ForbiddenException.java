package de.digitalcollections.model.exception.http.client;

import de.digitalcollections.model.exception.problem.MetasvcProblem;

public class ForbiddenException extends HttpClientException {

  public ForbiddenException(String methodKey, int status, String request, MetasvcProblem problem) {
    super(methodKey, status, request, problem);
  }
}
