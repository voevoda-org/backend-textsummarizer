package textsummarizer.services

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.typesafe.config.ConfigFactory
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.server.config.HoconApplicationConfig
import io.ktor.server.plugins.NotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json.Default.decodeFromString
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.slf4j.LoggerFactory
import textsummarizer.models.ChatGPTQueryType
import textsummarizer.models.Queries
import textsummarizer.models.Query
import textsummarizer.models.devices
import textsummarizer.models.dto.chatGPT.request.ChatGPTQueryOutputMessageDto
import textsummarizer.models.dto.chatGPT.request.ChatGPTQueryRequest
import textsummarizer.models.dto.chatGPT.request.ChatGPTRequestFromMobileDto
import textsummarizer.models.dto.chatGPT.request.QueryType
import textsummarizer.models.dto.chatGPT.response.ChatGPTQueryResponseDto
import textsummarizer.models.dto.chatGPT.response.EssayResult
import textsummarizer.models.dto.chatGPT.response.QuestionsResult
import textsummarizer.models.dto.chatGPT.response.SummaryResult
import textsummarizer.models.dto.chatGPT.response.TranslationResult
import textsummarizer.models.queries
import textsummarizer.plugins.DatabaseFactory.db
import textsummarizer.utils.ChatGPTHttpClient.chatGptApiClient
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

private const val queryUrl = "https://api.openai.com/v1/chat/completions"

class ChatGptService {

    private val logger = LoggerFactory.getLogger("ChatGptService")
    private val config = HoconApplicationConfig(ConfigFactory.load())

    val openAI = OpenAI(
        token = config.property("chatGpt.authKey").getString()
    )

    suspend fun query(chatGPTRequestFromMobileDto: ChatGPTRequestFromMobileDto, deviceId: UUID): String {
        val chatGPTQueryDto = chatGPTRequestFromMobileDto
            .defineChatGPTQueryType()
            .toChatGPTQuery()
        logger.info("Calling OpenApi with: ${chatGPTRequestFromMobileDto.queryText}")

        return chatGptApiClient.post(queryUrl) {
            setBody(chatGPTQueryDto)
        }
            .also { logger.debug("Received {}", it.bodyAsText()) }
            .body<ChatGPTQueryResponseDto>()
            .extractContentAsJSONString()
            .also { logger.debug("Extracted content: $it") }
            .let {
                handleChatGPTResponse(
                    queryType = chatGPTRequestFromMobileDto.queryType,
                    chatGPTResponseContent = it
                )
            }
            .also { chatGptResponse ->
                saveQueryResultToDB(
                    queryText = chatGPTRequestFromMobileDto.queryText,
                    response = chatGptResponse,
                    deviceId = deviceId
                )
            }
            .also {
                logger.info("ChatGPT response: $it")
            }
    }

    fun queryStream(chatGPTRequestFromMobileDto: ChatGPTRequestFromMobileDto, deviceId: UUID): Flow<String> {
        val chatGPTQueryDto = chatGPTRequestFromMobileDto
            .defineChatGPTQueryType()
        logger.info("Calling OpenApi with: $chatGPTRequestFromMobileDto}")

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-4o-mini"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = chatGPTQueryDto.systemPrompt
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = chatGPTQueryDto.queryPrompt
                )
            )
        )

        return openAI.chatCompletions(chatCompletionRequest)
            .map { it.choices.first().delta?.content ?: "" }
            .map { it + Instant.now() }
    }

    private fun handleChatGPTResponse(
        queryType: QueryType,
        chatGPTResponseContent: String
    ): String = try {
        when (queryType) {
            QueryType.SUMMARIZE -> decodeFromString(SummaryResult.serializer(), chatGPTResponseContent)
            QueryType.ESSAY -> decodeFromString(EssayResult.serializer(), chatGPTResponseContent)
            QueryType.QUESTION -> decodeFromString(QuestionsResult.serializer(), chatGPTResponseContent)
            QueryType.TRANSLATE -> decodeFromString(TranslationResult.serializer(), chatGPTResponseContent)
        }.toText()
    } catch (e: Exception) {
        logger.error("Failed to parse message content: $chatGPTResponseContent", e)
        chatGPTResponseContent
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
        ChatGPTQueryRequest(
            // model = "gpt-3.5-turbo",
            model = "gpt-4o-mini",
            //model = "gpt-4o",
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

    fun ChatGPTQueryResponseDto.extractContentAsJSONString() = choices.first().chatGPTResponseMessageDto.content
}