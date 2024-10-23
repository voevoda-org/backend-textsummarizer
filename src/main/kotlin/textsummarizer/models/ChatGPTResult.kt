package textsummarizer.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatGPTResult(
    val id: String,
    val obj: String,
    val created: Long,
    val model: String,
    val choices: List<ChatGPTResultChoice>,
    val usage: ChatGPTResultUsage,
    val systemFingerprint: String? = null,
)

@Serializable
data class ChatGPTResultChoice(
    val index: Int,
    val message: ChatGPTResultMessage,
    val logProbs: String? = null,  // You might want to replace 'Any?' with the actual type of logprobs
    val finishReason: String,
)

@Serializable
data class ChatGPTResultMessage(
    val role: String,
    val content: String,
)

@Serializable
data class ChatGPTResultUsage(
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int,
)