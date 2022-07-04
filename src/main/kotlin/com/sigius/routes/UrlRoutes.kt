package com.sigius.routes

import com.sigius.models.UrlItem
import com.sigius.models.getUrlFromStorage
import com.sigius.models.urlStorage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

val regex = "^(https?|ftp)://[-a-zA-Z\\d+&@#/%?=~_|!:,.;]*[-a-zA-Z\\d+&@#/%=~_|]".toRegex()
val shortDomain: String = System.getenv("SHORTDOMAIN") ?: "localhost"

private fun randomID(): String = List(16) {
    (('a'..'z') + ('A'..'Z') + ('0'..'9')). random()
}. joinToString("")

fun isValidUrl(url: String): Boolean {
    return regex.containsMatchIn(url)
}


fun Route.mainRoute() {
    route("/") {
        get {
                val message = "{\"appName\": \"urlShortener\"}"
                call.respond(message = message, status = HttpStatusCode.OK)
            }
        }
}

fun Route.urlAll() {
    route("/url") {
        get {
            if (urlStorage.isNotEmpty()) {
                call.application.environment.log.info("Respond from /url api with content")
                call.respond(message = urlStorage, status = HttpStatusCode.OK)
            } else {
                call.application.environment.log.info("Respond from /url api without content")
                val message = "{ \"message\": \"No urls found\" }"
                call.respondText(
                    text = message,
                    contentType = ContentType.Application.Json,
                    status =HttpStatusCode.OK)
            }
        }
    }
}

fun Route.urlShorten() {
    route("/url/shorten") {
        get ("{longUrl?}"){
            val longUrl = call.parameters["longUrl"]
            if (longUrl == null) {
                val message = "{\"errorMessage\": \"Missing longUrl\"}"
                return@get call.respondText(
                    text = message,
                    contentType = ContentType.Application.Json,
                    status = HttpStatusCode.BadRequest)
            }
            else if (!isValidUrl(longUrl.toString()) ) {
                call.application.environment.log.info("Wrong url sent: $longUrl")
                val message = "{\"errorMessage\": \"longUrl should be a valid URL, like http(s)://www.example.com\"}"
                return@get call.respondText(
                    text = message,
                    contentType = ContentType.Application.Json,
                    status = HttpStatusCode.BadRequest)
            }
            else {
                val hash = randomID()
                val shortUrl = "https://$shortDomain/u/$hash"
                urlStorage.add(UrlItem(hash, longUrl, shortUrl))
                call.application.environment.log.info("$longUrl saved as $shortUrl with id: $hash")
                val message = "{\"shortenedUrl\": \"$shortUrl\"}"
                call.respondText(
                    text = message,
                    contentType = ContentType.Application.Json,
                    status = HttpStatusCode.Created)
            }
        }
    }
}

fun Route.urlExpand() {
    route("/url/expand") {
        get("{shortUrl?}") {
            val shortUrl = call.parameters["shortUrl"]
            if (shortUrl == null) {
                call.application.environment.log.debug("Missing shortUrl in request")
                val message = "{\"errorMessage\": \"Missing shortUrl\"}"
                return@get call.respondText(
                    text = message,
                    contentType = ContentType.Application.Json,
                    status = HttpStatusCode.BadRequest
                )
            } else if (!isValidUrl(shortUrl.toString())) {
                call.application.environment.log.info("Wrong url sent: $shortUrl")
                val message =
                    "{\"errorMessage\": \"shortUrl should be a valid short URL, like http(s)://example.com/u/xxxxxxx\"}"
                return@get call.respondText(
                    text = message,
                    contentType = ContentType.Application.Json,
                    status = HttpStatusCode.BadRequest
                )
            } else {
                val id = shortUrl.toString().split("/u/")[1]
                call.application.environment.log.info("Client is asking for url with id: $id")
                val longUrlObject = getUrlFromStorage(id)
                if (longUrlObject != null) {
                    call.application.environment.log.info("Returned ${longUrlObject.shortUrl}")
                    val message = "{\"longUrl\": \"${longUrlObject.longUrl}\", \"shortUrl\": \"${longUrlObject.shortUrl}\"}"
                    call.respondText(
                        text = message,
                        contentType = ContentType.Application.Json,
                        status = HttpStatusCode.OK)
                } else {
                    call.application.environment.log.info("Returned nothing")
                    val message = "{\"errorMessage\": \"No valid url was asked\"}"
                    call.respondText(
                        text = message,
                        contentType = ContentType.Application.Json,
                        status = HttpStatusCode.BadRequest)
                }
            }
        }
    }
}
fun Route.urlRedirect() {
    route("/u") {
        get ("{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                val message = "{\"errorMessage\": \"Missing id\"}"
                return@get call.respondText(
                    text = message,
                    contentType = ContentType.Application.Json,
                    status = HttpStatusCode.BadRequest)
            }
            else {
                call.application.environment.log.info("Client is asking for url with id: $id")
                val longUrlObject = getUrlFromStorage(id)
                if(longUrlObject != null) {
                    call.application.environment.log.info("Returned ${longUrlObject.shortUrl}")
                    call.respondRedirect(
                        url = longUrlObject.longUrl,
                        permanent = true)
                }
                else{
                    call.application.environment.log.info("Returned nothing")
                    val message = "{\"errorMessage\": \"No valid url was asked\"}"
                    call.respondText(
                        text = message,
                        contentType = ContentType.Application.Json,
                        status = HttpStatusCode.BadRequest)
                }
            }
        }
    }
}