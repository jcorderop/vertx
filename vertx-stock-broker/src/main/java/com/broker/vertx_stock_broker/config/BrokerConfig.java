package com.broker.vertx_stock_broker.config;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.util.Objects;

@Builder
@Value
@ToString
public class BrokerConfig {

  int serverPort;
  String version;
  DBConfig dbConfig;

  public static BrokerConfig from(final JsonObject config){
    final Integer serverPort = config.getInteger(ConfigLoader.SERVER_PORT);
    if (Objects.isNull(serverPort)) {
      throw new RuntimeException("Server port is not configure.");
    }

    final String version = config.getString("version");
    if (Objects.isNull(version)) {
      throw new RuntimeException("Version is not configure.");
    }

    return BrokerConfig.builder()
      .serverPort(serverPort)
      .version(version)
      .dbConfig(parseDbConfig(config))
      .build();
  }

  private static DBConfig parseDbConfig(final JsonObject config) {
    var dbConfig= config.getJsonObject("db");
    return DBConfig.builder()
      .host(dbConfig.getString(ConfigLoader.DB_HOST))
      .port(dbConfig.getInteger(ConfigLoader.DB_PORT))
      .database(dbConfig.getString(ConfigLoader.DB_DATABASE))
      .user(dbConfig.getString(ConfigLoader.DB_USER))
      .password(dbConfig.getString(ConfigLoader.DB_PASSWORD))
      .build();
  }
}
