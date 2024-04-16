package io.github.dbmdz.metadata.server.backend.api.repository.exceptions;

import de.digitalcollections.model.exception.ProblemHinting;

public class RepositoryException extends Exception implements ProblemHinting {

  private ProblemHint hint = ProblemHint.NONE;

  public RepositoryException(String message) {
    super(message);
  }

  public RepositoryException(Throwable cause) {
    super("An unexpected error occured!", cause);
  }

  public RepositoryException(Throwable cause, ProblemHint hint) {
    this(cause);
    this.hint = hint;
  }

  public RepositoryException(String message, Throwable cause) {
    super(message, cause);
  }

  public RepositoryException(String message, Throwable cause, ProblemHint hint) {
    this(message, cause);
    this.hint = hint;
  }

  @Override
  public ProblemHint getHint() {
    return hint;
  }
}
