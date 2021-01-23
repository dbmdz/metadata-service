package de.digitalcollections.cudami.server.backend.impl.jdbi.identifiable.entity;

import de.digitalcollections.cudami.server.backend.api.repository.identifiable.IdentifierRepository;
import de.digitalcollections.cudami.server.backend.api.repository.identifiable.entity.DigitalObjectRepository;
import de.digitalcollections.cudami.server.backend.impl.jdbi.identifiable.entity.work.ItemRepositoryImpl;
import de.digitalcollections.cudami.server.backend.impl.jdbi.identifiable.resource.FileResourceMetadataRepositoryImpl;
import de.digitalcollections.cudami.server.backend.impl.jdbi.identifiable.resource.ImageFileResourceRepositoryImpl;
import de.digitalcollections.model.api.identifiable.Identifier;
import de.digitalcollections.model.api.identifiable.entity.Collection;
import de.digitalcollections.model.api.identifiable.entity.DigitalObject;
import de.digitalcollections.model.api.identifiable.entity.Project;
import de.digitalcollections.model.api.identifiable.entity.work.Item;
import de.digitalcollections.model.api.identifiable.resource.FileResource;
import de.digitalcollections.model.api.identifiable.resource.ImageFileResource;
import de.digitalcollections.model.api.paging.PageRequest;
import de.digitalcollections.model.api.paging.PageResponse;
import de.digitalcollections.model.impl.identifiable.entity.DigitalObjectImpl;
import de.digitalcollections.model.impl.identifiable.resource.FileResourceImpl;
import de.digitalcollections.model.impl.paging.PageResponseImpl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
public class DigitalObjectRepositoryImpl extends EntityRepositoryImpl<DigitalObject>
    implements DigitalObjectRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(DigitalObjectRepositoryImpl.class);

  public static final String MAPPING_PREFIX = "do";
  public static final String TABLE_ALIAS = "d";
  public static final String TABLE_NAME = "digitalobjects";

  public static String getSqlAllFields(String tableAlias, String mappingPrefix) {
    // TODO: add license, version
    //    return getSqlReducedFields(tableAlias, mappingPrefix) + ", "
    //            + tableAlias + ".version " + mappingPrefix + "_version";
    return getSqlReducedFields(tableAlias, mappingPrefix);
  }

  public static String getSqlReducedFields(String tableAlias, String mappingPrefix) {
    return EntityRepositoryImpl.getSqlReducedFields(tableAlias, mappingPrefix);
  }

  @Lazy @Autowired private CollectionRepositoryImpl collectionRepositoryImpl;

  @Lazy @Autowired private FileResourceMetadataRepositoryImpl fileResourceMetadataRepositoryImpl;

  @Lazy @Autowired private ImageFileResourceRepositoryImpl imageFileResourceRepositoryImpl;

  @Lazy @Autowired private ItemRepositoryImpl itemRepositoryImpl;

  @Lazy @Autowired private ProjectRepositoryImpl projectRepositoryImpl;

  @Autowired
  public DigitalObjectRepositoryImpl(Jdbi dbi, IdentifierRepository identifierRepository) {
    super(
        dbi,
        identifierRepository,
        TABLE_NAME,
        TABLE_ALIAS,
        MAPPING_PREFIX,
        DigitalObjectImpl.class);
    this.sqlAllFields = getSqlAllFields(tableAlias, mappingPrefix);
    this.sqlReducedFields = getSqlReducedFields(tableAlias, mappingPrefix);
  }

  @Override
  public void deleteFileResources(UUID digitalObjectUuid) {
    dbi.withHandle(
        h ->
            h.createUpdate(
                    "DELETE FROM digitalobject_fileresources WHERE digitalobject_uuid = :uuid")
                .bind("uuid", digitalObjectUuid)
                .execute());
  }

  @Override
  protected String[] getAllowedOrderByFields() {
    return new String[] {"created", "lastModified", "refId"};
  }

  @Override
  public PageResponse<Collection> getCollections(UUID digitalObjectUuid, PageRequest pageRequest) {
    final String tableAliasCollection = collectionRepositoryImpl.getTableAlias();
    final String tableNameCollection = collectionRepositoryImpl.getTableName();

    String commonSql =
        " FROM "
            + tableNameCollection
            + " AS "
            + tableAliasCollection
            + " LEFT JOIN collection_digitalobjects AS cd ON "
            + tableAliasCollection
            + ".uuid = cd.collection_uuid"
            + " WHERE cd.digitalobject_uuid = :uuid";

    StringBuilder innerQuery = new StringBuilder("SELECT *" + commonSql);
    addFiltering(pageRequest, innerQuery);
    pageRequest.setSorting(null);
    innerQuery.append(" ORDER BY ").append(tableAliasCollection).append(".label ASC");
    addPageRequestParams(pageRequest, innerQuery);

    List<Collection> result =
        collectionRepositoryImpl.retrieveList(
            collectionRepositoryImpl.getSqlReducedFields(),
            innerQuery,
            Map.of("uuid", digitalObjectUuid));

    StringBuilder countQuery = new StringBuilder("SELECT count(*)" + commonSql);
    addFiltering(pageRequest, countQuery);
    long total = retrieveCount(countQuery, Map.of("uuid", digitalObjectUuid));

    return new PageResponseImpl<>(result, pageRequest, total);
  }

  @Override
  protected String getColumnName(String modelProperty) {
    if (modelProperty == null) {
      return null;
    }
    switch (modelProperty) {
      case "created":
        return tableAlias + ".created";
      case "lastModified":
        return tableAlias + ".last_modified";
      case "refId":
        return tableAlias + ".refid";
      default:
        return null;
    }
  }

  @Override
  public List<FileResource> getFileResources(UUID digitalObjectUuid) {
    final String frTableAlias = fileResourceMetadataRepositoryImpl.getTableAlias();
    final String frTableName = fileResourceMetadataRepositoryImpl.getTableName();
    final String fieldsSql = fileResourceMetadataRepositoryImpl.getSqlReducedFields();
    StringBuilder innerQuery =
        new StringBuilder(
            "SELECT * FROM "
                + frTableName
                + " AS "
                + frTableAlias
                + " LEFT JOIN digitalobject_fileresources AS df ON "
                + frTableAlias
                + ".uuid = df.fileresource_uuid"
                + " WHERE df.digitalobject_uuid = :uuid"
                + " ORDER BY df.sortIndex ASC");
    Map<String, Object> argumentMappings = Map.of("uuid", digitalObjectUuid);

    List<FileResource> fileResources =
        fileResourceMetadataRepositoryImpl.retrieveList(fieldsSql, innerQuery, argumentMappings);

    return fileResources;
  }

  @Override
  public List<ImageFileResource> getImageFileResources(UUID digitalObjectUuid) {
    final String frTableAlias = imageFileResourceRepositoryImpl.getTableAlias();
    final String frTableName = imageFileResourceRepositoryImpl.getTableName();
    final String fieldsSql = imageFileResourceRepositoryImpl.getSqlAllFields();
    StringBuilder innerQuery =
        new StringBuilder(
            "SELECT * FROM "
                + frTableName
                + " AS "
                + frTableAlias
                + " LEFT JOIN digitalobject_fileresources AS df ON "
                + frTableAlias
                + ".uuid = df.fileresource_uuid"
                + " WHERE df.digitalobject_uuid = :uuid"
                + " ORDER BY df.sortIndex ASC");
    Map<String, Object> argumentMappings = Map.of("uuid", digitalObjectUuid);

    List<ImageFileResource> fileResources =
        imageFileResourceRepositoryImpl.retrieveList(fieldsSql, innerQuery, argumentMappings);

    return fileResources;
  }

  @Override
  public Item getItem(UUID digitalObjectUuid) {
    final String itTableAlias = itemRepositoryImpl.getTableAlias();
    final String itTableName = itemRepositoryImpl.getTableName();

    StringBuilder innerQuery =
        new StringBuilder(
            "SELECT * FROM "
                + itTableName
                + " AS "
                + itTableAlias
                + " LEFT JOIN item_digitalobjects AS ido ON "
                + itTableAlias
                + ".uuid = ido.item_uuid"
                + " WHERE ido.digitalobject_uuid = :uuid");

    Item result =
        itemRepositoryImpl.retrieveOne(
            itemRepositoryImpl.getSqlReducedFields(),
            innerQuery,
            null,
            Map.of("uuid", digitalObjectUuid));
    return result;
  }

  @Override
  public PageResponse<Project> getProjects(UUID digitalObjectUuid, PageRequest pageRequest) {
    final String prTableAlias = projectRepositoryImpl.getTableAlias();
    final String prTableName = projectRepositoryImpl.getTableName();

    String commonSql =
        " FROM "
            + prTableName
            + " AS "
            + prTableAlias
            + " LEFT JOIN project_digitalobjects AS pd ON "
            + prTableAlias
            + ".uuid = pd.project_uuid"
            + " WHERE pd.digitalobject_uuid = :uuid";

    StringBuilder innerQuery = new StringBuilder("SELECT *" + commonSql);
    addFiltering(pageRequest, innerQuery);
    pageRequest.setSorting(null);
    innerQuery.append(" ORDER BY ").append(prTableAlias).append(".label ASC");
    addPageRequestParams(pageRequest, innerQuery);

    List<Project> result =
        projectRepositoryImpl.retrieveList(
            projectRepositoryImpl.getSqlReducedFields(),
            innerQuery,
            Map.of("uuid", digitalObjectUuid));

    StringBuilder countQuery = new StringBuilder("SELECT count(*)" + commonSql);
    addFiltering(pageRequest, countQuery);
    long total = retrieveCount(countQuery, Map.of("uuid", digitalObjectUuid));

    return new PageResponseImpl<>(result, pageRequest, total);
  }

  @Override
  public DigitalObject save(DigitalObject digitalObject) {
    digitalObject.setUuid(UUID.randomUUID());
    digitalObject.setCreated(LocalDateTime.now());
    digitalObject.setLastModified(LocalDateTime.now());
    // refid is generated as serial, DO NOT SET!
    final UUID previewImageUuid =
        digitalObject.getPreviewImage() == null ? null : digitalObject.getPreviewImage().getUuid();

    String query =
        "INSERT INTO "
            + tableName
            + "("
            + "uuid, label, description, previewFileResource, preview_hints, custom_attrs,"
            + " identifiable_type, entity_type,"
            + " created, last_modified"
            + ") VALUES ("
            + ":uuid, :label::JSONB, :description::JSONB, :previewFileResource, :previewImageRenderingHints::JSONB, :customAttributes::JSONB,"
            + " :type, :entityType,"
            + " :created, :lastModified"
            + ")";

    dbi.withHandle(
        h ->
            h.createUpdate(query)
                .bind("previewFileResource", previewImageUuid)
                .bindBean(digitalObject)
                .execute());

    // for now we implement first interesting use case: new digital object with new fileresources...
    final List<FileResource> fileResources = digitalObject.getFileResources();
    saveFileResources(digitalObject, fileResources);

    // save identifiers
    Set<Identifier> identifiers = digitalObject.getIdentifiers();
    saveIdentifiers(identifiers, digitalObject);

    DigitalObject result = findOne(digitalObject.getUuid());
    return result;
  }

  @Override
  public List<FileResource> saveFileResources(
      UUID digitalObjectUuid, List<FileResource> fileResources) {

    // as we store the whole list new: delete old entries
    dbi.withHandle(
        h ->
            h.createUpdate(
                    "DELETE FROM digitalobject_fileresources WHERE digitalobject_uuid = :uuid")
                .bind("uuid", digitalObjectUuid)
                .execute());

    if (fileResources != null) {
      // first save fileresources
      for (FileResource fileResource : fileResources) {
        if (fileResource.getUuid() == null) {
          fileResourceMetadataRepositoryImpl.save((FileResourceImpl) fileResource);
        }
      }

      // second: save relations to digital object
      dbi.useHandle(
          handle -> {
            PreparedBatch preparedBatch =
                handle.prepareBatch(
                    "INSERT INTO digitalobject_fileresources(digitalobject_uuid, fileresource_uuid, sortIndex) VALUES(:uuid, :fileResourceUuid, :sortIndex)");
            for (FileResource fileResource : fileResources) {
              preparedBatch
                  .bind("uuid", digitalObjectUuid)
                  .bind("fileResourceUuid", fileResource.getUuid())
                  .bind("sortIndex", getIndex(fileResources, fileResource))
                  .add();
            }
            preparedBatch.execute();
          });
    }
    return getFileResources(digitalObjectUuid);
  }

  @Override
  public DigitalObject update(DigitalObject digitalObject) {
    digitalObject.setLastModified(LocalDateTime.now());

    // do not update/left out from statement (not changed since insert):
    // uuid, created, identifiable_type, entity_type, refid
    final UUID previewImageUuid =
        digitalObject.getPreviewImage() == null ? null : digitalObject.getPreviewImage().getUuid();

    String query =
        "UPDATE "
            + tableName
            + " SET"
            + " label=:label::JSONB, description=:description::JSONB,"
            + " previewFileResource=:previewFileResource, preview_hints=:previewImageRenderingHints::JSONB, custom_attrs=:customAttributes::JSONB,"
            + " last_modified=:lastModified"
            + " WHERE uuid=:uuid";

    dbi.withHandle(
        h ->
            h.createUpdate(query)
                .bind("previewFileResource", previewImageUuid)
                .bindBean(digitalObject)
                .execute());

    // save identifiers
    // as we store the whole list new: delete old entries
    identifierRepository.deleteByIdentifiable(digitalObject);
    Set<Identifier> identifiers = digitalObject.getIdentifiers();
    saveIdentifiers(identifiers, digitalObject);

    DigitalObject result = findOne(digitalObject.getUuid());
    return result;
  }
}
