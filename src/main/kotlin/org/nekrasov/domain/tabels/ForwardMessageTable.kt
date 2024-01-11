package org.nekrasov.domain.tabels

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object ForwardMessageTable : Table("forward_message") {
    val mainId: Column<Long> = long("main_id").references(MessageTable.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val forwardId: Column<Long> = long("forward_id").references(MessageTable.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val answer: Column<Boolean> = bool("answer")
    override val primaryKey = PrimaryKey(mainId, forwardId)
}