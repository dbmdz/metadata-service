package io.github.dbmdz.metadata.server.business.api.service.exceptions;

public class ServiceException extends Exception {

  public ServiceException(String message) {
    super(message);
  }

  public ServiceException(Throwable cause) {
    super(cause);
  }

  public ServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
