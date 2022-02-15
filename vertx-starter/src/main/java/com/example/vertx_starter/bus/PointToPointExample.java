package com.example.vertx_starter.bus;

import io.micrometer.core.instrument.MeterRegistry;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.micrometer.MetricsService;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxPrometheusOptions;
import io.vertx.micrometer.backends.BackendRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PointToPointExample {
  public static final Logger LOGGER = LoggerFactory.getLogger(PointToPointExample.class);
  public static final String MY_REQUEST_ADDRESS = "my.request.address";



  public static void main(String[] args) {
    LOGGER.info("Starting project...");
    //var vertx = Vertx.vertx();
    MeterRegistry registry = BackendRegistries.getDefaultNow();

    /*
    new ClassLoaderMetrics().bindTo(registry);
    new JvmMemoryMetrics().bindTo(registry);
    new JvmGcMetrics().bindTo(registry);
    new ProcessorMetrics().bindTo(registry);
    new JvmThreadMetrics().bindTo(registry);


     */

    MicrometerMetricsOptions options = new MicrometerMetricsOptions()
      .setPrometheusOptions(new VertxPrometheusOptions()
        .setStartEmbeddedServer(true)
        .setEmbeddedServerOptions(new HttpServerOptions().setPort(8081))
        .setEnabled(true))
      .setEnabled(true);
    Vertx vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(options));
    /*
    Vertx vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(
      new MicrometerMetricsOptions()
        .setMicrometerRegistry(registry)
        .setEnabled(true)));*/

    //vertx.deployVerticle(new Metrics(vertx));
    vertx.deployVerticle(new Sender());
    vertx.deployVerticle(new Receiver());
    LOGGER.debug("Has been started...");



    final MetricsService metricsService = MetricsService.create(vertx.eventBus());
    vertx.setPeriodic(1000, id -> {
      JsonObject metrics = metricsService.getMetricsSnapshot();
      metricsService.getMetricsSnapshot();
      System.out.println("metrics: "+ metrics);
    });
  }

  static class Sender extends AbstractVerticle {
    @Override
    public void start(final Promise<Void> startPromise) throws Exception {
      startPromise.complete();
      vertx.setPeriodic(100, id -> {
        //System.out.println("sending...");
        vertx.eventBus().send(Sender.class.getName(), "Sender sending something ..."+ id);
      });
    }
  }

  //https://vertx.io/docs/vertx-micrometer-metrics/java/#_jmx
  //https://vertx.io/blog/eclipse-vert-x-metrics-now-with-micrometer-io/
  static class Metrics extends AbstractVerticle {

    final MetricsService metricsService;

    Metrics(Vertx vertx) {
      this.metricsService = MetricsService.create(vertx);
    }

    @Override
    public void start(final Promise<Void> startPromise) throws Exception {
      startPromise.complete();
      vertx.setPeriodic(1000, id -> {
        JsonObject metrics = metricsService.getMetricsSnapshot();
        metricsService.getMetricsSnapshot();
        System.out.println("metrics: "+ metrics);
      });
    }
  }




  static class Receiver extends AbstractVerticle {
    @Override
    public void start(final Promise<Void> startPromise) throws Exception {
      startPromise.complete();
      vertx.eventBus().<String>consumer(Sender.class.getName(), message -> {
        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        LOGGER.debug("New: {}", message.body());
      });
    }
  }


}
