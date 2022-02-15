package com.broker.vertx_stock_broker.broker;

import lombok.*;
import lombok.experimental.SuperBuilder;

//@Getter
//@SuperBuilder(toBuilder = true)
//@Value
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Asset {

  @NonNull
  public String name;

}
