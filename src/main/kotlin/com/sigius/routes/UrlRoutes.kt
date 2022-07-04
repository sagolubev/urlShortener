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
                call.respondText("urlShortener ", status = HttpStatusCode.OK)
            }
        }
}

fun Route.urlAll() {
    route("/url") {
        get {
            if (urlStorage.isNotEmpty()) {
                call.application.environment.log.info("Respond from /url api with content")
                call.respond( message = urlStorage, status = HttpStatusCode.OK )
            } else {
                call.application.environment.log.info("Respond from /url api without content")
                call.respond( message ="{\"message\": \"No urls found\"}", status = HttpStatusCode.OK )
            }
        }
    }
}

fun Route.urlShorten() {
    route("/url/shorten") {
        get ("{longUrl?}"){
            val longUrl = call.parameters["longUrl"]
            if (longUrl == null) {
                return@get call.respond( message = "{\"errorMessage\": \"Missing longUrl\"}", status = HttpStatusCode.BadRequest )
            }
            else if (!isValidUrl(longUrl.toString()) ) {
                call.application.environment.log.info("Wrong url sent: $longUrl")
                return@get call.respond( message = "{\"errorMessage\": \"longUrl should be a valid URL, like http(s)://www.example.com\"}", status = HttpStatusCode.BadRequest)
            }
            else {
                val hash = randomID()
                val shortUrl = "https://$shortDomain/u/$hash"
                urlStorage.add(UrlItem(hash, longUrl, shortUrl))
                call.application.environment.log.info("$longUrl saved as $shortUrl with id: $hash")
                call.respond(message = "{\"shortenedUrl\": \"$shortUrl\"}", status = HttpStatusCode.Created)
            }
        }
    }
}

fun Route.urlExpand() {
    route("/url/expand") {
        get ("{shortUrl?}") {
            val shortUrl = call.parameters["shortUrl"]
            if (shortUrl == null) {
                return@get call.respond(message = "{\"errorMessage\": \"Missing shortUrl\"}", status = HttpStatusCode.BadRequest)
            }
            else if (!isValidUrl(shortUrl.toString()) ) {
                call.application.environment.log.info("Wrong url sent: $shortUrl")
                return@get call.respond( message = "shortUrl should be a valid short URL, like http(s)://example.com/u/xxxxxxx", status = HttpStatusCode.BadRequest)
            }
            else {
                val id = shortUrl.toString().split("/u/")[1]
                call.application.environment.log.info("Client is asking for url with id: $id")
                val longUrl = getUrlFromStorage(id)
                if(longUrl != "1") {
                    call.application.environment.log.info("Returned $longUrl")
                    call.respondText("$longUrl", status = HttpStatusCode.OK)
                }
                else{
                    call.application.environment.log.info("Returned nothing")
                    call.respondText("No valid url was asked", status = HttpStatusCode.BadRequest)
                }
            }
        }
    }
    route("/u") {
        get ("{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                return@get call.respondText( "Missing id", status = HttpStatusCode.BadRequest)
            }
//            else if (!isValidUrl(shortUrl.toString()) ) {
//                call.application.environment.log.info("Wrong url sent: $shortUrl")
//                return@get call.respondText( "id should be a valid short URL, like http(s)://example.com/u/xxxxxxx", status = HttpStatusCode.BadRequest)
//            }
            else {
                call.application.environment.log.info("Client is asking for url with id: $id")
                val longUrl = getUrlFromStorage(id)
                if(longUrl != "1") {
                    call.application.environment.log.info("Returned $longUrl")
                    call.respondText("$longUrl\n", status = HttpStatusCode.MovedPermanently)
                }
                else{
                    call.application.environment.log.info("Returned nothing")
                    call.respondText("No valid url was asked", status = HttpStatusCode.BadRequest)
                }
            }
        }
    }
}