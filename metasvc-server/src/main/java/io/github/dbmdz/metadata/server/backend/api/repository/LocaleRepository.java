package io.github.dbmdz.metadata.server.backend.api.repository;

import java.util.List;
import java.util.Locale;

/** Repository for Locale persistence handling. */
public interface LocaleRepository {

  String getDefaultLanguage();

  Locale getDefaultLocale();

  List<String> getSupportedLanguages();

  List<Locale> getSupportedLocales();
}
