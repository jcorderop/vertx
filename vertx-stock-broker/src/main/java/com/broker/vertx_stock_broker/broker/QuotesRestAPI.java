package com.broker.vertx_stock_broker.broker;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class QuotesRestAPI {

  private static final Logger logger = LoggerFactory.getLogger(QuotesRestAPI.class);

  public static void attach(Router restApi, final Pool pgPool) {
    final Map<String, JsonObject> cachedQuotes = new HashMap<>();
    AssetsRestApi.ASSETS.forEach(symbol -> {
      cachedQuotes.put(symbol, initRandomQuote(symbol));
    });

    restApi.get("/quotes/:asset").handler(new QuoteHandler(cachedQuotes));
    restApi.get("/pg/quotes/:asset").handler(new QuoteFromDBHandler(pgPool));
  }

  private static JsonObject initRandomQuote(String asset) {
    final Quote quote = Quote.builder()
                            .asset(new Asset(asset))
                            .bid(randomeValue())
                            .ask(randomeValue())
                            .lastPrice(randomeValue())
                            .volume(randomeValue())
                            .build();
    return quote.toJsonObject();
  }

  private static BigDecimal randomeValue() {
    return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 100));
  }
}
