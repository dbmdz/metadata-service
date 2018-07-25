package de.digitalcollections.cudami.server.backend.api.repository.identifiable.resource;

import de.digitalcollections.cudami.server.backend.api.repository.identifiable.IdentifiableRepository;
import de.digitalcollections.model.api.identifiable.resource.Resource;

public interface ResourceRepository<R extends Resource> extends IdentifiableRepository<R> {

}
