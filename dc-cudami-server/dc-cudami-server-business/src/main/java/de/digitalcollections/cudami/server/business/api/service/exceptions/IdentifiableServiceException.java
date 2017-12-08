package de.digitalcollections.cudami.server.business.api.service.exceptions;

public class IdentifiableServiceException extends Exception {

  public IdentifiableServiceException(String message) {
    super(message);
  }

  public IdentifiableServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
