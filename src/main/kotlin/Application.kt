package pl.dev.bkwiatkowski

import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
  EngineMain.main(args)
}

fun Application.module() {
  configureHTTP()
  configureSecurity()
  configureSerialization()
  configureDatabases()
  configureFrameworks()
  configureSockets()
  configureAdministration()
  configureRouting()
}
