package com.broker.vertx_stock_broker.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class ConfigLoader {

  public static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);

  public static final String CONFIG_FILE = "application.yaml";
  // Exposed Environment Variables
  public static final String SERVER_PORT = "SERVER_PORT";
  public static final String DB_HOST = "host";
  public static final String DB_PORT = "port";
  public static final String DB_DATABASE = "database";
  public static final String DB_USER = "user";
  public static final String DB_PASSWORD = "password";
  static final List<String> EXPOSED_ENVIRONMENT_VARIABLES = Arrays.asList(SERVER_PORT,
    DB_HOST, DB_PORT, DB_DATABASE, DB_USER, DB_PASSWORD);

  public static Future<BrokerConfig> load(Vertx vertx) {
    final var exposedKeys =  new JsonArray();
    EXPOSED_ENVIRONMENT_VARIABLES.forEach(exposedKeys::add);
    logger.info("Fetch configuration for {} ", exposedKeys.encode());

    var envStore = new ConfigStoreOptions()
      .setType("sys")
      .setConfig(new JsonObject().put("key", exposedKeys));

    var propertyStore = new ConfigStoreOptions()
      .setType("env")
      .setConfig(new JsonObject().put("cache", false));

    var yamlStore = new ConfigStoreOptions()
      .setType("file")
      .setFormat("yaml")
      .setConfig(new JsonObject().put("path", CONFIG_FILE));

    var retriever = ConfigRetriever.create(vertx
      , new ConfigRetrieverOptions()
            .addStore(envStore)
            .addStore(propertyStore)
            .addStore(yamlStore));
    return retriever.getConfig().map(BrokerConfig::from);
  }
}
