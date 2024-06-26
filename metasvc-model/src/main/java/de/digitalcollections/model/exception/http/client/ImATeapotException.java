package de.digitalcollections.model.exception.http.client;

import de.digitalcollections.model.exception.problem.MetasvcProblem;

/**
 * HttpStatusCode 418 denoting the api is wrongfully using a teapot for making coffee as specified
 * in the Hyper Text Coffee Pot Control Protocol (see <a
 * href="https://tools.ietf.org/html/rfc2324">RFC 2324</a> for details).
 */
public class ImATeapotException extends HttpClientException {

  public ImATeapotException(String methodKey, int status, String request, MetasvcProblem problem) {
    super(methodKey, status, request, problem);
  }
}
