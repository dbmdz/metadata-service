package de.digitalcollections.model.validation;

import de.digitalcollections.model.exception.problem.ProblemHint;
import de.digitalcollections.model.exception.problem.ProblemHinting;
import java.util.ArrayList;
import java.util.List;

public class ValidationException extends Exception implements ProblemHinting {

  private List<ValidationError> errors = new ArrayList<>(1);
  private ProblemHint hint = ProblemHint.NONE;

  public ValidationException(String msg, Exception e) {
    super(msg, e);
  }

  public ValidationException(String msg) {
    super(msg);
  }

  public ValidationException(String msg, ProblemHint hint) {
    this(msg);
    this.hint = hint;
  }

  public void addError(ValidationError validationError) {
    errors.add(validationError);
  }

  public List<ValidationError> getErrors() {
    return errors;
  }

  @Override
  public ProblemHint getHint() {
    return hint;
  }
}
