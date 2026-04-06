package pl.dev.bkwiatkowski

import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.koin.ktor.ext.inject
import pl.dev.bkwiatkowski.plugins.FrameworksPlugin
import pl.dev.bkwiatkowski.plugins.MonitoringPlugin
import pl.dev.bkwiatkowski.plugins.RoutingPlugin
import pl.dev.bkwiatkowski.plugins.SecurityPlugin

fun main(args: Array<String>) {
  EngineMain.main(args)
}

fun Application.module() {
  FrameworksPlugin().configure(this)
  
  val monitoringPlugin: MonitoringPlugin by inject()
  monitoringPlugin.configure(this)
  
  val securityPlugin: SecurityPlugin by inject()
  securityPlugin.configure(application = this)

  val routingPlugin: RoutingPlugin by inject()
  routingPlugin.configure(application = this)

  configureHTTP()
  configureSockets()
  configureAdministration()
}
