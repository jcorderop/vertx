import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  java
  application
  id("com.github.johnrengelman.shadow") version "7.0.0"
  id("io.spring.dependency-management") version "1.0.1.RELEASE"
  id("com.google.cloud.tools.jib") version "3.1.4"
}

group = "com.example"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "4.1.5"
val junitJupiterVersion = "5.7.0"
val jacksonVersion = "2.11.4"

val mainVerticleName = "com.example.vertx_starter.MainVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClass.set(launcherClassName)
}
dependencyManagement {
  imports {
    mavenBom("org.apache.logging.log4j:log4j-bom:2.14.1")
  }
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-core")
  implementation("io.vertx:vertx-web")
  testImplementation("io.vertx:vertx-junit5")
  implementation("io.vertx:vertx-micrometer-metrics")
  implementation("io.micrometer:micrometer-registry-prometheus:1.8.2")

  implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")

  implementation("org.apache.logging.log4j:log4j-api")
  implementation("org.apache.logging.log4j:log4j-core")
  implementation("org.apache.logging.log4j:log4j-slf4j-impl")
  implementation("org.slf4j:slf4j-api:1.7.32")

  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
}

java {
  sourceCompatibility = JavaVersion.VERSION_16
  targetCompatibility = JavaVersion.VERSION_16
}

jib {
  from {
    image = "adoptopenjdk:16-jre-hotspot"
  }
  to {
    image = "jcorderop/vertx-starter:1.0.0"
  }
  container {
    mainClass = "io.vertx.core.Launcher"
    args = listOf("run", mainVerticleName)
    ports = listOf("8888")
  }
}

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  args = listOf("run", mainVerticleName, "--redeploy=$watchForChange", "--launcher-class=$launcherClassName", "--on-redeploy=$doOnChange")
}
