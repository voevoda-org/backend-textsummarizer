package textsummarizer.models

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.text
import java.time.LocalDateTime

interface Query : Entity<Query> {
    companion object : Entity.Factory<Query>()

    val id: Int
    var query: String
    var createdAt: LocalDateTime
}

object Queries : Table<Query>("queries") {
    val id = int("id").primaryKey().bindTo { it.id }
    val query = text("query").bindTo { it.query }
    val createdAt = datetime("created_at").bindTo { it.createdAt }
}

val Database.queries get() = this.sequenceOf(Queries)