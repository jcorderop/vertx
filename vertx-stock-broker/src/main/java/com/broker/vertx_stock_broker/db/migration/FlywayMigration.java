package com.broker.vertx_stock_broker.db.migration;

import com.broker.vertx_stock_broker.config.DBConfig;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class FlywayMigration {

  private static final Logger logger = LoggerFactory.getLogger(FlywayMigration.class);

  public static Future<Object> migrate(Vertx vertx, DBConfig dbConfig) {
    return vertx.executeBlocking(promise -> {
        // use executeBlocking because all has to wait until it is finished.
        executeDBMigration(dbConfig);
        promise.complete();
      }
    ).onFailure(err -> logger.error("Migration Failed, reason: ", err));
  }

  private static void executeDBMigration(DBConfig dbConfig) {
    logger.info("DB Configuration: {}", dbConfig);

    final String jdbcurl = String.format("jdbc:postgresql://%s:%d/%s",
      dbConfig.getHost(),
      dbConfig.getPort(),
      dbConfig.getDatabase()
    );
    logger.info("JDBC URL: {}", jdbcurl);

    Flyway flyway = Flyway.configure()
      .dataSource(jdbcurl, dbConfig.getUser(), dbConfig.getPassword())
      .schemas("broker")
      .defaultSchema("broker")
      .load();

    try {
      var current = Optional.ofNullable(flyway.info().current());
      current.ifPresent(info -> logger.info("DB Schema version: {}", Optional.ofNullable(info.getVersion()).orElse(MigrationVersion.EMPTY)));

      var pendingMigration = Optional.ofNullable(flyway.info().pending());
      logger.info("DB Pending Migration: {}", printMigrations(pendingMigration));

      flyway.migrate();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private static String printMigrations(Optional<MigrationInfo[]> pendingMigration) {
    Optional<String> pendingPrintable = pendingMigration
      .map(migrationInfos -> Arrays.stream(migrationInfos)
        .map(migrationInfo -> migrationInfo.getVersion() + " - " + migrationInfo.getDescription())
        .collect(Collectors.joining(",", "[", "]")));
    return pendingPrintable.get();
  }

}
