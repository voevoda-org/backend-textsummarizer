package textsummarizer.services

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.server.plugins.NotFoundException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.slf4j.LoggerFactory
import textsummarizer.models.Queries
import textsummarizer.models.Query
import textsummarizer.models.devices
import textsummarizer.models.dto.AuthenticateDto
import textsummarizer.models.queries
import textsummarizer.plugins.DatabaseFactory.db
import textsummarizer.utils.HttpClient.openApiClient
import java.time.LocalDateTime
import java.util.*

private const val authUrl = "/models"
private const val queryUrl = "/chat/completions"

class ChatGptService {

    private val logger = LoggerFactory.getLogger("QueryService")

    init {
        runBlocking { authenticate() }
    }

    private suspend fun authenticate(): AuthenticateDto {
        logger.info("Authenticating...")
        return openApiClient
            .get(authUrl)
            .bodyAsText()
            .let<String, AuthenticateDto> { json ->
                Json.decodeFromString(json)
            }.also {
                logger.info("Successfully authenticated")
            }
    }

    suspend fun query(body: String, deviceId: UUID): String {
        logger.info("Calling OpenApi with: $body")
        return openApiClient.post(queryUrl) {
            setBody(body)
        }
            .bodyAsText()   // TODO: Receive as <ChatGptResponse>
            .also {
                Query {
                    query = body
                    response = it
                    device = db.devices.find { it.id eq deviceId } ?: throw NotFoundException("DeviceId not found")
                    createdAt = LocalDateTime.now()
                }.let {
                    kotlin.runCatching {
                        db.queries.add(it)
                    }.onFailure { exception ->
                        logger.error("Encountered error when persisting query with $it", exception)
                    }.onSuccess {
                        logger.info("Added $it to database")
                    }
                }
            }
            .also {
                logger.info("OpenApi response: $it")
            }
    }

    fun getQuery(queryId: Int): Query? {
        logger.info("Searching for query with id: $queryId")
        val result = db.queries.find { Queries.id eq queryId }
        logger.info("Found query: $result")
        return result
    }
}