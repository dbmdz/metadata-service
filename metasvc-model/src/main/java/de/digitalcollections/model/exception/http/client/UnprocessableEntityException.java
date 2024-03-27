package de.digitalcollections.model.exception.http.client;

import org.zalando.problem.Problem;

public class UnprocessableEntityException extends HttpClientException {

  public UnprocessableEntityException(
      String methodKey, int status, String request, Problem problem) {
    super(methodKey, status, request, problem);
  }
}
