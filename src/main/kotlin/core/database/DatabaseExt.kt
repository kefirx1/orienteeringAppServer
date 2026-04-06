package pl.dev.bkwiatkowski.core.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

suspend fun <T> Database.dbQuery(block: suspend () -> T): T =
  withContext(Dispatchers.IO) {
    suspendTransaction(db = this@dbQuery) { block() }
  }

inline fun <reified T: Table> Database.initTable(table: T) {
  transaction(this) {
    SchemaUtils.create(table)
  }
}