package textsummarizer.models.dto.chatGPT.request

import kotlinx.serialization.Serializable

@Serializable
data class ChatGPTRequestFromMobileDto(
    val queryText: String,
    val queryType: QueryType
)

enum class QueryType {
    SUMMARIZE,
    ESSAY,
    QUESTION,
    TRANSLATE
}
