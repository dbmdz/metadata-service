package io.github.dbmdz.metadata.server.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

public class MdcLoggingInterceptor implements HandlerInterceptor {
  /** Clear MDC to avoid data leaking between two requests handled by the same thread. */
  @Override
  public void afterCompletion(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler,
      Exception ex)
      throws Exception {
    MDC.clear();
  }
}
