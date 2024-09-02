package textsummarizer.models

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.uuid
import java.time.LocalDateTime
import java.util.*

interface Device : Entity<Device> {
    companion object : Entity.Factory<Device>()

    var id: UUID
    var subscriptionId: UUID?
    var createdAt: LocalDateTime
}

object Devices : Table<Device>("devices") {
    val id = uuid("id").primaryKey().bindTo { it.id }
    val subscriptionId = uuid("subscription_id").bindTo { it.subscriptionId }
    val createdAt = datetime("created_at").bindTo { it.createdAt }
}

val Database.devices get() = this.sequenceOf(Devices)