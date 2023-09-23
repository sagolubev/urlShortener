package com.sigius

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.pwall.json.test.JSONExpect.Companion.expectJSON
import kotlin.test.Test
import kotlin.test.assertEquals

@Serializable
data class ResponseUrl(val longUrl: String, val shortUrl: String)

class MainRouteTestOK {
    @Test
    fun rootPathTestOK() = testApplication {
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        expectJSON(response.bodyAsText()) {
            propertyPresent("appName")
            property("appName", "urlShortener")
        }
    }

    @Test
    fun rootPathTestFail() = testApplication {
        val response = client.get("/fds")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}

class UrlAllPathTest {
    @Test
    fun urlPathTestNotFound() = testApplication {
        val response = client.get("/url")
        assertEquals(HttpStatusCode.NoContent, response.status)
        expectJSON(response.bodyAsText()) {
            propertyPresent("noContentMessage")
            property("noContentMessage", "No urls found")
        }
    }

    @Test
    fun urlPathTestFound() = testApplication {
        val checkUrl = "https://www.example.com"
        val short = client.get("/url/shorten?longUrl=$checkUrl")
        assertEquals(HttpStatusCode.Created, short.status)
        val response = client.get("/url")
        assertEquals(HttpStatusCode.OK, response.status)
        expectJSON(response.bodyAsText()) {
            item(0) {
                propertyPresent("longUrl")
                property("longUrl", checkUrl)
            }
        }
    }
}

class UrlShortenTest {
    @Test
    fun urlShortenTestMissingParameter() = testApplication {
        val checkParameter = client.get("/url/shorten")
        assertEquals(HttpStatusCode.BadRequest, checkParameter.status)
        expectJSON(checkParameter.bodyAsText()) {
            propertyPresent("errorMessage")
            property("errorMessage", "Missing longUrl")
        }
    }

    @Test
    fun urlShortenTestWrongUrl() = testApplication {
        val checkParameter = client.get("/url/shorten?longUrl=htps://fds")
        assertEquals(HttpStatusCode.BadRequest, checkParameter.status)
        expectJSON(checkParameter.bodyAsText()) {
            propertyPresent("errorMessage")
            property("errorMessage", "longUrl should be a valid URL, like http(s)://www.example.com")
        }
    }

    @Test
    fun urlShortenUrlAdded() = testApplication {
        val checkUrl = "https://www.example.com"
        val urlAdded = client.get("/url/shorten?longUrl=$checkUrl")
        assertEquals(HttpStatusCode.Created, urlAdded.status)
        expectJSON(urlAdded.bodyAsText()) {
            propertyPresent("longUrl")
            propertyPresent("shortUrl")
            property("longUrl", checkUrl)
        }
    }
}

class UrlExpandTest {
    @Test
    fun urlExpandTestMissingParameter() = testApplication {
        val checkParameter = client.get("/url/expand")
        assertEquals(HttpStatusCode.BadRequest, checkParameter.status)
        expectJSON(checkParameter.bodyAsText()) {
            propertyPresent("errorMessage")
            property("errorMessage", "Missing shortUrl")
        }
    }

    @Test
    fun urlExpandTestWrongUrl() = testApplication {
        val checkParameter = client.get("/url/expand?shortUrl=htps://fds")
        assertEquals(HttpStatusCode.BadRequest, checkParameter.status)
        expectJSON(checkParameter.bodyAsText()) {
            propertyPresent("errorMessage")
            property("errorMessage", "shortUrl should be a valid short URL, like http(s)://example.com/u/xxxxxxx")
        }
    }

    @Test
    fun urlExpandUrl() = testApplication {
        val checkUrl = "https://www.example.com"
        val shortUrlResponse = client.get("/url/shorten?longUrl=$checkUrl")
        assertEquals(HttpStatusCode.Created, shortUrlResponse.status)
        var shortUrl = Json.decodeFromString<ResponseUrl>(shortUrlResponse.bodyAsText()).shortUrl
        val urlExpandResponse = client.get("/url/expand?shortUrl=$shortUrl")
        assertEquals(HttpStatusCode.OK, urlExpandResponse.status)
        expectJSON(urlExpandResponse.bodyAsText()) {
            propertyPresent("longUrl")
            propertyPresent("shortUrl")
            property("longUrl", checkUrl)
            property("shortUrl", shortUrl)
        }
    }

    @Test
    fun urlExpandWrongUrl() = testApplication {
        val checkUrl = "https://www.example.com"
        val shortUrlResponse = client.get("/url/shorten?longUrl=$checkUrl")
        assertEquals(HttpStatusCode.Created, shortUrlResponse.status)
        var shortUrl = Json.decodeFromString<ResponseUrl>(shortUrlResponse.bodyAsText()).shortUrl
        val urlExpandResponse = client.get("/url/expand?shortUrl=$shortUrl 1")
        assertEquals(HttpStatusCode.BadRequest, urlExpandResponse.status)
        expectJSON(urlExpandResponse.bodyAsText()) {
            propertyPresent("errorMessage")
            property("errorMessage", "No valid url was asked")
        }
    }
}

class UrlRedirectTestValidUrl {
    @Test
    fun urlRedirectTestValidUrl() = testApplication {
        val checkParameter = client.get("/u/123")
        assertEquals(HttpStatusCode.BadRequest, checkParameter.status)
        expectJSON(checkParameter.bodyAsText()) {
            propertyPresent("errorMessage")
            property("errorMessage", "No valid url was asked")
        }
    }
}
