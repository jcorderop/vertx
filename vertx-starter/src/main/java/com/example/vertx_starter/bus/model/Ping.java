package com.example.vertx_starter.bus.model;

public class Ping {
  final private String name;
  final private boolean enable;

  public Ping(final String name, final boolean enable) {
    this.name = name;
    this.enable = enable;
  }

  public String getName() {
    return name;
  }

  public boolean isEnable() {
    return enable;
  }

  @Override
  public String toString() {
    return "Ping{" +
      "name='" + name + '\'' +
      ", enable=" + enable +
      '}';
  }
}
