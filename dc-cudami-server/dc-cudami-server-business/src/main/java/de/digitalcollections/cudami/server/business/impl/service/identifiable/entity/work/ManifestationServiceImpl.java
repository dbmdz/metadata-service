package de.digitalcollections.cudami.server.business.impl.service.identifiable.entity.work;

import de.digitalcollections.cudami.model.config.CudamiConfig;
import de.digitalcollections.cudami.server.backend.api.repository.identifiable.entity.work.ManifestationRepository;
import de.digitalcollections.cudami.server.business.api.service.LocaleService;
import de.digitalcollections.cudami.server.business.api.service.exceptions.CudamiServiceException;
import de.digitalcollections.cudami.server.business.api.service.exceptions.IdentifiableServiceException;
import de.digitalcollections.cudami.server.business.api.service.exceptions.ServiceException;
import de.digitalcollections.cudami.server.business.api.service.exceptions.ValidationException;
import de.digitalcollections.cudami.server.business.api.service.identifiable.IdentifierService;
import de.digitalcollections.cudami.server.business.api.service.identifiable.alias.UrlAliasService;
import de.digitalcollections.cudami.server.business.api.service.identifiable.entity.relation.EntityRelationService;
import de.digitalcollections.cudami.server.business.api.service.identifiable.entity.work.ManifestationService;
import de.digitalcollections.cudami.server.business.impl.service.identifiable.entity.EntityServiceImpl;
import de.digitalcollections.cudami.server.config.HookProperties;
import de.digitalcollections.model.identifiable.Identifier;
import de.digitalcollections.model.identifiable.entity.manifestation.Manifestation;
import de.digitalcollections.model.identifiable.entity.relation.EntityRelation;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ManifestationServiceImpl extends EntityServiceImpl<Manifestation>
    implements ManifestationService {

  private EntityRelationService entityRelationService;

  public ManifestationServiceImpl(
      ManifestationRepository repository,
      IdentifierService identifierService,
      UrlAliasService urlAliasService,
      HookProperties hookProperties,
      LocaleService localeService,
      EntityRelationService entityRealationService,
      CudamiConfig cudamiConfig) {
    super(
        repository,
        identifierService,
        urlAliasService,
        hookProperties,
        localeService,
        cudamiConfig);
    this.entityRelationService = entityRealationService;
  }

  @Override
  public Manifestation getByUuid(UUID uuid) throws IdentifiableServiceException {
    Manifestation manifestation = super.getByUuid(uuid);
    return manifestation;
  }

  @Override
  public Manifestation getByIdentifier(Identifier identifier) {
    // TODO Auto-generated method stub
    return super.getByIdentifier(identifier);
  }

  @Override
  public Manifestation getByRefId(long refId) {
    // TODO Auto-generated method stub
    return super.getByRefId(refId);
  }

  @Override
  public void save(Manifestation manifestation) throws ServiceException, ValidationException {
    super.save(manifestation);
    try {
      persistEntityRelations(manifestation, true);
    } catch (CudamiServiceException e) {
      throw new ServiceException("Cannot save Manifestation=" + manifestation + ": " + e, e);
    }
  }

  @Override
  public void update(Manifestation manifestation) throws ServiceException, ValidationException {
    super.update(manifestation);
    try {
      persistEntityRelations(manifestation, false);
    } catch (CudamiServiceException e) {
      throw new ServiceException("Cannot update Manifestation=" + manifestation + ": " + e, e);
    }
  }

  private void persistEntityRelations(Manifestation manifestation, boolean deleteExisting)
      throws CudamiServiceException {
    if (deleteExisting) {
      // Check, if there are already persisted EntityRelations for the manifestation
      // If yes, delete them
      entityRelationService.deleteByObject(manifestation);
    }

    // save all entity relations and set the UUID of the object
    List<EntityRelation> relations =
        manifestation.getRelations().stream()
            .map(
                r -> {
                  r.setObject(manifestation);
                  return r;
                })
            .collect(Collectors.toList());
    entityRelationService.save(relations);
    manifestation.setRelations(relations);
  }
}
