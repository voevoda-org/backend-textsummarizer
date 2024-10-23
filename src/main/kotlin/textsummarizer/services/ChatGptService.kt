package textsummarizer.services

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.server.plugins.NotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json.Default.decodeFromString
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.slf4j.LoggerFactory
import textsummarizer.models.ChatGPTQueryType
import textsummarizer.models.ChatGPTResult
import textsummarizer.models.Queries
import textsummarizer.models.Query
import textsummarizer.models.devices
import textsummarizer.models.dto.ChatGPTQueryDto
import textsummarizer.models.dto.ChatGPTQueryOutputMessageDto
import textsummarizer.models.dto.ChatGPTQueryResponseDto
import textsummarizer.models.dto.ChatGPTRequestFromMobileDto
import textsummarizer.models.dto.QueryType
import textsummarizer.models.dto.request.ChatGptRequestDto
import textsummarizer.models.dto.request.ChatGptRequestMessageDto
import textsummarizer.models.dto.response.EssayResult
import textsummarizer.models.dto.response.QuestionsResult
import textsummarizer.models.dto.response.SummaryResult
import textsummarizer.models.dto.response.TranslationResult
import textsummarizer.models.mapper.ChatGPTQueryResponseDtoMapper.toChatGPTResult
import textsummarizer.models.queries
import textsummarizer.plugins.DatabaseFactory.db
import textsummarizer.utils.ChatGPTHttpClient.openApiClient
import java.time.LocalDateTime
import java.util.*

private const val queryUrl = "https://api.openai.com/v1/chat/completions"

class ChatGptService {

    private val logger = LoggerFactory.getLogger("ChatGptService")

    suspend fun query(chatGPTRequestFromMobileDto: ChatGPTRequestFromMobileDto, deviceId: UUID): String {
        val chatGPTQueryDto = chatGPTRequestFromMobileDto.defineChatGPTQueryType().toChatGPTQuery()
        logger.info("Calling OpenApi with: ${chatGPTRequestFromMobileDto.queryText}")

        return openApiClient.post(queryUrl) {
            setBody(chatGPTQueryDto)
        }
            .also { logger.debug("Received {}", it) }
            .body<ChatGPTQueryResponseDto>()
            .toChatGPTResult()
            .let { handleChatGPTResult(chatGPTRequestFromMobileDto.queryType, it) }
            .also { chatGptResponse ->
                saveQueryResultToDB(
                    queryText = chatGPTRequestFromMobileDto.queryText,
                    response = chatGptResponse,
                    deviceId = deviceId
                )
            }
            .also {
                logger.info("OpenApi response: $it")
            }
    }

    private fun handleChatGPTResult(
        queryType: QueryType,
        result: ChatGPTResult
    ): String = try {
        when (queryType) {
            QueryType.SUMMARIZE -> decodeFromString(SummaryResult.serializer(), result.choices[0].message.content)
            QueryType.ESSAY -> decodeFromString(EssayResult.serializer(), result.choices[0].message.content)
            QueryType.QUESTION -> decodeFromString(QuestionsResult.serializer(), result.choices[0].message.content)
            QueryType.TRANSLATE -> decodeFromString(
                TranslationResult.serializer(),
                result.choices[0].message.content
            )
        }.toText()
    } catch (e: Exception) {
        logger.error("Failed to parse message content: ${result.choices[0].message.content}", e)
        result.choices[0].message.content
    }

    private fun saveQueryResultToDB(queryText: String, response: String, deviceId: UUID) {
        Query {
            this.query = queryText
            this.response = response
            // TODO propagate the NotFound to the end
            this.device = db.devices.find { it.id eq deviceId } ?: throw NotFoundException("DeviceId not found")
            this.createdAt = LocalDateTime.now()
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

    suspend fun getQuery(queryId: Int): String? {
        return withContext(Dispatchers.IO) {
            this.runCatching {
                db.queries.find { Queries.id eq queryId }
            }
                .onSuccess { logger.info("Found query: $it") }
                .onFailure { logger.error("Exception", it) }
        }
            .getOrNull()
            ?.query
    }

    private fun ChatGPTRequestFromMobileDto.defineChatGPTQueryType() =
        when (this.queryType) {
            QueryType.SUMMARIZE -> ChatGPTQueryType.Summarize(this.queryText)
            QueryType.ESSAY -> ChatGPTQueryType.Essay(this.queryText)
            QueryType.QUESTION -> ChatGPTQueryType.Question(this.queryText)
            QueryType.TRANSLATE -> ChatGPTQueryType.Translate(this.queryText)
        }

    private fun ChatGPTQueryType.toChatGPTQuery() =
        ChatGPTQueryDto(
            model = "gpt-3.5-turbo",
            messages = listOf(
                ChatGPTQueryOutputMessageDto(
                    role = "system",
                    content = this.systemPrompt
                ),
                ChatGPTQueryOutputMessageDto(
                    role = "user",
                    content = this.queryPrompt
                ),
            ),
            temperature = 0.7f
        )
}

// TESTING
private fun String.toQueryDto(): ChatGptRequestDto {
    return ChatGptRequestDto(
        model = "gpt-3.5-turbo",
        messages = listOf(
            ChatGptRequestMessageDto(
                role = "system",
                content = "You are a text summarizer"
            ),
            ChatGptRequestMessageDto(
                role = "user",
                content = "Give me a summary of the following text in 20 words or less:"
            ),
            ChatGptRequestMessageDto(
                role = "user",
                content = this
            )
        ),
        temperature = 0.7f,
    )
}
