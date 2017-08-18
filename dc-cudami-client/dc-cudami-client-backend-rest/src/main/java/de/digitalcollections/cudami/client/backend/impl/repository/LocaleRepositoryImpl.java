package de.digitalcollections.cudami.client.backend.impl.repository;

import de.digitalcollections.cudami.client.backend.api.repository.LocaleRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LocaleRepositoryImpl implements LocaleRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(LocaleRepositoryImpl.class);

  @Autowired
  private LocaleRepositoryEndpoint endpoint;

  @Override
  public List<Locale> findAll() {
    List<String> localeCodes = endpoint.find();
    List<Locale> result = new ArrayList<>();
    for (String localeCode : localeCodes) {
      try {
        Locale locale = LocaleUtils.toLocale(localeCode);
        result.add(locale);
      } catch (IllegalArgumentException ex) {
        LOGGER.warn("Illegal argument for Locale: '{}'. Ignoring it.", localeCode);
      }
    }
    return result;
  }

  @Override
  public Locale getDefault() {
    String defaultLocaleCode = endpoint.getDefault();
    return LocaleUtils.toLocale(defaultLocaleCode);
  }
}
