package com.example.mockserver

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlin.math.roundToLong
import kotlin.random.Random

fun main() {
    val config = MockServerConfig.fromEnvironment()
    embeddedServer(Netty, port = config.port) {
        mockServerModule(config)
    }.start(wait = true)
}

fun Application.mockServerModule(config: MockServerConfig) {
    install(CallLogging)
    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/health") {
            call.respondText("ok")
        }

        route("/api/v1/mock-external/banks") {
            post("/{bankCode}/loan-limit") {
                val bankCode = call.parameters["bankCode"]
                if (bankCode == null) {
                    call.respond(HttpStatusCode.BadRequest, "bankCode is required")
                } else {
                    val request: LoanLimitRequest = call.receive()
                    delay(resolveLatencyMs(config, bankCode))

                    val success = Random.nextInt(1, 101) <= config.successRatePercent
                    val approvedLimit: Long? = if (success) {
                        minOf(request.requestedAmount, (request.annualIncome * 1.1).roundToLong())
                    } else {
                        null
                    }

                    val requestPayload =
                        """{"customer":{"id":"${request.borrowerId}"},"income":{"annual":${request.annualIncome}},"loan":{"requestedAmount":${request.requestedAmount}}}"""

                    val responsePayload = if (success) {
                        """{"status":{"code":"S000","message":"SUCCESS"},"data":{"limit":$approvedLimit}}"""
                    } else {
                        """{"status":{"code":"E503","message":"Provider timeout"}}"""
                    }

                    val response = MockExternalCallResponse(
                        httpStatus = if (success) 200 else 503,
                        responseCode = if (success) "S000" else "E503",
                        responseMessage = if (success) "Approved" else "Upstream timeout",
                        approvedLimit = approvedLimit,
                        requestPayload = requestPayload,
                        responsePayload = responsePayload,
                    )

                    call.respond(HttpStatusCode.OK, response)
                }
            }
        }
    }
}

private fun resolveLatencyMs(config: MockServerConfig, bankCode: String): Long {
    val bankNumber = bankCode.substringAfter("BANK-").toIntOrNull() ?: 0
    val slowStart = config.bankCount - config.slowBankCount + 1
    val isSlow = bankNumber >= slowStart && config.slowBankCount > 0

    val (minLatencyMs, maxLatencyMs) = if (isSlow) {
        config.slowMinLatencyMs to config.slowMaxLatencyMs
    } else {
        config.minLatencyMs to config.maxLatencyMs
    }

    val lower = minOf(minLatencyMs, maxLatencyMs)
    val upper = maxOf(minLatencyMs, maxLatencyMs)
    return Random.nextLong(lower, upper + 1)
}

@Serializable
data class LoanLimitRequest(
    val borrowerId: String,
    val annualIncome: Long,
    val requestedAmount: Long,
)

@Serializable
data class MockExternalCallResponse(
    val httpStatus: Int,
    val responseCode: String,
    val responseMessage: String,
    val approvedLimit: Long?,
    val requestPayload: String,
    val responsePayload: String,
)

data class MockServerConfig(
    val port: Int,
    val bankCount: Int,
    val minLatencyMs: Long,
    val maxLatencyMs: Long,
    val slowMinLatencyMs: Long,
    val slowMaxLatencyMs: Long,
    val slowBankCount: Int,
    val successRatePercent: Int,
) {
    companion object {
        fun fromEnvironment(): MockServerConfig {
            return MockServerConfig(
                port = envInt("MOCK_SERVER_PORT", 18080),
                bankCount = envInt("MOCK_BANK_COUNT", 50),
                minLatencyMs = envLong("MOCK_MIN_LATENCY_MS", 3_000),
                maxLatencyMs = envLong("MOCK_MAX_LATENCY_MS", 15_000),
                slowMinLatencyMs = envLong("MOCK_SLOW_MIN_LATENCY_MS", 30_000),
                slowMaxLatencyMs = envLong("MOCK_SLOW_MAX_LATENCY_MS", 45_000),
                slowBankCount = envInt("MOCK_SLOW_BANK_COUNT", 2),
                successRatePercent = envInt("MOCK_SUCCESS_RATE_PERCENT", 85),
            )
        }

        private fun envInt(name: String, defaultValue: Int): Int {
            return System.getenv(name)?.toIntOrNull() ?: defaultValue
        }

        private fun envLong(name: String, defaultValue: Long): Long {
            return System.getenv(name)?.toLongOrNull() ?: defaultValue
        }
    }
}
