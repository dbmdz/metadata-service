package io.github.dbmdz.metadata.server.controller;

public class CudamiControllerException extends Exception {

  public CudamiControllerException(Throwable cause) {
    super(cause);
  }

  public CudamiControllerException(String message) {
    super(message);
  }

  public CudamiControllerException(String message, Throwable cause) {
    super(message, cause);
  }
}
