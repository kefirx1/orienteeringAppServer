plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.ktor)
  alias(libs.plugins.kotlin.serialization)
}

group = "pl.dev.bkwiatkowski"
version = "0.0.1"

application {
  mainClass = "io.ktor.server.netty.EngineMain"
  applicationDefaultJvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
}

kotlin {
  jvmToolchain(25)
}

dependencies {
  implementation(libs.kotlin.asyncapi.ktor)
  implementation(libs.ktor.server.caching.headers)
  implementation(libs.ktor.server.default.headers)
  implementation(libs.ktor.server.call.logging)
  implementation(libs.ktor.server.openapi)
  implementation(libs.ktor.server.routing.openapi)
  implementation(libs.ktor.server.swagger)
  implementation(libs.ktor.server.auth)
  implementation(libs.ktor.server.auth.jwt)
  implementation(libs.firebase.auth.provider)
  implementation(libs.ktor.server.content.negotiation)
  implementation(libs.ktor.server.cors)
  implementation(libs.ktor.serialization.kotlinx.json)
  implementation(libs.postgresql)
  implementation(libs.h2)
  implementation(libs.koin.ktor)
  implementation(libs.koin.logger.slf4j)
  implementation(libs.ktor.server.websockets)
  implementation(libs.ktor.server.rate.limiting)
  implementation(libs.ktor.server.netty)
  implementation(libs.logback.classic)
  implementation(libs.ktor.server.config.yaml)
  implementation(libs.exposed.jdbc)
  implementation(libs.exposed.dao)
  implementation(libs.exposed.java.time)
  implementation(libs.hikari.cp)
  testImplementation(libs.ktor.server.test.host)
  testImplementation(libs.kotlin.test.junit)
}
