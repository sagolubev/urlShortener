package com.sigius

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import net.pwall.json.test.JSONExpect.Companion.expectJSON
import kotlin.test.Test
import kotlin.test.assertEquals

class RootPathTest {
    @Test
    fun rootPathTest() = testApplication {
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        expectJSON(response.bodyAsText()) {
            property("appName", "urlShortener")
        }
    }
}

class UrlsPathTest {
    @Test
    fun rootPathTest() = testApplication {
        val response = client.get("/url")
        assertEquals(HttpStatusCode.NoContent, response.status)
        expectJSON(response.bodyAsText()) {
            property("noContentMessage", "No urls found")
        }
    }
}