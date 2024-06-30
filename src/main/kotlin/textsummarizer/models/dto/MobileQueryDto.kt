package textsummarizer.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class MobileQueryDto(
    val queryText: String,
    val queryType: QueryType
)

enum class QueryType {
    SUMMARIZE,
    ESSAY,
    QUESTION,
    TRANSLATE
}
