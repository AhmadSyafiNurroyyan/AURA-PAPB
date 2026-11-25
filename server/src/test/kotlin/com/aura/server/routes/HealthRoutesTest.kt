package com.aura.server.routes

import com.aura.server.configureHttp
import com.aura.server.plugins.ApiException
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import io.ktor.server.routing.get
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HealthRoutesTest {
    @Test
    fun `health endpoint returns the public health response without a database`() = testApplication {
        application { configureHttp() }

        val response = client.get("/health")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("""{"status":"ok"}""", response.bodyAsText())
    }

    @Test
    fun `StatusPages returns the common error shape`() = testApplication {
        application {
            configureHttp {
                get("/test-error") {
                    throw ApiException(HttpStatusCode.BadRequest, "INPUT_TIDAK_VALID", "Input tidak valid.")
                }
            }
        }

        val response = client.get("/test-error")
        val body = response.bodyAsText()

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(body.contains(""""kode":"INPUT_TIDAK_VALID"""))
        assertTrue(body.contains(""""pesan":"Input tidak valid."""))
    }

    @Test
    fun `unexpected errors return generic text without exception details`() = testApplication {
        application {
            configureHttp {
                get("/test-error") {
                    error("confidential detail")
                }
            }
        }

        val response = client.get("/test-error")
        val body = response.bodyAsText()

        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertTrue(body.contains("KESALAHAN_INTERNAL"))
        assertFalse(body.contains("confidential detail"))
    }
}
