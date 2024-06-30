package textsummarizer.models

data class QueryOutputDomainModel(
    val model: String,
    val messages: List<QueryOutputMessageDomainModel>,
    val temperature: Float,
    val responseFormat: QueryOutputResponseFormatDomainModel = QueryOutputResponseFormatDomainModel(),
)

data class QueryOutputMessageDomainModel(
    val role: String,
    val content: String,
)

data class QueryOutputResponseFormatDomainModel(
    val type: String = "json_object",
)