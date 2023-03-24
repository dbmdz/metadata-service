package de.digitalcollections.cudami.server.backend.impl.jdbi;

import de.digitalcollections.cudami.server.backend.impl.database.AbstractPagingSortingFilteringRepositoryImpl;
import de.digitalcollections.cudami.server.backend.impl.jdbi.identifiable.SearchTermTemplates;
import de.digitalcollections.model.UniqueObject;
import de.digitalcollections.model.list.filtering.FilterCriterion;
import de.digitalcollections.model.list.filtering.FilterOperation;
import de.digitalcollections.model.list.filtering.Filtering;
import de.digitalcollections.model.list.paging.PageRequest;
import de.digitalcollections.model.list.paging.PageResponse;
import de.digitalcollections.model.text.LocalizedText;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JdbiRepositoryImpl<U extends UniqueObject>
    extends AbstractPagingSortingFilteringRepositoryImpl {

  private static final String KEY_PREFIX_FILTERVALUE = "filtervalue_";
  private static final Logger LOGGER = LoggerFactory.getLogger(JdbiRepositoryImpl.class);

  private static String getArrayTypeAndFillArgumentMappings(
      Map<String, Object> argumentMappings, String criterionKey, Collection<?> valueCollection) {
    String arrayType = "varchar[]";
    Object valueSample = valueCollection.stream().findFirst().get();

    if (valueSample instanceof UUID) {
      argumentMappings.put(criterionKey, valueCollection.stream().toArray(UUID[]::new));
      arrayType = "UUID[]";
    } else if (valueSample instanceof String) {
      argumentMappings.put(criterionKey, valueCollection.stream().toArray(String[]::new));
      arrayType = "varchar[]";
    } else if (valueSample instanceof Integer) {
      argumentMappings.put(criterionKey, valueCollection.stream().toArray(Integer[]::new));
      arrayType = "int[]";
    } else if (valueSample instanceof Long) {
      argumentMappings.put(criterionKey, valueCollection.stream().toArray(Long[]::new));
      arrayType = "long[]";
    }
    return arrayType;
  }

  protected final Jdbi dbi;
  protected final String mappingPrefix;
  protected final String tableAlias;

  protected final String tableName;

  public JdbiRepositoryImpl(
      Jdbi dbi,
      String tableName,
      String tableAlias,
      String mappingPrefix,
      int offsetForAlternativePaging) {
    this.dbi = dbi;
    this.mappingPrefix = mappingPrefix;
    this.tableName = tableName;
    this.tableAlias = tableAlias;
    this.offsetForAlternativePaging = offsetForAlternativePaging;
  }

  public void addFiltering(
      Filtering filtering, StringBuilder sqlQuery, Map<String, Object> argumentMappings) {
    if (filtering != null) {
      // handle optional filtering params
      String whereClauses = getWhereClauses(filtering, argumentMappings);
      if (!whereClauses.isEmpty()) {
        String sqlQueryStr = sqlQuery.toString();
        if (sqlQueryStr.toUpperCase().contains(" WHERE ")) {
          sqlQuery.append(" AND ");
        } else {
          sqlQuery.append(" WHERE ");
        }
        sqlQuery.append(whereClauses);
      }
    }
  }

  // FIXME: delete
  // protected String addSearchTerm(
  // PageRequest pageRequest, StringBuilder innerQuery, Map<String, Object>
  // argumentMappings) {
  // // handle search term
  // String searchTerm = pageRequest.getSearchTerm();
  // String executedSearchTerm = null;
  // String commonSearchSql = getCommonSearchSql(tableAlias, searchTerm);
  // if (StringUtils.hasText(commonSearchSql) && StringUtils.hasText(searchTerm))
  // {
  // String commonSql = innerQuery.toString();
  // if (commonSql.toUpperCase().contains(" WHERE ")
  // || commonSql.toUpperCase().contains(" WHERE(")) {
  // innerQuery.append(" AND ");
  // } else {
  // innerQuery.append(" WHERE ");
  // }
  // // select with search term
  // innerQuery.append(commonSearchSql);
  // executedSearchTerm = addSearchTermMappings(searchTerm, argumentMappings);
  // }
  // return executedSearchTerm;
  // }

  public void addFiltering(
      PageRequest pageRequest, StringBuilder sqlQuery, Map<String, Object> argumentMappings) {
    if (pageRequest != null) {
      addFiltering(pageRequest.getFiltering(), sqlQuery, argumentMappings);
    }
  }

  /**
   * Add the search term to the argument map. By overriding this method custom modifications can be
   * made. Belongs to {@link #getSearchTermTemplates(String, String)} and {@link
   * #addSearchTerm(PageRequest, StringBuilder, Map)}.
   *
   * @param searchTerm original term from the {@code PageRequest}
   * @param argumentMappings
   * @return the search term that should be used for the {@link
   *     PageResponse#setExecutedSearchTerm(String)}
   */
  // FIXME: delete
  //  protected String addSearchTermMappings(String searchTerm, Map<String, Object>
  // argumentMappings) {
  //    String executedSearchTerm = escapeTermForJsonpath(searchTerm);
  //    argumentMappings.put("searchTerm", executedSearchTerm);
  //    return executedSearchTerm;
  //  }

  public long count() {
    final String sql = "SELECT count(*) FROM " + tableName;
    long count = dbi.withHandle(h -> h.createQuery(sql).mapTo(Long.class).findOne().get());
    return count;
  }

  public long count(String commonSql, Map<String, Object> argumentMappings) {
    final String sql = "SELECT count(*) " + commonSql;
    return dbi.withHandle(
        h -> h.createQuery(sql).bindMap(argumentMappings).mapTo(Long.class).findOne().get());
  }

  /**
   * Escape characters that must not appear in jsonpath inner strings.
   *
   * <p>This method should always be used to clean up strings, e.g. search terms, that are intended
   * to appear in an jsonpath inner string, i.e. between double quotes. If the inserted term
   * contains double quotes then the jsonpath breaks. Hence we remove double quotes at start and end
   * of the provided string (they do not have any meaning for the search at all) and escape the
   * remaining ones with a backslash.
   *
   * @param term can be null
   * @return term with forbidden characters removed or escaped
   */
  protected final String escapeTermForJsonpath(String term) {
    if (term == null) {
      return null;
    }
    if (term.startsWith("\"") && term.endsWith("\"")) {
      // 1st step: remove useless surrounding quotes
      term = term.replaceAll("^\"(.+)\"$", "$1");
    }
    if (term.contains("\"")) {
      // 2nd step: escape remaining double quotes; yes, looks ugly...
      term = term.replaceAll("\"", "\\\\\"");
    }
    return term;
  }

  protected UUID[] extractUuids(Collection<? extends UniqueObject> uniqueObjects) {
    if (uniqueObjects == null || uniqueObjects.isEmpty()) {
      return new UUID[0];
    }
    return uniqueObjects.stream()
        .collect(
            ArrayList<UUID>::new,
            (result, uniqueObject) -> result.add(uniqueObject.getUuid()),
            ArrayList::addAll)
        .toArray(new UUID[1]);
  }

  protected void filterByLocalizedTextFields(
      PageRequest pageRequest,
      PageResponse<U> pageResponse,
      LinkedHashMap<String, Function<U, Optional<Object>>> jsonbFields) {
    if (!pageRequest.hasFiltering()) {
      return;
    }

    // FIXME: modifying pageResponse afterwards should not only change current page
    // but all pages !
    // seems to be not possible, as other pages base on SQL filtering.... rethink
    // the whole thing...
    for (Map.Entry<String, Function<U, Optional<Object>>> entry : jsonbFields.entrySet()) {
      String fieldName = entry.getKey();
      Function<U, Optional<Object>> retrieveFieldFunction = entry.getValue();

      FilterCriterion filterCriterion =
          pageRequest.getFiltering().getFilterCriteria().stream()
              .filter(fc -> fc.getExpression().startsWith(fieldName))
              .findAny()
              .orElse(null);
      if (filterCriterion != null) {
        filterBySplitField(pageResponse, filterCriterion, retrieveFieldFunction);
        // only one filtering supported (first in order of added rules):
        return;
        // TODO: what happens if all entries have been removed by the filter?
      }
    }
  }

  /**
   * Special logic to filter by label, optionally paying attention to the language. The passed
   * {@code PageResponse} could be modified.
   *
   * @param pageResponse the response from the repo, must always contain the request too (if
   *     everything goes right)
   */
  protected void filterBySplitField(
      PageResponse<U> pageResponse,
      FilterCriterion<String> filter,
      Function<U, Optional<Object>> retrieveField) {
    if (!pageResponse.hasContent()) {
      return;
    }
    // we must differentiate several cases
    if (filter.getOperation() == FilterOperation.EQUALS) {
      // everything has been done by repo already
      return;
    }

    // for CONTAINS the language, if any, has not been taken into account yet
    Matcher matchLanguage = Pattern.compile("\\.([\\w_-]+)$").matcher(filter.getExpression());
    if (matchLanguage.find()) {
      // there is a language...
      Locale language = Locale.forLanguageTag(matchLanguage.group(1));
      List<String> searchTerms = Arrays.asList(splitToArray((String) filter.getValue()));
      List<U> filteredContent =
          pageResponse.getContent().parallelStream()
              .filter(
                  uniqueObject -> {
                    Optional<Object> objOpt = retrieveField.apply(uniqueObject);
                    if (objOpt.isPresent()) {
                      Object obj = objOpt.get();
                      if (obj instanceof LocalizedText) {
                        String text = ((LocalizedText) obj).get(language);
                        if (text == null) {
                          return false;
                        }
                        List<String> splitText = Arrays.asList(splitToArray(text));
                        return splitText.containsAll(searchTerms);
                      }
                    }
                    return false;
                  })
              .collect(Collectors.toList());
      // fix total elements count roughly
      pageResponse.setTotalElements(
          pageResponse.getTotalElements()
              - (pageResponse.getContent().size() - filteredContent.size()));
      pageResponse.setContent(filteredContent);
    }
  }

  /**
   * Map expression to target SQL expression.
   *
   * @param fc filter criterion containing given expression
   * @return target SQL expression used for one operand in WHERE clause
   * @throws IllegalArgumentException
   */
  protected String getTargetExpression(FilterCriterion<?> fc) throws IllegalArgumentException {
    // map expression to column name:
    String givenExpression = fc.getExpression(); // e.g. "label_de"
    String columnName;
    if (fc.isNativeExpression()) {
      // safe (not created using user input)
      columnName = givenExpression;
      String tableAlias = getTableAlias();
      if (tableAlias != null) {
        columnName = getTableAlias() + "." + columnName;
      }
    } else {
      String basicExpression = givenExpression;
      if (givenExpression.contains("_")) {
        basicExpression = givenExpression.split("_")[0]; // e.g. "label"
      }
      // may be created using user input: map expression to column name
      columnName = getColumnName(basicExpression); // e.g. tableAlias + ".label"
      if (columnName == null) {
        throw new IllegalArgumentException(
            String.format(
                "Given expression '%s' is invalid / can not be mapped to column name.",
                givenExpression));
      }

      // FIXME: columname = tableAlias + ".split_label" if split_label column
      // exists...
      // and contains/equals operations are a search in an array?
    }
    return columnName;
  }

  public String getCommonSearchSql(String tblAlias, String originalSearchTerm) {
    List<String> searchTermTemplates = getSearchTermTemplates(tblAlias, originalSearchTerm);
    return searchTermTemplates.isEmpty()
        ? ""
        : "(" + searchTermTemplates.stream().collect(Collectors.joining(" OR ")) + ")";
  }

  /**
   * @return map containing name of jsonb field and function to get the field value
   */
  protected LinkedHashMap<String, Function<U, Optional<Object>>> getJsonbFields() {
    LinkedHashMap<String, Function<U, Optional<Object>>> jsonbFields = new LinkedHashMap<>();
    return jsonbFields;
  }

  public String getMappingPrefix() {
    return mappingPrefix;
  }

  protected List<String> getSearchTermTemplates(String tableAlias, String originalSearchTerm) {
    return new ArrayList<>(0);
  }

  public String getTableAlias() {
    return tableAlias;
  }

  public String getTableName() {
    return tableName;
  }

  protected String getWhereClause(
      FilterCriterion<?> fc, Map<String, Object> argumentMappings, int criterionCount)
      throws IllegalArgumentException, UnsupportedOperationException {
    Set<String> jsonbFields = getJsonbFields().keySet();
    if (!jsonbFields.isEmpty()) {
      String expression = fc.getExpression();
      String basicExpression = expression;
      if (!fc.isNativeExpression() && expression.contains("_")) {
        basicExpression = expression.split("_")[0];
      }

      if (jsonbFields.contains(basicExpression)) {
        // JSONB field handling:
        if (!(fc.getValue() instanceof String)) {
          throw new IllegalArgumentException("Value of JSONB field expression must be a string!");
        }
        String value = (String) fc.getValue();

        FilterOperation operation = fc.getOperation();
        if (value.matches("\".+\"")) {
          if (FilterOperation.CONTAINS == operation) {
            operation = FilterOperation.EQUALS;
          }
        }

        switch (operation) {
          case CONTAINS:
            if (argumentMappings.containsKey(SearchTermTemplates.ARRAY_CONTAINS.placeholder)) {
              throw new IllegalArgumentException(
                  "Filtering by JSONB fields value are mutually exclusive!");
            }
            // FIXME: we introduced "split_xyz" column, because LocalizedText and
            // LocalizedStructuredContent
            // are HashMaps producing something like "{"de": "...", "en": "..."}" what is
            // not
            // properly handled (extremely slow) by JSONB search.
            // TODO: 1. migrate jsonb content to proper key:value structure:
            // "[{"lang":"de, "text": "..."}, {"lang":"en", "text":"..."}]"
            // 2. implement new LocalizedArgumentFactory and LocalizedColumnMapper for
            // JsonJdbiPlugin, converting
            // between searchable json and Localized-Model-Objects
            // 3. implement new CONTAINS search here (JdbiRepository.getWhereClause)
            // 4. remove "split_" columns and code for that (e.g.
            // filterBLocalizedTextFields, filterBySplitField)
            if (hasSplitColumn(basicExpression)) {
              argumentMappings.put(
                  SearchTermTemplates.ARRAY_CONTAINS.placeholder, splitToArray(value));
              return SearchTermTemplates.ARRAY_CONTAINS.renderTemplate(
                  tableAlias, "split_" + basicExpression);
            } else {
              throw new UnsupportedOperationException(
                  "Filtering on JSONB field CONTAINS only supported if split_-column for "
                      + basicExpression
                      + " exists (for now)!");
            }
          case EQUALS:
            if (argumentMappings.containsKey(SearchTermTemplates.JSONB_PATH.placeholder)) {
              throw new IllegalArgumentException(
                  "Filtering by JSONB fields value are mutually exclusive!");
            }
            Matcher matchLanguage = Pattern.compile("\\.([\\w_-]+)$").matcher(expression);
            String language =
                matchLanguage.find() ? "\"%s\"".formatted(matchLanguage.group(1)) : "**";
            argumentMappings.put(
                SearchTermTemplates.JSONB_PATH.placeholder, escapeTermForJsonpath(value));
            return SearchTermTemplates.JSONB_PATH.renderTemplate(
                tableAlias, basicExpression, language);
          default:
            throw new UnsupportedOperationException(
                "Filtering on JSONB field only supports CONTAINS (to be preferred) or EQUALS operator!");
        }
      }
    }

    // normal field (non JSONB) handling:
    StringBuilder query = new StringBuilder();
    if (fc != null) {
      // e.g.: "url:like:creativeco"
      String expression =
          getTargetExpression(fc); // e.g. "url" -> tableAlias + ".url" or native: "parent_uuid" ->
      // tableAlias +
      // ".parent_uuid
      FilterOperation filterOperation = fc.getOperation(); // e.g. "like" -> "CONTAINS"

      String criterionKey = KEY_PREFIX_FILTERVALUE + criterionCount;
      switch (filterOperation) {
          // @see https://www.postgresql.org/docs/11/functions.html
        case BETWEEN:
          if (fc.getMinValue() == null || fc.getMaxValue() == null) {
            throw new IllegalArgumentException("For 'BETWEEN' operation two values are expected");
          } else {
            // example: BETWEEN '2015-01-01' AND '2015-12-31'
            String keyMin = criterionKey + "_min";
            String keyMax = criterionKey + "_max";
            query
                .append("(")
                .append(expression)
                .append(" BETWEEN ")
                .append(":")
                .append(keyMin)
                .append(" AND ")
                .append(":")
                .append(keyMax)
                .append(")");
            argumentMappings.put(keyMin, fc.getMinValue());
            argumentMappings.put(keyMax, fc.getMaxValue());
          }
          break;
        case IN:
        case NOT_IN:
          if (fc.getValues() == null || fc.getValues().isEmpty()) {
            throw new IllegalArgumentException(
                "For 'IN/NOT_IN' operation at least one value is expected");
          }
          // For 'in' or 'nin' operation
          query.append("(").append(expression);
          if (filterOperation == FilterOperation.NOT_IN) {
            query.append(" NOT");
          }
          query.append(" IN (");

          ArrayList<String> values = new ArrayList<>();
          AtomicInteger valueCounter = new AtomicInteger(0);
          fc.getValues()
              .forEach(
                  v -> {
                    String key = criterionKey + "_" + valueCounter.incrementAndGet();
                    values.add(":" + key);
                    argumentMappings.put(key, v);
                  });
          query.append(values.stream().collect(Collectors.joining(",")));

          query.append("))");
          break;
        case CONTAINS:
          // @see https://www.postgresql.org/docs/11/functions-matching.html

          if (fc.getValue() instanceof Collection<?> valueCollection
              && !valueCollection.isEmpty()) {
            String arrayType =
                getArrayTypeAndFillArgumentMappings(
                    argumentMappings, criterionKey, valueCollection);
            query
                .append("(")
                .append(expression)
                .append(" @> ")
                .append(":")
                .append(criterionKey)
                .append("::")
                .append(arrayType)
                .append(")");

          } else {
            query
                .append("(")
                .append(expression)
                .append(" ILIKE '%' || ")
                .append(":")
                .append(criterionKey)
                .append(" || '%')");
            argumentMappings.put(criterionKey, fc.getValue());
          }
          break;
        case STARTS_WITH:
          // @see https://www.postgresql.org/docs/11/functions-matching.html
          query
              .append("(")
              .append(expression)
              .append(" ILIKE ")
              .append(":")
              .append(criterionKey)
              .append(" || '%')");
          argumentMappings.put(criterionKey, fc.getValue());
          break;
        case EQUALS:
          // @see https://www.postgresql.org/docs/11/functions-comparison.html
          if (fc.getValue() instanceof Collection<?> valueCollection
              && !valueCollection.isEmpty()) {
            String arrayType =
                getArrayTypeAndFillArgumentMappings(
                    argumentMappings, criterionKey, valueCollection);

            query
                .append("(")
                .append(expression)
                .append(" = ")
                .append(":")
                .append(criterionKey)
                .append("::")
                .append(arrayType)
                .append(")");
          } else {
            query
                .append("(")
                .append(expression)
                .append(" = ")
                .append(":")
                .append(criterionKey)
                .append(")");
            argumentMappings.put(criterionKey, fc.getValue());
          }
          break;
        case NOT_EQUALS:
          // @see https://www.postgresql.org/docs/11/functions-comparison.html
          query
              .append("(")
              .append(expression)
              .append(" != ")
              .append(":")
              .append(criterionKey)
              .append(")");
          argumentMappings.put(criterionKey, fc.getValue());
          break;
        case GREATER_THAN:
          // @see https://www.postgresql.org/docs/11/functions-comparison.html
          query
              .append("(")
              .append(expression)
              .append(" > ")
              .append(":")
              .append(criterionKey)
              .append(")");
          argumentMappings.put(criterionKey, fc.getValue());
          break;
        case GREATER_THAN_OR_NOT_SET:
          // @see https://www.postgresql.org/docs/11/functions-comparison.html
          query
              .append("(")
              .append(expression)
              .append(" > ")
              .append(":")
              .append(criterionKey)
              .append(" OR ")
              .append(expression)
              .append(" IS NULL")
              .append(")");
          argumentMappings.put(criterionKey, fc.getValue());
          break;
        case GREATER_THAN_OR_EQUAL_TO:
          // @see https://www.postgresql.org/docs/11/functions-comparison.html
          query
              .append("(")
              .append(expression)
              .append(" >= ")
              .append(":")
              .append(criterionKey)
              .append(")");
          argumentMappings.put(criterionKey, fc.getValue());
          break;
        case LESS_THAN:
          // @see https://www.postgresql.org/docs/11/functions-comparison.html
          query
              .append("(")
              .append(expression)
              .append(" < ")
              .append(":")
              .append(criterionKey)
              .append(")");
          argumentMappings.put(criterionKey, fc.getValue());
          break;
        case LESS_THAN_AND_SET:
          // @see https://www.postgresql.org/docs/11/functions-comparison.html
          query
              .append("(")
              .append(expression)
              .append(" < ")
              .append(":")
              .append(criterionKey)
              .append(" AND ")
              .append(expression)
              .append(" IS NOT NULL")
              .append(")");
          argumentMappings.put(criterionKey, fc.getValue());
          break;
        case LESS_THAN_OR_EQUAL_TO:
          // @see https://www.postgresql.org/docs/11/functions-comparison.html
          query
              .append("(")
              .append(expression)
              .append(" <= ")
              .append(":")
              .append(criterionKey)
              .append(")");
          argumentMappings.put(criterionKey, fc.getValue());
          break;
        case LESS_THAN_OR_EQUAL_TO_AND_SET:
          // @see https://www.postgresql.org/docs/11/functions-comparison.html
          query
              .append("(")
              .append(expression)
              .append(" <= ")
              .append(":")
              .append(criterionKey)
              .append(" AND ")
              .append(expression)
              .append(" IS NOT NULL")
              .append(")");
          argumentMappings.put(criterionKey, fc.getValue());
          break;
        case LESS_THAN_OR_EQUAL_TO_OR_NOT_SET:
          // @see https://www.postgresql.org/docs/11/functions-comparison.html
          query
              .append("(")
              .append(expression)
              .append(" <= ")
              .append(":")
              .append(criterionKey)
              .append(" OR ")
              .append(expression)
              .append(" IS NULL")
              .append(")");
          argumentMappings.put(criterionKey, fc.getValue());
          break;
        case SET:
          // @see https://www.postgresql.org/docs/11/functions-comparison.html
          query.append("(").append(expression).append(" IS NOT NULL").append(")");
          break;
        case NOT_SET:
          // @see https://www.postgresql.org/docs/11/functions-comparison.html
          query.append("(").append(expression).append(" IS NULL").append(")");
          break;
        default:
          throw new UnsupportedOperationException(filterOperation + " not supported yet");
      }
    }
    return query.toString();
  }

  protected String getWhereClauses(Filtering filtering, Map<String, Object> argumentMappings) {
    if (filtering == null || filtering.getFilterCriteria().isEmpty()) {
      return "";
    }

    ArrayList<String> whereClauses = new ArrayList<>();
    List<FilterCriterion> filterCriteria = filtering.getFilterCriteria();
    int criterionCount = argumentMappings.size() + 1;
    for (FilterCriterion filterCriterion : filterCriteria) {
      String whereClause = getWhereClause(filterCriterion, argumentMappings, criterionCount);
      whereClauses.add(whereClause);
      criterionCount++;
    }

    String filterClauses = whereClauses.stream().collect(Collectors.joining(" AND "));
    return filterClauses;
  }

  /**
   * Override this method for check of split fields that exist in the repository's context
   *
   * @param propertName java property name
   * @return corresponding split-column for property exists
   */
  protected boolean hasSplitColumn(String propertName) {
    return false;
  }

  /*
   * if filtering has other target object type than actual (this) repository
   * instance use this method to rename filter expression names to target table
   * alias and column names
   */
  protected void mapFilterExpressionsToOtherTableColumnNames(
      Filtering filtering, AbstractPagingSortingFilteringRepositoryImpl otherRepository) {
    if (filtering != null) {
      List<FilterCriterion> filterCriteria =
          filtering.getFilterCriteria().stream()
              .map(
                  fc -> {
                    fc.setExpression(otherRepository.getColumnName(fc.getExpression()));
                    fc.setNativeExpression(true);
                    return fc;
                  })
              .collect(Collectors.toList());
      filtering.setFilterCriteria(filterCriteria);
    }
  }

  protected long retrieveCount(StringBuilder sqlCount, final Map<String, Object> argumentMappings) {
    long total =
        dbi.withHandle(
            h ->
                h.createQuery(sqlCount.toString())
                    .bindMap(argumentMappings)
                    .mapTo(Long.class)
                    .findOne()
                    .get());
    return total;
  }

  protected Integer retrieveNextSortIndexForParentChildren(
      Jdbi dbi, String tableName, String columNameParentUuid, UUID parentUuid) {
    // first child: max gets no results (= null)):
    Integer sortIndex =
        dbi.withHandle(
            (Handle h) ->
                h.createQuery(
                        "SELECT MAX(sortIndex) + 1 FROM "
                            + tableName
                            + " WHERE "
                            + columNameParentUuid
                            + " = :parent_uuid")
                    .bind("parent_uuid", parentUuid)
                    .mapTo(Integer.class)
                    .findOne()
                    .orElse(null));
    if (sortIndex == null) {
      return 0;
    }
    return sortIndex;
  }

  public String[] splitToArray(LocalizedText localizedText) {
    if (localizedText == null) {
      return new String[0];
    }
    List<String> splitLabels =
        localizedText.values().stream()
            .map(text -> splitToArray(text))
            .flatMap(Arrays::stream)
            .collect(Collectors.toList());
    return splitLabels.toArray(new String[splitLabels.size()]);
  }

  public String[] splitToArray(String term) {
    term = term.toLowerCase();
    /*
     * Remove all characters that are NOT: - space - letter or digit - underscore -
     * hyphen and remove all standalone hyphens (space hyphen space) (flag `U`
     * stands for Unicode)
     */
    term = term.replaceAll("(?iU)[^\\s\\w_-]|(?<=\\s)-(?=\\s)", "");
    // Look for words with hyphens to split them too
    Matcher hyphenWords = Pattern.compile("(?iU)\\b\\w+(-\\w+)+\\b").matcher(term);
    List<String> result =
        hyphenWords
            .results()
            .collect(
                ArrayList<String>::new,
                (list, match) -> list.addAll(Arrays.asList(match.group().split("-+"))),
                ArrayList::addAll);
    for (String word : term.trim().split("\\s+")) {
      result.add(word);
    }
    return result.toArray(new String[result.size()]);
  }
}
