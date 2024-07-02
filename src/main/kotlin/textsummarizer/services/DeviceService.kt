package textsummarizer.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.any
import org.ktorm.entity.find
import org.slf4j.LoggerFactory
import textsummarizer.models.Device
import textsummarizer.models.devices
import textsummarizer.plugins.DatabaseFactory.db
import java.util.*

class DeviceService {

    private val logger = LoggerFactory.getLogger("DeviceService")

    suspend fun save(device: Device): Unit = withContext(Dispatchers.IO) {
        this.runCatching {
            db.devices.add(device)
        }.onSuccess {
            logger.info("Saving device id: ${device.id}")
        }.onFailure {
            logger.error("Error saving device id: ${device.id}", it)
        }.getOrNull()
    }

    suspend fun findById(deviceId : UUID) = withContext(Dispatchers.IO) {
        this.runCatching {
            db.devices.find { it.id eq deviceId }
        }.onFailure {
            logger.error("Error finding device: $deviceId", it)
        }
            .getOrNull()
    }
    
    suspend fun exists(deviceId : UUID) = withContext(Dispatchers.IO) {
        db.devices.any { it.id eq deviceId }
    }
}