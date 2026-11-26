package com.aura.app.data.remote

import okhttp3.Call
import okhttp3.Connection
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.concurrent.TimeUnit

class AuthInterceptorTest {

    private class FakeChain(
        private var currentRequest: Request,
        private val statusCode: Int,
    ) : Interceptor.Chain {
        var proceededRequest: Request? = null

        override fun request(): Request = currentRequest

        override fun proceed(request: Request): Response {
            proceededRequest = request
            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(statusCode)
                .message("Test")
                .body("{}".toResponseBody("application/json".toMediaType()))
                .build()
        }

        override fun connection(): Connection? = null
        override fun call(): Call = throw UnsupportedOperationException()
        override fun connectTimeoutMillis(): Int = 15_000
        override fun withConnectTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this
        override fun readTimeoutMillis(): Int = 30_000
        override fun withReadTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this
        override fun writeTimeoutMillis(): Int = 30_000
        override fun withWriteTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this
    }

    @Test
    fun `menyisipkan header Bearer pada endpoint terproteksi dan memicu onUnauthorized saat 401`() {
        var unauthorizedCount = 0
        val interceptor = AuthInterceptor(
            tokenProvider = { "token-rahasia-123" },
            onUnauthorized = { unauthorizedCount++ },
        )
        val chain = FakeChain(
            Request.Builder().url("https://example.test/api/v1/auth/me").build(),
            statusCode = 401,
        )

        interceptor.intercept(chain)

        assertEquals("Bearer token-rahasia-123", chain.proceededRequest?.header("Authorization"))
        assertEquals(1, unauthorizedCount)
    }

    @Test
    fun `tidak menyisipkan token atau memicu onUnauthorized pada endpoint login publik`() {
        var unauthorizedCount = 0
        val interceptor = AuthInterceptor(
            tokenProvider = { "token-lama" },
            onUnauthorized = { unauthorizedCount++ },
        )
        val chain = FakeChain(
            Request.Builder().url("https://example.test/api/v1/auth/owner/login").build(),
            statusCode = 401,
        )

        interceptor.intercept(chain)

        assertNull(chain.proceededRequest?.header("Authorization"))
        assertEquals(0, unauthorizedCount)
    }

    @Test
    fun `respons 200 tidak memicu onUnauthorized`() {
        var unauthorizedCount = 0
        val interceptor = AuthInterceptor(
            tokenProvider = { "token-aktif" },
            onUnauthorized = { unauthorizedCount++ },
        )
        val chain = FakeChain(
            Request.Builder().url("https://example.test/api/v1/kasir").build(),
            statusCode = 200,
        )

        interceptor.intercept(chain)

        assertEquals("Bearer token-aktif", chain.proceededRequest?.header("Authorization"))
        assertEquals(0, unauthorizedCount)
    }
}
