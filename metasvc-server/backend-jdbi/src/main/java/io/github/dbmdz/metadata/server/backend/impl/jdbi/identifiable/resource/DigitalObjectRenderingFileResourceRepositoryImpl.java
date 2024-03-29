package io.github.dbmdz.metadata.server.backend.impl.jdbi.identifiable.resource;

import de.digitalcollections.cudami.model.config.CudamiConfig;
import de.digitalcollections.model.identifiable.resource.FileResource;
import de.digitalcollections.model.identifiable.resource.FileResourceType;
import de.digitalcollections.model.list.paging.PageRequest;
import de.digitalcollections.model.list.paging.PageResponse;
import io.github.dbmdz.metadata.server.backend.api.repository.exceptions.RepositoryException;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.resource.DigitalObjectRenderingFileResourceRepository;
import io.github.dbmdz.metadata.server.backend.impl.jdbi.JdbiRepositoryImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;
import org.springframework.stereotype.Repository;

@Repository
public class DigitalObjectRenderingFileResourceRepositoryImpl extends JdbiRepositoryImpl
    implements DigitalObjectRenderingFileResourceRepository {

  public static final String MAPPING_PREFIX = "dorr";
  public static final String TABLE_ALIAS = "do_rr";
  public static final String TABLE_NAME = "digitalobject_renderingresources";

  private final FileResourceMetadataRepositoryImpl<FileResource> fileResourceMetadataRepositoryImpl;

  public DigitalObjectRenderingFileResourceRepositoryImpl(
      Jdbi dbi,
      CudamiConfig cudamiConfig,
      FileResourceMetadataRepositoryImpl<FileResource> fileResourceMetadataRepositoryImpl) {
    super(
        dbi, TABLE_NAME, TABLE_ALIAS, MAPPING_PREFIX, cudamiConfig.getOffsetForAlternativePaging());
    this.fileResourceMetadataRepositoryImpl = fileResourceMetadataRepositoryImpl;
  }

  @Override
  public int countDigitalObjectsForResource(UUID uuid) {
    return dbi.withHandle(
        h ->
            h.createQuery("SELECT count(*) FROM " + tableName + " WHERE fileresource_uuid = :uuid")
                .bind("uuid", uuid)
                .mapTo(Integer.class)
                .findOne()
                .get());
  }

  @Override
  public int delete(List<UUID> uuids) {
    return dbi.withHandle(
        h ->
            h.createUpdate("DELETE FROM " + tableName + " WHERE fileresource_uuid in (<uuids>)")
                .bindList("uuids", uuids)
                .execute());
  }

  /**
   * Sets the {@link FileResource#getFileResourceType()} of the passed {@code FileResource}
   * depending on the MIME type.
   *
   * @param untypedFileResource the object that the {@code fileResourceType} should be set of
   * @return the passed object itself (for use in e.g. {@link
   *     Stream#map(java.util.function.Function)})
   */
  public static FileResource fillResourceType(FileResource untypedFileResource) {
    switch (untypedFileResource.getMimeType().getPrimaryType()) {
      case "application":
        untypedFileResource.setFileResourceType(FileResourceType.APPLICATION);
        break;
      case "audio":
        untypedFileResource.setFileResourceType(FileResourceType.AUDIO);
        break;
      case "image":
        untypedFileResource.setFileResourceType(FileResourceType.IMAGE);
        break;
      case "text":
        untypedFileResource.setFileResourceType(FileResourceType.TEXT);
        break;
      case "video":
        untypedFileResource.setFileResourceType(FileResourceType.VIDEO);
        break;
      default:
        // nop
    }
    return untypedFileResource;
  }

  @Override
  public PageResponse<FileResource> findRenderingFileResources(
      UUID digitalObjectUuid, PageRequest pageRequest) {
    throw new UnsupportedOperationException(); // TODO: not yet implemented
  }

  @Override
  protected List<String> getAllowedOrderByFields() {
    return new ArrayList<>(Arrays.asList("digitalobject_uuid", "fileresource_uuid", "sortIndex"));
  }

  @Override
  public String getColumnName(String modelProperty) {
    return null;
  }

  @Override
  public List<FileResource> getRenderingFileResources(UUID digitalObjectUuid)
      throws RepositoryException {
    final String fieldsSql = fileResourceMetadataRepositoryImpl.getSqlSelectAllFields();

    StringBuilder innerQuery =
        new StringBuilder(
            "SELECT "
                + getTableAlias()
                + ".sortindex as idx, *"
                + " FROM fileresources AS f"
                + " INNER JOIN "
                + getTableName()
                + " AS "
                + getTableAlias()
                + " ON f.uuid = "
                + getTableAlias()
                + ".fileresource_uuid"
                + " WHERE "
                + getTableAlias()
                + ".digitalobject_uuid = :uuid"
                + " ORDER by "
                + getTableAlias()
                + ".sortindex ASC");
    Map<String, Object> argumentMappings = new HashMap<>(0);
    argumentMappings.put("uuid", digitalObjectUuid);

    return fileResourceMetadataRepositoryImpl
        .retrieveList(fieldsSql, innerQuery, argumentMappings, "ORDER BY idx ASC")
        .stream()
        .map(DigitalObjectRenderingFileResourceRepositoryImpl::fillResourceType)
        .collect(Collectors.toList());
  }

  @Override
  protected String getUniqueField() {
    return null;
  }

  @Override
  public int removeByDigitalObject(UUID digitalObjectUuid) {
    return dbi.withHandle(
        h ->
            h.createUpdate("DELETE FROM " + getTableName() + " WHERE digitalobject_uuid = :uuid")
                .bind("uuid", digitalObjectUuid)
                .execute());
  }

  @Override
  public void setRenderingFileResources(
      UUID digitalObjectUuid, List<FileResource> renderingResources) throws RepositoryException {
    dbi.useHandle(
        handle -> {
          PreparedBatch preparedBatch =
              handle.prepareBatch(
                  "INSERT INTO "
                      + getTableName()
                      + "(digitalobject_uuid, fileresource_uuid, sortIndex) VALUES(:uuid, :fileResourceUuid, :sortIndex)");
          for (FileResource renderingResource : renderingResources) {
            preparedBatch
                .bind("uuid", digitalObjectUuid)
                .bind("fileResourceUuid", renderingResource.getUuid())
                .bind(
                    "sortIndex",
                    fileResourceMetadataRepositoryImpl.getIndex(
                        renderingResources, renderingResource))
                .add();
          }
          preparedBatch.execute();
        });
  }

  @Override
  protected boolean supportsCaseSensitivityForProperty(String modelProperty) {
    return false;
  }
}
