package de.digitalcollections.model.exception.http.server;

import de.digitalcollections.model.exception.http.HttpException;
import de.digitalcollections.model.exception.problem.MetasvcProblem;

public class HttpServerException extends HttpException {

  public HttpServerException(String methodKey, int status, String request, MetasvcProblem problem) {
    super(methodKey, status, request, problem);
  }
}
