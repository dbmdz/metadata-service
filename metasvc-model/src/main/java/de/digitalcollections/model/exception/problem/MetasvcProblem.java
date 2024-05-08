package de.digitalcollections.model.exception.problem;

import de.digitalcollections.model.validation.ValidationError;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Singular;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.StatusType;

public final class MetasvcProblem extends AbstractThrowableProblem {

  private List<ValidationError> errors;
  private Date timestamp;
  private ProblemHint hint;

  public MetasvcProblem() {
    super();
  }

  public MetasvcProblem(
      URI type, String title, StatusType status, String detail, URI instance, Date timestamp) {
    super(type, title, status, detail, instance);
    this.timestamp = timestamp;
  }

  @Builder(setterPrefix = "with")
  public MetasvcProblem(
      URI type,
      String title,
      StatusType status,
      String detail,
      URI instance,
      Date timestamp,
      @Singular List<ValidationError> errors,
      ProblemHint hint) {
    super(type, title, status, detail, instance);
    this.timestamp = timestamp;
    this.errors = errors;
    this.hint = hint;
  }

  public MetasvcProblem(
      URI type,
      String title,
      StatusType status,
      String detail,
      URI instance,
      Date timestamp,
      ProblemHint hint) {
    super(type, title, status, detail, instance);
    this.timestamp = timestamp;
    this.hint = hint;
  }

  public List<ValidationError> getErrors() {
    return errors;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public ProblemHint getHint() {
    return hint;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder
        .append("MetasvcProblem {")
        .append("title=")
        .append(getTitle())
        .append(", type=")
        .append(getType())
        .append(", status=")
        .append(getStatus())
        .append(", instance=")
        .append(getInstance())
        .append(", hint=")
        .append(getHint())
        .append(", detail=")
        .append(getDetail())
        .append(", timestamp=")
        .append(getTimestamp())
        .append(", errors=")
        .append(
            getErrors() != null
                ? getErrors().stream()
                    .map(ValidationError::toString)
                    .collect(Collectors.joining(", ", "[", "]"))
                : null)
        .append(", parameters=")
        .append(getParameters())
        .append("}");
    return builder.toString();
  }
}
