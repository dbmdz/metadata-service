package io.github.dbmdz.metadata.server.business.api.service.identifiable.entity.semantic;

import de.digitalcollections.model.identifiable.Identifier;
import de.digitalcollections.model.identifiable.semantic.Subject;
import io.github.dbmdz.metadata.server.business.api.service.UniqueObjectService;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ServiceException;
import java.util.List;
import java.util.Locale;

public interface SubjectService extends UniqueObjectService<Subject> {

  Subject getByTypeAndIdentifier(String type, Identifier identifier) throws ServiceException;

  /**
   * Return list of languages of all subjects
   *
   * @return list of languages
   */
  List<Locale> getLanguages() throws ServiceException;
}
