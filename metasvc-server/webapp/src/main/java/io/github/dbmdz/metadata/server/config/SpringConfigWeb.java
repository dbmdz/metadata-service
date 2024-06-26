package io.github.dbmdz.metadata.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.digitalcollections.model.jackson.DigitalCollectionsObjectMapper;
import io.github.dbmdz.metadata.server.converter.StringToOrderConverter;
import io.github.dbmdz.metadata.server.interceptors.RequestIdLoggingInterceptor;
import io.github.dbmdz.metadata.server.thymeleaf.SpacesDialect;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.zalando.problem.jackson.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

@Configuration
public class SpringConfigWeb implements WebMvcConfigurer {

  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    return new DigitalCollectionsObjectMapper()
        .registerModules(new ProblemModule(), new ConstraintViolationProblemModule());
  }

  @Bean
  public FormattingConversionService conversionService() {
    DefaultFormattingConversionService conversionService =
        new DefaultFormattingConversionService(false);

    // for conversion of String to LocalDate for controller method arguments:
    DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
    registrar.setDateFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    registrar.setDateTimeFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
    registrar.registerFormatters(conversionService);

    return conversionService;
  }

  @Override
  public void configurePathMatch(PathMatchConfigurer configurer) {
    // "This is generally recommended to reduce ambiguity and to avoid issues such as when a "."
    // appears in the path for other reasons."
    configurer.setUseRegisteredSuffixPatternMatch(Boolean.TRUE);
  }

  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer
        .favorParameter(true)
        .favorPathExtension(true)
        // .ignoreAcceptHeader(false)
        .ignoreUnknownPathExtensions(true)
        .useRegisteredExtensionsOnly(false)
        // we are a REST-Server (no HTML/Webapp)!
        .defaultContentType(MediaType.APPLICATION_JSON)
        .mediaTypes(
            Map.of(
                "html",
                MediaType.TEXT_HTML,
                "json",
                MediaType.APPLICATION_JSON,
                "xml",
                MediaType.APPLICATION_XML));
  }

  /**
   * Needed to get rid of all the whitespaces in the rendered thymeleaf HTML
   *
   * @return the dialect
   */
  @Bean
  public AbstractProcessorDialect whiteSpaceNormalizedDialect() {
    return new SpacesDialect();
  }

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(new StringToOrderConverter());
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new RequestIdLoggingInterceptor());
  }
}
