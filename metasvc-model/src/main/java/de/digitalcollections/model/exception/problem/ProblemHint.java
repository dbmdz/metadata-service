package de.digitalcollections.model.exception.problem;

public enum ProblemHint {
  NONE("No hint available."),
  RETRY_RECOMMENDED("The error is caused by a concurrent operation. It might succeed if retried."),
  REFERENCED_OBJECT_NOT_EXISTS(
      "An included object is not stored yet. Please save it separately before trying this operation again."),
  UNIQUE_VIOLATION("One or more of these identifiers exist already."),
  MANDATORY_CHECK_FAILED(
      "A data integrity check failed. Please check for missing or incorrect properties."),
  PROPERTY_MUST_NOT_BE_NULL("Ensure that all necessary/mandatory properties are set.");

  private String description;

  ProblemHint(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return name() + " (" + description + ")";
  }
}
