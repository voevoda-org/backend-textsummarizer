package textsummarizer.services

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.slf4j.LoggerFactory
import textsummarizer.models.Queries
import textsummarizer.models.Query
import textsummarizer.models.queries
import textsummarizer.plugins.DatabaseFactory.db

private const val url = "MOCK"

class QueryService {

    private val logger = LoggerFactory.getLogger("QueryService")
    private val client = HttpClient(CIO)

    suspend fun query(body: String): String {
        logger.info("Calling AWS Lambda with $body")
        val result = client.post(url) {
            headers {
                append(HttpHeaders.ContentType, ContentType.Application.Json)
                append(HttpHeaders.Authorization, "Bearer token")
            }
            setBody(body)
        }.bodyAsText()
        logger.info("Done calling AWS Lambda with $result")
        return result
    }

    fun getQuery(queryId: Int): Query? {
        val result = db.queries.find { Queries.id eq queryId }
        logger.info("Found query $result")
        return result
    }
}