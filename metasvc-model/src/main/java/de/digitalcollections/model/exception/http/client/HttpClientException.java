package de.digitalcollections.model.exception.http.client;

import de.digitalcollections.model.exception.http.HttpException;
import de.digitalcollections.model.exception.problem.MetasvcProblem;

public class HttpClientException extends HttpException {

  public HttpClientException(String methodKey, int status, String request, MetasvcProblem problem) {
    super(methodKey, status, request, problem);
  }
}
