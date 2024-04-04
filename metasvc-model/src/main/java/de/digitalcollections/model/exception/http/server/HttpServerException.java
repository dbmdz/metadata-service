package de.digitalcollections.model.exception.http.server;

import de.digitalcollections.model.exception.http.HttpException;
import org.zalando.problem.Problem;

public class HttpServerException extends HttpException {

  public HttpServerException(String methodKey, int status, String request, Problem problem) {
    super(methodKey, status, request, problem);
  }
}
