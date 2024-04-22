package de.digitalcollections.model.exception.http.client;

import de.digitalcollections.model.exception.problem.MetasvcProblem;

public class ResourceNotFoundException extends HttpClientException {

  public ResourceNotFoundException(
      String methodKey, int status, String request, MetasvcProblem problem) {
    super(methodKey, status, request, problem);
  }

  public ResourceNotFoundException(String methodKey, int status, String request) {
    this(methodKey, status, request, null);
  }
}
