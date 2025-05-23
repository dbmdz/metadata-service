package io.github.dbmdz.metadata.server.backend.impl.inmemory;

import de.digitalcollections.cudami.model.config.CudamiConfig;
import io.github.dbmdz.metadata.server.backend.api.repository.LocaleRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Repository;

@Repository
public class LocaleRepositoryImpl implements LocaleRepository {

  private String language;
  private Locale locale;

  public LocaleRepositoryImpl(CudamiConfig cudamiConfig) {
    this.language = cudamiConfig.getDefaults().getLanguage();
    this.locale = cudamiConfig.getDefaults().getLocale();
  }

  @Override
  public List<String> getSupportedLanguages() {
    return Arrays.asList(Locale.getISOLanguages());
  }

  @Override
  public List<Locale> getSupportedLocales() {
    return Arrays.asList(Locale.getAvailableLocales());
  }

  @Override
  public String getDefaultLanguage() {
    return language;
  }

  @Override
  public Locale getDefaultLocale() {
    return locale;
  }
}
