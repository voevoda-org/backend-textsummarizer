package textsummarizer.models

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.*
import java.time.LocalDateTime

interface Query : Entity<Query> {
    companion object : Entity.Factory<Query>()

    val id: Int
    var query: String
    var response: String
    var device: Device
    var createdAt: LocalDateTime
}

object Queries : Table<Query>("queries") {
    val id = int("id").primaryKey().bindTo { it.id }
    val query = text("query").bindTo { it.query }
    val response = text("response").bindTo { it.response }
    val createdAt = datetime("created_at").bindTo { it.createdAt }
    val deviceId = uuid("device_id").references(Devices) { it.device }
}

val Database.queries get() = this.sequenceOf(Queries)