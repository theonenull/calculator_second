package com.example.plugins

import com.example.Calculate
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureRouting(db:Database) {
//    install(ContentNegotiation) {
//        gson {
//            // Configure Gson here
//        }
//    }
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        post("/calculate") {
            val response = call.receiveParameters()
            val calculationFormula = response["calculationFormula"] ?: ""
            val result = response["result"] ?: ""
            transaction(db){
                val stPete = Calculate.insert {
                    it[Calculate.calculationFormula] = calculationFormula
                    it[Calculate.result] = result
                } get Calculate.id
            }
            call.respond(
                mapOf<String,String>(
                    "state" to "ok"
                )
            )
        }
        get ("/history"){
            var stPete : List<History> = listOf()
            transaction(db){
                stPete = Calculate.selectAll().toList().map {
                    History(it[Calculate.result],it[Calculate.calculationFormula])
                }
            }
            call.respond(mapOf("list" to stPete))

        }

//        get("/rate"){
//            call response
//        }

        get("/depositInterestRate"){
            call.respond(
                mapOf(
                    "data" to depositInterestRateWithClass
                )
            )
        }

        get("/interestRate"){
            call.respond(
                interestRate
            )
        }
    }
}

//689

val interestRate = mapOf(
    0 to 0.50,
    3 to 2.85,
    6 to 3.05,
    12 to 3.25,
    24 to 4.15,
    36 to 4.75,
    60 to 5.25
)

val depositInterestRate = mapOf(
    6 to 5.85,
    12 to 6.83,
    36 to 6.40,
    60 to 6.65,

)

val depositInterestRateWithClass = listOf(
    DepositInterestRate(
        0,6,5.58
    ),
    DepositInterestRate(
        6,12,5.58
    ),
    DepositInterestRate(
        12,36,5.58
    ),
    DepositInterestRate(
        36,60,5.58
    ),
    DepositInterestRate(
        60,-1,5.58
    )
)

@Serializable
data class DepositInterestRate(
    var start : Int ,
    val end : Int,
    var rate : Double
)

@Serializable
data class History(
    val result : String,
    val calculationFormula : String
)