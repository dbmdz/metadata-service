package io.github.dbmdz.metadata.server.business.api.service.exceptions;

public class ConflictException extends Exception {

  public ConflictException(String msg, Exception e) {
    super(msg, e);
  }

  public ConflictException(String msg) {
    super(msg);
  }
}
