package io.github.dbmdz.metadata.server.backend.api.repository.identifiable.entity.geo.location;

import de.digitalcollections.model.identifiable.entity.geo.location.GeoLocation;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.entity.EntityRepository;

/** Repository for GeoLocation persistence handling. */
public interface GeoLocationRepository<G extends GeoLocation> extends EntityRepository<G> {}
