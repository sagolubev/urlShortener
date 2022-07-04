package com.sigius.plugins

import com.sigius.routes.mainRoute
import com.sigius.routes.urlAll
import com.sigius.routes.urlExpand
import com.sigius.routes.urlShorten
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        mainRoute()
        urlAll()
        urlShorten()
        urlExpand()
//        trace { application.log.trace(it.buildText()) }
    }
}
