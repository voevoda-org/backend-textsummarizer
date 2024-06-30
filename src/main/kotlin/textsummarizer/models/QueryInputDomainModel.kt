package textsummarizer.models

data class QueryInputDomainModel(
    val id: String,
    val obj: String,
    val created: Long,
    val model: String,
    val choices: List<QueryInputChoiceDomainModel>,
    val usage: QueryInputUsageDomainModel,
    val systemFingerprint: String? = null,
)

data class QueryInputChoiceDomainModel(
    val index: Int,
    val message: QueryInputMessageDomainModel,
    val logProbs: String? = null,  // You might want to replace 'Any?' with the actual type of logprobs
    val finishReason: String,
)

data class QueryInputMessageDomainModel(
    val role: String,
    val content: String,
)

data class QueryInputUsageDomainModel(
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int,
)