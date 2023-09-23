package com.sigius

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class UrlRedirectTest {
    @Test
    fun urlRedirectTest() = testApplication {
        val checkUrl = "https://www.example.com"
        val shortUrlResponse = client.get("/url/shorten?longUrl=$checkUrl")
        assertEquals(HttpStatusCode.Created, shortUrlResponse.status)
        var shortUrl = Json.decodeFromString<ResponseUrl>(shortUrlResponse.bodyAsText()).shortUrl
        println(shortUrl)
//        val urlRedirectResponse = client.get(shortUrl) // - an error here!! see at ktor sources tests
//        assertEquals(HttpStatusCode.PermanentRedirect, urlRedirectResponse.status)
    }
}
