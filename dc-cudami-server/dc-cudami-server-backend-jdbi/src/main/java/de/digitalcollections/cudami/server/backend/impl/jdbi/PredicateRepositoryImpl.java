package de.digitalcollections.cudami.server.backend.impl.jdbi;

import de.digitalcollections.cudami.server.backend.api.repository.PredicateRepository;
import de.digitalcollections.model.api.relations.Predicate;
import de.digitalcollections.model.impl.relations.PredicateImpl;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.result.RowView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PredicateRepositoryImpl extends AbstractPagingAndSortingRepositoryImpl
    implements PredicateRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(PredicateRepositoryImpl.class);

  public static final String SQL_REDUCED_PREDICATE_FIELDS_PRED =
      " p.value pred_value, p.label pred_label,"
          + " p.created pred_created, p.last_modified pred_lastModified";

  public static final String SQL_FULL_PREDICATE_FIELDS_PRED =
      SQL_REDUCED_PREDICATE_FIELDS_PRED + " , p.description pred_description";

  public static BiFunction<LinkedHashMap<UUID, Predicate>, RowView, LinkedHashMap<UUID, Predicate>>
      mapRowToPredicate(boolean withIdentifiers, boolean withPreviewImage) {
    return (map, rowView) -> {
      Predicate predicate =
          map.computeIfAbsent(
              rowView.getColumn("pred_uuid", UUID.class),
              fn -> {
                return rowView.getRow(PredicateImpl.class);
              });
      return map;
    };
  }

  private final Jdbi dbi;

  @Autowired
  public PredicateRepositoryImpl(Jdbi dbi) {
    this.dbi = dbi;
  }

  @Override
  public List<Predicate> findAll() {
    final String sql = "SELECT * FROM predicates AS p";

    List<Predicate> result =
        dbi.withHandle(
            h ->
                h.createQuery(sql).mapToBean(PredicateImpl.class).stream()
                    .map(Predicate.class::cast)
                    .collect(Collectors.toList()));
    return result;
  }

  @Override
  public Predicate findOneByValue(String value) {
    String query = "SELECT * FROM predicates WHERE value = :value";
    Optional<Predicate> result =
        dbi.withHandle(
            h ->
                h.createQuery(query).bind("value", value).mapToBean(PredicateImpl.class).stream()
                    .map(Predicate.class::cast)
                    .findFirst());
    return result.orElse(null);
  }

  @Override
  protected String[] getAllowedOrderByFields() {
    return new String[] {"created", "label", "lastModified", "value"};
  }

  @Override
  protected String getColumnName(String modelProperty) {
    if (modelProperty == null) {
      return null;
    }
    switch (modelProperty) {
      case "created":
        return "p.created";
      case "label":
        return "p.label";
      case "lastModified":
        return "p.last_modified";
      case "value":
        return "p.value";
      default:
        return null;
    }
  }

  @Override
  public Predicate save(Predicate predicate) {
    Predicate existingPredicate = findOneByValue(predicate.getValue());
    if (existingPredicate != null) {
      predicate.setCreated(existingPredicate.getCreated());
    }

    predicate.setLastModified(LocalDateTime.now());

    if (existingPredicate != null) {
      // Update
      String updateQuery =
          "UPDATE predicates SET"
              + " label=:label::JSONB, description=:description::JSONB,"
              + " last_modified=:lastModified"
              + " WHERE value=:value";

      dbi.withHandle(h -> h.createUpdate(updateQuery).bindBean(predicate).execute());
    } else {
      // Creation
      predicate.setCreated(predicate.getLastModified());

      String createQuery =
          "INSERT INTO predicates("
              + "value, label, description,"
              + " created, last_modified"
              + ") VALUES ("
              + ":value, :label::JSONB, :description::JSONB,"
              + " :created, :lastModified"
              + ")";

      dbi.withHandle(h -> h.createUpdate(createQuery).bindBean(predicate).execute());
    }

    return findOneByValue(predicate.getValue());
  }
}