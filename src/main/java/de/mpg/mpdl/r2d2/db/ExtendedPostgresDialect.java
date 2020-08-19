package de.mpg.mpdl.r2d2.db;

import java.sql.Types;

import org.hibernate.dialect.PostgreSQL95Dialect;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

public class ExtendedPostgresDialect extends PostgreSQL95Dialect {

  public ExtendedPostgresDialect() {
    super();
    this.registerHibernateType(Types.OTHER, JsonBinaryType.class.getName());
  }

}
