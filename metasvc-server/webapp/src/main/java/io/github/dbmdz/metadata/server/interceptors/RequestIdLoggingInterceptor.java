package io.github.dbmdz.metadata.server.interceptors;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

public class RequestIdLoggingInterceptor implements HandlerInterceptor {

  /**
   * Register the request identifier (if received from client/frontend server) in the logging
   * context.
   */
  @Override
  @SuppressFBWarnings(value = {"HRS_REQUEST_PARAMETER_TO_HTTP_HEADER"})
  public boolean preHandle(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler) {
    String requestId = request.getHeader("X-Request-Id");
    if (StringUtils.hasText(requestId)) {
      MDC.put("request_id", requestId);
      response.setHeader("X-Request-Id", requestId);
    }
    return true;
  }
}
