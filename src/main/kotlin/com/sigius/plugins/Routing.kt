package com.sigius.plugins

import com.sigius.routes.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        mainRoute()
        urlAll()
        urlShorten()
        urlExpand()
        urlRedirect()
//        trace { application.log.trace(it.buildText()) }
    }
}
