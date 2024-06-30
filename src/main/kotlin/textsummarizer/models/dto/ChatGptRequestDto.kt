package textsummarizer.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatGptRequestDto(
    @SerialName("model") val model: String,
    @SerialName("messages") val messages: List<ChatGptRequestMessageDto>,
    @SerialName("temperature") val temperature: Float,
    @SerialName(
        "response_format"
    ) val responseFormat: ChatGptResponseFormatDto = ChatGptResponseFormatDto(),
)

@Serializable
data class ChatGptRequestMessageDto(
    @SerialName("role") val role: String,
    @SerialName("content") val content: String
)

@Serializable
data class ChatGptResponseFormatDto(
    @SerialName("type") val type: String = "json_object",
)