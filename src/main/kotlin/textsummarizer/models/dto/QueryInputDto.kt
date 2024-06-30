package textsummarizer.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QueryInputDto(
    @SerialName("id") val id: String,
    @SerialName("object") val obj: String,
    @SerialName("created") val created: Long,
    @SerialName("model") val model: String,
    @SerialName("choices") val choices: List<QueryInputChoiceDto>,
    @SerialName("usage") val queryInputUsageDto: QueryInputUsageDto,
    @SerialName("system_fingerprint") val systemFingerprint: String? = null,
)

@Serializable
data class QueryInputChoiceDto(
    @SerialName("index") val index: Int,
    @SerialName("message") val queryInputMessageDto: QueryInputMessageDto,
    @SerialName(
        "logprobs"
    ) val logProbs: String? = null,  // You might want to replace 'Any?' with the actual type of logprobs
    @SerialName("finish_reason") val finishReason: String,
)

@Serializable
data class QueryInputMessageDto(
    @SerialName("role") val role: String,
    @SerialName("content") val content: String,
)

@Serializable
data class QueryInputUsageDto(
    @SerialName("prompt_tokens") val promptTokens: Int,
    @SerialName("completion_tokens") val completionTokens: Int,
    @SerialName("total_tokens") val totalTokens: Int,
)