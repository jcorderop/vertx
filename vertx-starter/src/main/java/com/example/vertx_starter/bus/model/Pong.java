package com.example.vertx_starter.bus.model;

public class Pong {

  final Type type;
  final Status status;

  public Pong(Type type, Status status) {
    this.type = type;
    this.status = status;
  }

  public Type getType() {
    return type;
  }

  public Status getStatus() {
    return status;
  }

  @Override
  public String toString() {
    return "Pong{" +
      "confirmation='" + type + '\'' +
      ", status='" + status + '\'' +
      '}';
  }
}
