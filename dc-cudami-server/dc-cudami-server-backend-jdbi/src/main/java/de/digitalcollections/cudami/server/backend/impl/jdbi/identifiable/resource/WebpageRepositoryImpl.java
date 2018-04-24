package de.digitalcollections.cudami.server.backend.impl.jdbi.identifiable.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.digitalcollections.core.model.api.paging.PageRequest;
import de.digitalcollections.core.model.api.paging.PageResponse;
import de.digitalcollections.core.model.impl.paging.PageResponseImpl;
import de.digitalcollections.cudami.model.api.identifiable.parts.MultilanguageDocument;
import de.digitalcollections.cudami.model.api.identifiable.resource.Webpage;
import de.digitalcollections.cudami.model.impl.identifiable.parts.MultilanguageDocumentImpl;
import de.digitalcollections.cudami.model.impl.identifiable.parts.TextImpl;
import de.digitalcollections.cudami.model.impl.identifiable.resource.WebpageImpl;
import de.digitalcollections.cudami.server.backend.api.repository.LocaleRepository;
import de.digitalcollections.cudami.server.backend.api.repository.identifiable.resource.ResourceRepository;
import de.digitalcollections.cudami.server.backend.api.repository.identifiable.resource.WebpageRepository;
import de.digitalcollections.cudami.server.backend.impl.jdbi.AbstractPagingAndSortingRepositoryImpl;
import de.digitalcollections.prosemirror.model.api.Document;
import de.digitalcollections.prosemirror.model.impl.DocumentImpl;
import de.digitalcollections.prosemirror.model.impl.contentblocks.ParagraphImpl;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class WebpageRepositoryImpl extends AbstractPagingAndSortingRepositoryImpl implements WebpageRepository<Webpage> {

  private static final Logger LOGGER = LoggerFactory.getLogger(WebpageRepositoryImpl.class);

  @Autowired
  private Jdbi dbi;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  private ResourceRepository resourceRepository;

  @Autowired
  LocaleRepository localeRepository;

  @Override
  public long count() {
    String sql = "SELECT count(*) FROM webpages";
    long count = dbi.withHandle(h -> h.createQuery(sql).mapTo(Long.class).findOnly());
    return count;
  }

  @Override
  public Webpage create() {
    Locale defaultLocale = localeRepository.getDefault();
    WebpageImpl webpage = new WebpageImpl();
    webpage.setLabel(new TextImpl(defaultLocale, ""));
    webpage.setDescription(createEmptyMLD(defaultLocale));
    webpage.setText(createEmptyMLD(defaultLocale));
    return webpage;
  }

  private MultilanguageDocument createEmptyMLD(Locale defaultLocale) {
    MultilanguageDocument emptyMLD = new MultilanguageDocumentImpl();
    Document document = new DocumentImpl();
    document.addContentBlock(new ParagraphImpl());
    emptyMLD.addDocument(defaultLocale, document);
    return emptyMLD;
  }

  @Override
  public PageResponse<Webpage> find(PageRequest pageRequest) {
    StringBuilder query = new StringBuilder("SELECT wp.uuid as uuid, wp.text as text, i.label as label, i.description as description")
            .append(" FROM webpages wp INNER JOIN resources r ON wp.uuid=r.uuid INNER JOIN identifiables i ON wp.uuid=i.uuid");

    addPageRequestParams(pageRequest, query);

//    List<Map<String, Object>> list = dbi.withHandle(h -> h.createQuery(query.toString()).mapToMap().list());
    List<WebpageImpl> result = dbi.withHandle(h -> h.createQuery(query.toString())
            .mapToBean(WebpageImpl.class)
            .list());
    long total = count();
    PageResponse pageResponse = new PageResponseImpl(result, pageRequest, total);
//    PageResponse pageResponse = new PageResponseImpl(null, pageRequest, total);
    return pageResponse;
  }

  @Override
  public Webpage findOne(UUID uuid) {
    String query = "SELECT wp.uuid as uuid, wp.text as text, i.label as label, i.description as description"
            + " FROM webpages wp INNER JOIN resources r ON wp.uuid=r.uuid INNER JOIN identifiables i ON wp.uuid=i.uuid"
            + " WHERE wp.uuid = :uuid";

    List<WebpageImpl> list = dbi.withHandle(h -> h.createQuery(query)
            .bind("uuid", uuid)
            .mapToBean(WebpageImpl.class)
            .list());
    if (list.isEmpty()) {
      return null;
    }
    return list.get(0);
  }

  @Override
  protected String[] getAllowedOrderByFields() {
    return new String[]{"uuid"};
  }

  @Override
  public Webpage save(Webpage webpage) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Webpage save(Webpage webpage, UUID websiteUuid) {
    resourceRepository.save(webpage);

    dbi.withHandle(h -> h.createUpdate("INSERT INTO webpages(uuid, text) VALUES (:uuid, :text::JSONB)")
            .bindBean(webpage)
            .execute());

    dbi.withHandle(h -> {
      return h.createUpdate("INSERT INTO website_webpage(website_uuid, webpage_uuid) VALUES (:website_uuid, :uuid)")
              .bind("website_uuid", websiteUuid)
              .bindBean(webpage)
              .execute();
    });

    return findOne(webpage.getUuid());
  }

  @Override
  public Webpage update(Webpage webpage) {
    resourceRepository.update(webpage);
    dbi.withHandle(h -> h.createUpdate("UPDATE webpages SET text=:text::JSONB WHERE uuid=:uuid")
            .bindBean(webpage)
            .execute());
    return findOne(webpage.getUuid());
  }
}
