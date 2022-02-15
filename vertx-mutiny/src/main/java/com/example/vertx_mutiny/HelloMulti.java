package com.example.vertx_mutiny;

import io.smallrye.mutiny.Multi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;

public class HelloMulti {

  private static final Logger logger = LoggerFactory.getLogger(HelloMulti.class);

  public static void main(String[] args) {
    Multi.createFrom().items(IntStream.rangeClosed(0,10).boxed())
      .onItem().transform(integer -> integer / 0)
      .onItem().transform(String::valueOf)
      .onFailure().invoke(failure -> logger.error("Transformation failed with following exception: ", failure))
      //.onFailure().recoverWithItem("fallback")
      .select().first(2)
      //.select().last(2)
      .subscribe().with(
        logger::info,
        failure -> logger.error("Could not handle the input..."));
  }
}
