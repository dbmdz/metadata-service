package de.digitalcollections.cudami.server.backend.api.repository;

import java.util.List;
import java.util.Locale;

/**
 * Repository for Locale persistence handling.
 */
public interface LocaleRepository {

  List<String> findAllLanguages();

  List<Locale> findAllLocales();

  String getDefaultLanguage();

  Locale getDefaultLocale();
}
