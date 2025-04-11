package io.github.dbmdz.metadata.server.business.impl.service.identifiable.entity;

import de.digitalcollections.cudami.model.config.CudamiConfig;
import de.digitalcollections.model.identifiable.entity.Event;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.entity.EventRepository;
import io.github.dbmdz.metadata.server.business.api.service.LocaleService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.IdentifierService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.alias.UrlAliasService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.entity.EventService;
import io.github.dbmdz.metadata.server.config.HookProperties;
import org.springframework.stereotype.Service;

@Service("eventService")
public class EventServiceImpl extends EntityServiceImpl<Event> implements EventService {

  public EventServiceImpl(
      EventRepository repository,
      IdentifierService identifierService,
      UrlAliasService urlAliasService,
      HookProperties hookProperties,
      LocaleService localeService,
      CudamiConfig cudamiConfig) {
    super(
        repository,
        identifierService,
        urlAliasService,
        hookProperties,
        localeService,
        cudamiConfig);
  }
}
