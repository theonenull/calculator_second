package com.example.plugins

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import io.ktor.util.*
import kotlinx.serialization.Serializable
import org.h2.util.JdbcUtils.serializer
import kotlin.test.Test

class RoutingKtTest {

    @Test
    fun testPostCalculate() = testApplication {
        application {
            configureRouting()
        }
        val client = HttpClient(Apache) {
            install(ContentNegotiation) {
                json()
            }
        }
        client.post("/calculate"){
            setBody(HelloWorld(result = "hello",calculationFormula = "world"))
            contentType(ContentType.Application.Json)
        }

    }
}

@Serializable
data class HelloWorld(
    val result: String,
    val calculationFormula : String
)