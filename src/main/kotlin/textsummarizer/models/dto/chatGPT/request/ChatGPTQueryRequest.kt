package textsummarizer.models.dto.chatGPT.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatGPTQueryRequest(
    @SerialName("model") val model: String,
    @SerialName("messages") val messages: List<ChatGPTQueryOutputMessageDto>,
    @SerialName("temperature") val temperature: Float,
    @SerialName("response_format")
    val responseFormat: ChatGPTQueryOutputResponseFormatDto = ChatGPTQueryOutputResponseFormatDto(),
)

@Serializable
data class ChatGPTQueryOutputMessageDto(
    @SerialName("role") val role: String,
    @SerialName("content") val content: String,
)

@Serializable
data class ChatGPTQueryOutputResponseFormatDto(
    @SerialName("type") val type: String = "json_object",
)