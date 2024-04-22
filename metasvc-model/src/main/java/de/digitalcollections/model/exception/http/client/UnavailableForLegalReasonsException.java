package de.digitalcollections.model.exception.http.client;

import de.digitalcollections.model.exception.problem.MetasvcProblem;

public class UnavailableForLegalReasonsException extends HttpClientException {

  public UnavailableForLegalReasonsException(
      String methodKey, int status, String request, MetasvcProblem problem) {
    super(methodKey, status, request, problem);
  }
}
