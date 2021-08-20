package de.digitalcollections.cudami.server.business.impl.service.alias;

import de.digitalcollections.cudami.server.backend.api.repository.alias.UrlAliasRepository;
import de.digitalcollections.cudami.server.backend.api.repository.exceptions.UrlAliasRepositoryException;
import de.digitalcollections.cudami.server.business.api.service.alias.UrlAliasService;
import de.digitalcollections.cudami.server.business.api.service.exceptions.CudamiServiceException;
import de.digitalcollections.model.alias.LocalizedUrlAliases;
import de.digitalcollections.model.alias.UrlAlias;
import de.digitalcollections.model.paging.SearchPageRequest;
import de.digitalcollections.model.paging.SearchPageResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Service implementation for UrlAlias handling. */
@Service
public class UrlAliasServiceImpl implements UrlAliasService {

  private final UrlAliasRepository repository;

  @Autowired
  public UrlAliasServiceImpl(UrlAliasRepository repository) {
    this.repository = repository;
  }

  @Override
  public UrlAlias findOne(UUID uuid) throws CudamiServiceException {
    if (uuid == null) {
      return null;
    }

    try {
      return repository.findOne(uuid);
    } catch (Exception e) {
      throw new CudamiServiceException("Cannot findOne with uuid=" + uuid + ": " + e, e);
    }
  }

  @Override
  public boolean delete(UUID uuid) throws CudamiServiceException {
    return delete(List.of(uuid));
  }

  @Override
  public boolean delete(List<UUID> uuids) throws CudamiServiceException {
    try {
      return repository.delete(uuids) > 0;
    } catch (UrlAliasRepositoryException e) {
      throw new CudamiServiceException("Cannot delete UrlAliases by uuids: " + e, e);
    }
  }

  @Override
  public UrlAlias create(UrlAlias urlAlias) throws CudamiServiceException {
    if (urlAlias == null) {
      throw new CudamiServiceException("Cannot create an empty UrlAlias");
    }

    if (urlAlias.getUuid() != null) {
      throw new CudamiServiceException("Cannot create an UrlAlias, when its UUID is already set!");
    }

    try {
      return repository.save(urlAlias);
    } catch (Exception e) {
      throw new CudamiServiceException("Cannot save urlAlias: " + e, e);
    }
  }

  @Override
  public UrlAlias update(UrlAlias urlAlias) throws CudamiServiceException {
    if (urlAlias == null) {
      throw new CudamiServiceException("Cannot update an empty UrlAlias");
    }

    if (urlAlias.getUuid() == null) {
      throw new CudamiServiceException("Cannot update an UrlAlias with empty UUID");
    }

    try {
      return repository.update(urlAlias);
    } catch (Exception e) {
      throw new CudamiServiceException("Cannot update urlAlias: " + e, e);
    }
  }

  @Override
  public SearchPageResponse<LocalizedUrlAliases> find(SearchPageRequest searchPageRequest)
      throws CudamiServiceException {
    try {
      return repository.find(searchPageRequest);
    } catch (Exception e) {
      throw new CudamiServiceException(
          "Cannot find LocalizedUrlAliases with searchPageRequest=" + searchPageRequest + ": " + e,
          e);
    }
  }

  @Override
  public LocalizedUrlAliases findLocalizedUrlAliases(UUID uuid) throws CudamiServiceException {
    try {
      return repository.findAllForTarget(uuid);
    } catch (Exception e) {
      throw new CudamiServiceException(
          "Cannot find LocalizedUrlAliases for identifiable with uuid=" + uuid + ": " + e, e);
    }
  }

  @Override
  public LocalizedUrlAliases findMainLink(UUID websiteUuid, String slug)
      throws CudamiServiceException {
    if (websiteUuid == null) {
      throw new CudamiServiceException("Missing websiteUuid");
    }
    if (slug == null || slug.isBlank()) {
      throw new CudamiServiceException("Missing or empty slug");
    }

    try {
      return repository.findMainLinks(websiteUuid, slug);
    } catch (Exception e) {
      throw new CudamiServiceException(
          "Could not find mainLink for websiteUuid=" + websiteUuid + ", slug=" + slug + ": " + e,
          e);
    }
  }
}
