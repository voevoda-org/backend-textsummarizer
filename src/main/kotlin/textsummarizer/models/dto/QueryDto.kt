package textsummarizer.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QueryDto(
    @SerialName("model") val model: String,
    @SerialName("messages") val messages: List<QueryMessageDto>,
    @SerialName("temperature") val temperature: Float,
)

@Serializable
data class QueryMessageDto(
    @SerialName("role") val role: String,
    @SerialName("content") val content: String
)