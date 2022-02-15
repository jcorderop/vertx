package com.broker.vertx_stock_broker;

import com.broker.vertx_stock_broker.config.BrokerConfig;
import com.broker.vertx_stock_broker.config.ConfigLoader;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRestApiTest {

  private static final Logger logger = LoggerFactory.getLogger(AbstractRestApiTest.class);

  protected static final int TEST_DEFAULT_PORT = 9000;

  protected BrokerConfig brokerConfig;

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    System.setProperty(ConfigLoader.SERVER_PORT, String.valueOf(TEST_DEFAULT_PORT));

    brokerConfig = ConfigLoader.load(vertx)
      .onFailure(testContext::failNow)
      .onSuccess(config -> {
        logger.info("Test Retrieved Configuration {}", config);
      })
      .result();
    System.out.println(brokerConfig);

    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  protected WebClient createWebClient(Vertx vertx) {
    return WebClient.create(vertx, new WebClientOptions().setDefaultPort(TEST_DEFAULT_PORT));
  }
}
