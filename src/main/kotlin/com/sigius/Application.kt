package com.sigius

import com.sigius.plugins.configureRouting
import com.sigius.plugins.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    log.info("Application is starting..")
    configureRouting()
    configureSerialization()
}
