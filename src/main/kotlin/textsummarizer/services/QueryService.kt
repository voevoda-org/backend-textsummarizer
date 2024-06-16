package textsummarizer.services

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.plugins.*
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.slf4j.LoggerFactory
import textsummarizer.models.Queries
import textsummarizer.models.Query
import textsummarizer.models.devices
import textsummarizer.models.queries
import textsummarizer.plugins.DatabaseFactory.db
import java.time.LocalDateTime
import java.util.*

private const val aws_lambda_url = "MOCK"

class QueryService {

    private val logger = LoggerFactory.getLogger("QueryService")
    private val client = HttpClient(CIO)

    suspend fun query(body: String, deviceId: UUID): String {
        logger.info("Calling AWS Lambda with $body")
        val result = client.post(aws_lambda_url) {
            headers {
                append(HttpHeaders.ContentType, ContentType.Application.Json)
                append(HttpHeaders.Authorization, "Bearer token")
            }
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