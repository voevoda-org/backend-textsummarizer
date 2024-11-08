package textsummarizer.models.dto.chatGPT.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatGPTQueryResponseDto(
    @SerialName("id") val id: String,
    @SerialName("object") val obj: String,
    @SerialName("created") val created: Long,
    @SerialName("model") val model: String,
    @SerialName("choices") val choices: List<ChatGPTResponseChoiceDto>,
    @SerialName("usage") val chatGPTResponseUsageDto: ChatGPTResponseUsageDto,
    @SerialName("system_fingerprint") val systemFingerprint: String? = null,
)

@Serializable
data class ChatGPTResponseChoiceDto(
    @SerialName("index") val index: Int,
    @SerialName("message") val chatGPTResponseMessageDto: ChatGPTResponseMessageDto,
    @SerialName(
        "logprobs"
    ) val logProbs: String? = null,  // You might want to replace 'Any?' with the actual type of logprobs
    @SerialName("finish_reason") val finishReason: String,
)

@Serializable
data class ChatGPTResponseMessageDto(
    @SerialName("role") val role: String,
    @SerialName("content") val content: String,
)

@Serializable
data class ChatGPTResponseUsageDto(
    @SerialName("prompt_tokens") val promptTokens: Int,
    @SerialName("completion_tokens") val completionTokens: Int,
    @SerialName("total_tokens") val totalTokens: Int,
)