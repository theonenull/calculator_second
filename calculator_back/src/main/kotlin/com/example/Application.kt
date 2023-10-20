package com.example

import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.math.max

fun main() {
    val db = Database.connect("jdbc:mysql://localhost:3306/test", driver = "com.mysql.cj.jdbc.Driver", user = "root", password = "-----------")
    transaction(db){
        SchemaUtils.create(Calculate)
    }
    embeddedServer(Netty, port = 8082, host = "0.0.0.0", module = {
        this.module(db)
    })
        .start(wait = true)
}

fun Application.module(db:Database) {
    configureSecurity()
    configureSerialization()
    configureDatabases()
    configureRouting(db)
}

//object Cities: IntIdTable() {
//    val calculationFormula = varchar("calculationFormula", 50)
//    val result = varchar("result", 50 )
//}
object Calculate : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val calculationFormula: Column<String> = varchar("calculationFormula", 50)
    val result : Column<String> = varchar("result", 50 )
    override val primaryKey = PrimaryKey(id, name = "PK_Calculate_ID")
}

