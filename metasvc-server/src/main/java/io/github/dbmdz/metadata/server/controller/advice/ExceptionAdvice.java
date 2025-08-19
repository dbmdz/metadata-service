package io.github.dbmdz.metadata.server.controller.advice;

import de.digitalcollections.model.exception.problem.MetasvcProblem;
import de.digitalcollections.model.exception.problem.ProblemHint;
import de.digitalcollections.model.exception.problem.ProblemHinting;
import de.digitalcollections.model.validation.ValidationException;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ConflictException;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;
import org.zalando.problem.spring.web.advice.ProblemHandling;

@ControllerAdvice
public class ExceptionAdvice implements ProblemHandling {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionAdvice.class);

  private static URI getRequestUri(ServletWebRequest servletRequest) {
    HttpServletRequest req = servletRequest.getRequest();
    return UriComponentsBuilder.fromPath(req.getRequestURI())
        .query(req.getQueryString())
        .build()
        .toUri();
  }

  private static Status statusFromExceptionClass(Throwable exc) {
    if (exc instanceof ResourceNotFoundException || exc instanceof NoResourceFoundException) {
      return Status.NOT_FOUND;
    } else if (exc instanceof ConflictException) {
      return Status.CONFLICT;
    } else if (exc instanceof ValidationException) {
      return Status.UNPROCESSABLE_ENTITY;
    } else if (exc instanceof HttpMediaTypeNotAcceptableException) {
      return Status.NOT_FOUND;
    } else {
      return Status.INTERNAL_SERVER_ERROR;
    }
  }

  private static ProblemHint hintFromException(Throwable exc) {
    if (exc == null) return ProblemHint.NONE;
    if (exc instanceof ProblemHinting hintedExc) {
      return hintedExc.getHint();
    } else if (exc.getCause() != null) {
      return hintFromException(exc.getCause());
    } else {
      return ProblemHint.NONE;
    }
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<Problem> handleNotFound(
      UsernameNotFoundException e, ServletWebRequest request) {
    return create(Status.NOT_FOUND, e, request);
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<Problem> handleValidationException(
      ValidationException exception, ServletWebRequest request) {
    ThrowableProblem problem =
        MetasvcProblem.builder()
            .withType(
                UriComponentsBuilder.fromPath("/errors/")
                    .path(exception.getClass().getSimpleName())
                    .build()
                    .toUri())
            .withTitle("Validation Exception")
            .withStatus(statusFromExceptionClass(exception))
            .withInstance(getRequestUri(request))
            .withDetail(exception.getMessage())
            .withErrors(exception.getErrors())
            .withTimestamp(new Date())
            .withHint(exception.getHint())
            .build();
    return create(problem, request);
  }

  @ExceptionHandler
  public ResponseEntity<Problem> handleAllOther(Exception exception, ServletWebRequest request) {
    Throwable cause = exception;
    while (cause.getCause() != null) {
      cause = cause.getCause();
    }
    ThrowableProblem problem =
        MetasvcProblem.builder()
            .withType(
                UriComponentsBuilder.fromPath("/errors/")
                    .path(cause.getClass().getSimpleName())
                    .build()
                    .toUri())
            .withTitle("Metadata-service Exception")
            .withStatus(statusFromExceptionClass(cause))
            .withDetail(cause.getMessage())
            .withInstance(getRequestUri(request))
            .withHint(hintFromException(exception))
            .withTimestamp(new Date())
            .build();
    if (problem.getStatus() == Status.INTERNAL_SERVER_ERROR)
      LOGGER.error(cause.getMessage(), exception);
    return create(problem, request);
  }
}
