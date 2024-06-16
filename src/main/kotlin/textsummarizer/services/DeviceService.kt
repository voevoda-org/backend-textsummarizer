package textsummarizer.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ktorm.entity.add
import org.slf4j.LoggerFactory
import textsummarizer.models.Device
import textsummarizer.models.devices
import textsummarizer.plugins.DatabaseFactory.db

private val logger = LoggerFactory.getLogger("DeviceService")


class DeviceService {

    suspend fun save(device: Device): Unit = withContext(Dispatchers.IO) {
        this.runCatching {
            db.devices.add(device)
        }.onSuccess {
            logger.info("Saving device id: ${device.id}")
        }.onFailure {
            logger.error("Error saving device id: ${device.id}", it)
        }.getOrNull()
    }
}