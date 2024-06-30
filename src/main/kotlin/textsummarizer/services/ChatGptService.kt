package textsummarizer.services

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.server.plugins.NotFoundException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.decodeFromString
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.slf4j.LoggerFactory
import textsummarizer.models.ChatGPTQuery
import textsummarizer.models.Queries
import textsummarizer.models.Query
import textsummarizer.models.devices
import textsummarizer.models.dto.ChatGptRequestDto
import textsummarizer.models.dto.ChatGptRequestMessageDto
import textsummarizer.models.dto.ChatGptResponse
import textsummarizer.models.dto.EssayResult
import textsummarizer.models.dto.MobileQueryDto
import textsummarizer.models.dto.QueryInputDto
import textsummarizer.models.dto.QueryOutputDto
import textsummarizer.models.dto.QueryOutputMessageDto
import textsummarizer.models.dto.QueryType
import textsummarizer.models.dto.QuestionsResult
import textsummarizer.models.dto.SummaryResult
import textsummarizer.models.dto.TranslationResult
import textsummarizer.models.mapper.QueryInputDtoMapper.toQueryInputDomainModel
import textsummarizer.models.queries
import textsummarizer.plugins.DatabaseFactory.db
import textsummarizer.utils.HttpClient.openApiClient
import java.time.LocalDateTime
import java.util.*

//private const val queryUrl = "/chat/completions"
private const val queryUrl = "https://api.openai.com/v1/chat/completions"

class ChatGptService {

    private val logger = LoggerFactory.getLogger("QueryService")

    suspend fun query(mobileQueryDto: MobileQueryDto, deviceId: UUID): ChatGptResponse {
        val queryOutputDto = mobileQueryDto.createQueryContent().toQueryOutputDto()
        logger.info("Calling OpenApi with: ${mobileQueryDto.queryText}")
        return openApiClient.post(queryUrl) {
            setBody(queryOutputDto)
        }
            .body<QueryInputDto>()
            .toQueryInputDomainModel()
            .let {
                //Json.decodeFromString<ChatGptResponse>(it.choices[0].message.content)
                when(mobileQueryDto.queryType){
                    QueryType.SUMMARIZE -> decodeFromString(SummaryResult.serializer(), it.choices[0].message.content)
                    QueryType.ESSAY -> decodeFromString(EssayResult.serializer(), it.choices[0].message.content)
                    QueryType.QUESTION -> decodeFromString(QuestionsResult.serializer(), it.choices[0].message.content)
                    QueryType.TRANSLATE -> decodeFromString(TranslationResult.serializer(), it.choices[0].message.content)
                }
            }
            .also {
                persistToDatabase(
                    queryText = mobileQueryDto.queryText,
                    response = it.toResponse(),
                    deviceId = deviceId
                )
                logger.info("OpenApi response: $it")
            }
//            .let { json ->
//                Json.decodeFromString(json)
//            }
    }

    private fun persistToDatabase(queryText: String, response: String, deviceId: UUID) {
        Query {
            this.query = queryText
            this.response = response
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

    fun getQuery(queryId: Int): Query? {
        logger.info("Searching for query with id: $queryId")
        val result = db.queries.find { Queries.id eq queryId }
        logger.info("Found query: $result")
        return result
    }

    private fun MobileQueryDto.createQueryContent() =
        when (this.queryType) {
            QueryType.SUMMARIZE -> ChatGPTQuery.Summarize(this.queryText)
            QueryType.ESSAY -> ChatGPTQuery.Essay(this.queryText)
            QueryType.QUESTION -> ChatGPTQuery.Question(this.queryText)
            QueryType.TRANSLATE -> ChatGPTQuery.Translate(this.queryText)
        }

    private fun ChatGPTQuery.toQueryOutputDto() =
        QueryOutputDto(
            model = "gpt-3.5-turbo",
            messages = listOf(
                QueryOutputMessageDto(
                    role = "system",
                    content = this.systemPrompt
                ),
                QueryOutputMessageDto(
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
