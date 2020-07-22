package de.mpg.mpdl.r2d2.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class DeleteDatabaseExtension implements AfterEachCallback {

  @Override
  public void afterEach(ExtensionContext context) throws Exception {

    //TODO: Delete/recreate tables instead of truncate them?

    DataSource dataSource = SpringExtension.getApplicationContext(context).getBean(DataSource.class);
    Connection connection = dataSource.getConnection();

    String[] table_types = {"TABLE"};
    ResultSet tablesResultSet = connection.getMetaData().getTables(null, null, "%", table_types);

    Statement statement = connection.createStatement();
    while (tablesResultSet.next()) {
      String tableName = tablesResultSet.getString("TABLE_NAME");
      statement.addBatch("TRUNCATE TABLE " + tableName + " CASCADE");
    }
    statement.executeBatch();

    //TODO: Check persistence-level Approach:
    //Another approach would be to use the EntityManager (or Hibernate or JdbcTemplate!?) to delete the test data on entity/persistence-level (instead of the sql-level)
  }

}
