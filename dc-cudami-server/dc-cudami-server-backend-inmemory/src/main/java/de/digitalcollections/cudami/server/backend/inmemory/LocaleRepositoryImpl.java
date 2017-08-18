package de.digitalcollections.cudami.server.backend.inmemory;

import de.digitalcollections.cudami.server.backend.api.repository.LocaleRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class LocaleRepositoryImpl implements LocaleRepository {

  private Locale defaultLocale;

  @Override
  public List<Locale> findAll() {
    return Arrays.asList(Locale.getAvailableLocales());
  }

  @Override
  public Locale getDefault() {
    return defaultLocale;
  }

  @Value(value = "${locales.default:\"en_US\"}")
  protected void setDefaultLocale(String localeCode) {
    defaultLocale = LocaleUtils.toLocale(localeCode);
  }
}
