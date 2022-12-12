package de.digitalcollections.cudami.server.business.api.service.identifiable.versioning;

import de.digitalcollections.cudami.server.business.api.service.exceptions.CudamiServiceException;
import de.digitalcollections.cudami.server.business.api.service.exceptions.ValidationException;
import de.digitalcollections.model.identifiable.Identifiable;
import de.digitalcollections.model.identifiable.versioning.Version;
import java.util.UUID;

public interface VersionService {

  Version create(String instancekey, String instanceVersionkey) throws CudamiServiceException;

  String extractInstanceVersionkey(Identifiable identifiable);

  Version getByInstanceversionKey(String instanceVersionkey);

  Version getByUuid(UUID uuid);

  void save(Version version) throws CudamiServiceException;

  void update(Version version) throws ValidationException, CudamiServiceException;
}
