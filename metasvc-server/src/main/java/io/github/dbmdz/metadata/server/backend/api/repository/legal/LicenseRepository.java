package io.github.dbmdz.metadata.server.backend.api.repository.legal;

import de.digitalcollections.model.legal.License;
import io.github.dbmdz.metadata.server.backend.api.repository.UniqueObjectRepository;
import io.github.dbmdz.metadata.server.backend.api.repository.exceptions.RepositoryException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

/** Repository for licences persistence handling. */
public interface LicenseRepository extends UniqueObjectRepository<License> {

  /**
   * Delete a license by url
   *
   * @param url unique url of license
   */
  void deleteByUrl(URL url) throws RepositoryException;

  /**
   * Return license with url
   *
   * @param url the url of the license
   * @return The found license
   */
  License getByUrl(URL url) throws RepositoryException;

  /**
   * Return list of languages of all licenses
   *
   * @return list of languages
   */
  List<Locale> getLanguages() throws RepositoryException;
}
