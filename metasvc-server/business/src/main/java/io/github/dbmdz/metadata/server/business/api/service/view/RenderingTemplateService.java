package io.github.dbmdz.metadata.server.business.api.service.view;

import de.digitalcollections.model.view.RenderingTemplate;
import io.github.dbmdz.metadata.server.business.api.service.UniqueObjectService;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ServiceException;
import java.util.List;
import java.util.Locale;

public interface RenderingTemplateService extends UniqueObjectService<RenderingTemplate> {

  List<Locale> getLanguages() throws ServiceException;
}
