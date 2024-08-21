package textsummarizer.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatGPTQuery(
    @SerialName("model") val model: String,
    @SerialName("messages") val messages: List<QueryOutputMessageDto>,
    @SerialName("temperature") val temperature: Float,
    @SerialName(
        "response_format"
    ) val responseFormat: QueryOutputResponseFormatDto = QueryOutputResponseFormatDto(),
)

@Serializable
data class QueryOutputMessageDto(
    @SerialName("role") val role: String,
    @SerialName("content") val content: String,
)

@Serializable
data class QueryOutputResponseFormatDto(
    @SerialName("type") val type: String = "json_object",
)