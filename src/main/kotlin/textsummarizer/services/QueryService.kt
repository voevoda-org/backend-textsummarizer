package textsummarizer.services

import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.server.plugins.NotFoundException
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.slf4j.LoggerFactory
import textsummarizer.models.Queries
import textsummarizer.models.Query
import textsummarizer.models.devices
import textsummarizer.models.queries
import textsummarizer.plugins.DatabaseFactory.db
import textsummarizer.utils.HttpClient.openApiClient
import java.time.LocalDateTime
import java.util.*

private const val authUrl = "/models"
private const val queryUrl = "/chat/completions"

class QueryService {

    private val logger = LoggerFactory.getLogger("QueryService")

    suspend fun query(body: String, deviceId: UUID): String {
        logger.info("Calling AWS Lambda with $body")
        val result = openApiClient.post(queryUrl) {
            setBody(body)
        }
            .bodyAsText()
            .also {
                db.queries.add(
                    Query {
                        query = body
                        response = it
                        device = db.devices.find { it.id eq deviceId } ?: throw NotFoundException("DeviceId not found")
                        createdAt = LocalDateTime.now()
                    }
                )
            }
        logger.info("Done calling AWS Lambda with $result")
        return result
    }

    fun getQuery(queryId: Int): Query? {
        val result = db.queries.find { Queries.id eq queryId }
        logger.info("Found query $result")
        return result
    }
}