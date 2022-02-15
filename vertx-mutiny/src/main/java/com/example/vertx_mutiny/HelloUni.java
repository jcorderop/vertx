package com.example.vertx_mutiny;

import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloUni {

  private static final Logger logger = LoggerFactory.getLogger(HelloUni.class);

  public static void main(String[] args) {
    Uni.createFrom().item("Hello")
      .onItem().transform(item -> item + " Mutniny!")
      .onItem().transform(String::toUpperCase)
      .subscribe().with(item -> logger.info("Result: {}", item));

    Uni.createFrom().item("Ignore due to failure...")
      .onItem().castTo(Integer.class)
      .subscribe().with(
        integer -> logger.info("Integer value {}", integer),
        failure -> logger.error("Could not handle the input", failure)
      );

    Uni.createFrom().item(5)
      .onItem().castTo(Integer.class)
      .subscribe().with(
        integer -> logger.info("Integer value {}", integer),
        failure -> logger.error("Could not handle the input", failure)
      );
  }
}
