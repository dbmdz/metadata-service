package de.digitalcollections.cudami.server.backend.impl.database.migration;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.json.JSONObject;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class V1_4_0__DML_Refactor_localized_content extends BaseJavaMigration {

  @Override
  public void migrate(Context context) throws Exception {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(
        new SingleConnectionDataSource(context.getConnection(), true).getConnection()
    );
    migrateLocalizedStructuredContent(jdbcTemplate, "description", "identifiables");
    migrateLocalizedStructuredContent(jdbcTemplate, "text", "articles");
    migrateLocalizedStructuredContent(jdbcTemplate, "text", "webpages");
  }

  private void migrateLocalizedStructuredContent(JdbcTemplate jdbcTemplate, String tableField, String tableName) throws SQLException {
    String selectQuery = String.format("SELECT uuid,%s FROM %s", tableField, tableName);
    String updateQuery = String.format("UPDATE %s SET %s=?::JSONB WHERE uuid=?::uuid", tableName, tableField);

    List<Map<String, String>> identifiables = jdbcTemplate.queryForList(selectQuery);
    for (Map<String, String> identifiable : identifiables) {
      jdbcTemplate.update(
          updateQuery,
          convertLocalizedStructuredContent(identifiable.get(tableField)),
          identifiable.get("uuid")
      );
    }
  }

  private String convertLocalizedStructuredContent(String currentJson) {
    JSONObject localizedStructuredContent = new JSONObject(currentJson).getJSONObject("localizedStructuredContent");
    JSONObject newJson = new JSONObject();
    localizedStructuredContent.keySet().forEach((locale) -> {
      newJson.put(locale, localizedStructuredContent.get(locale));
    });
    return newJson.toString();
  }
}
