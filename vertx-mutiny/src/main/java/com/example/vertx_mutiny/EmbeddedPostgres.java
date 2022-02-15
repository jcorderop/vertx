package com.example.vertx_mutiny;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class EmbeddedPostgres {

  static final String DATABASE_NAME = "users";
  static final String USERNAME = "postgres";
  static final String PASSWORD = "secret";

  public static int startPostgres() {
    var pg = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13.3-alpine"))
      .withDatabaseName(DATABASE_NAME)
      .withUsername(USERNAME)
      .withPassword(PASSWORD)
      .withInitScript("db/setup.sql");
    pg.start();
    return pg.getFirstMappedPort();
  }
}
