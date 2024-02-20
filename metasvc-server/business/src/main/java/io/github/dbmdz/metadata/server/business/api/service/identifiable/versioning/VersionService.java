package io.github.dbmdz.metadata.server.business.api.service.identifiable.versioning;

import de.digitalcollections.model.identifiable.Identifiable;
import de.digitalcollections.model.identifiable.versioning.Version;
import de.digitalcollections.model.validation.ValidationException;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ServiceException;
import java.util.UUID;

public interface VersionService {

  Version create(String instancekey, String instanceVersionkey) throws ServiceException;

  String extractInstanceVersionkey(Identifiable identifiable);

  Version getByInstanceversionKey(String instanceVersionkey);

  Version getByUuid(UUID uuid);

  void save(Version version) throws ServiceException;

  void update(Version version) throws ValidationException, ServiceException;
}
