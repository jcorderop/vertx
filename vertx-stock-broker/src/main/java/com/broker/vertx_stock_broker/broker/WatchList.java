package com.broker.vertx_stock_broker.broker;

import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WatchList {

  List<Asset> assets;

  public JsonObject toJsonObject() {
    return JsonObject.mapFrom(this);
  }
}
