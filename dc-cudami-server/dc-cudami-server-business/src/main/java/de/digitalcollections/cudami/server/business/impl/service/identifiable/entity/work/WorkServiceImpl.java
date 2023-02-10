package de.digitalcollections.cudami.server.business.impl.service.identifiable.entity.work;

import de.digitalcollections.cudami.model.config.CudamiConfig;
import de.digitalcollections.cudami.server.backend.api.repository.identifiable.entity.work.WorkRepository;
import de.digitalcollections.cudami.server.business.api.service.LocaleService;
import de.digitalcollections.cudami.server.business.api.service.exceptions.ServiceException;
import de.digitalcollections.cudami.server.business.api.service.exceptions.ValidationException;
import de.digitalcollections.cudami.server.business.api.service.identifiable.IdentifierService;
import de.digitalcollections.cudami.server.business.api.service.identifiable.alias.UrlAliasService;
import de.digitalcollections.cudami.server.business.api.service.identifiable.entity.relation.EntityRelationService;
import de.digitalcollections.cudami.server.business.api.service.identifiable.entity.work.WorkService;
import de.digitalcollections.cudami.server.business.impl.service.identifiable.entity.EntityServiceImpl;
import de.digitalcollections.cudami.server.config.HookProperties;
import de.digitalcollections.model.identifiable.entity.relation.EntityRelation;
import de.digitalcollections.model.identifiable.entity.work.Work;
import de.digitalcollections.model.list.paging.PageRequest;
import de.digitalcollections.model.list.paging.PageResponse;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

// @Transactional should not be set in derived class to prevent overriding, check base class instead
@Service
public class WorkServiceImpl extends EntityServiceImpl<Work> implements WorkService {

  private static final Logger LOGGER = LoggerFactory.getLogger(WorkServiceImpl.class);
  private final EntityRelationService entityRelationService;

  public WorkServiceImpl(
      @Qualifier("workRepository") WorkRepository repository,
      IdentifierService identifierService,
      UrlAliasService urlAliasService,
      HookProperties hookProperties,
      LocaleService localeService,
      EntityRelationService entityRelationService,
      CudamiConfig cudamiConfig) {
    super(
        repository,
        identifierService,
        urlAliasService,
        hookProperties,
        localeService,
        cudamiConfig);
    this.entityRelationService = entityRelationService;
  }

  @Override
  public PageResponse<Work> findEmbedded(UUID uuid, PageRequest pageRequest) {
    return ((WorkRepository) repository).findEmbeddedWorks(uuid, pageRequest);
  }

  @Override
  public Work getForItem(UUID itemUuid) {
    return ((WorkRepository) repository).getByItemUuid(itemUuid);
  }

  @Override
  public Set<Work> getForPerson(UUID personUuid) {
    return ((WorkRepository) repository).getByPersonUuid(personUuid);
  }

  @Override
  public void save(Work work) throws ServiceException, ValidationException {
    super.save(work);
    try {
      List<EntityRelation> entityRelations = work.getRelations();
      Work workWithUuidOnly = Work.builder().uuid(work.getUuid()).build();
      entityRelationService.persistEntityRelations(work, entityRelations, true, workWithUuidOnly);
      work.setRelations(entityRelations);
    } catch (ServiceException e) {
      throw new ServiceException("Cannot save Work=" + work + ": " + e, e);
    }
  }

  @Override
  public void update(Work work) throws ServiceException, ValidationException {
    super.update(work);
    try {
      List<EntityRelation> entityRelations = work.getRelations();
      Work workWithUuidOnly = Work.builder().uuid(work.getUuid()).build();
      entityRelationService.persistEntityRelations(work, entityRelations, false, workWithUuidOnly);
      work.setRelations(entityRelations);
    } catch (ServiceException e) {
      throw new ServiceException("Cannot update Work=" + work + ": " + e, e);
    }
  }
}
