package com.sigius

import com.sigius.plugins.configureRouting
import com.sigius.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import io.ktor.server.util.*
import org.slf4j.event.Level

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    log.info("Application is starting..")
    install(CallLogging) {
        level = Level.INFO
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val url = call.url()
            val userAgent = call.request.headers["User-Agent"]
            "Status: $status, HTTP method: $httpMethod, URL: $url, User agent: $userAgent"
        }
    }
    configureRouting()
    configureSerialization()
}
