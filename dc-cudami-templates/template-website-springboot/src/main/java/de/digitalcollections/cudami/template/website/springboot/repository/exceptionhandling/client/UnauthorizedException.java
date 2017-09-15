package de.digitalcollections.cudami.template.website.springboot.repository.exceptionhandling.client;

import feign.Response;

public class UnauthorizedException extends HttpClientException {

  public UnauthorizedException(String methodKey, Response response) {
    super(methodKey, response);
  }

}
