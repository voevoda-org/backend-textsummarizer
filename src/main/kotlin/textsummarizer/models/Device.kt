package textsummarizer.models

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.*
import java.time.LocalDateTime
import java.util.UUID

interface Device : Entity<Device> {
    companion object : Entity.Factory<Device>()

    var id: UUID
    var createdAt: LocalDateTime
}

object Devices : Table<Device>("devices") {
    val id = uuid("id").primaryKey().bindTo { it.id }
    val createdAt = datetime("created_at").bindTo { it.createdAt }
}

val Database.devices get() = this.sequenceOf(Devices)