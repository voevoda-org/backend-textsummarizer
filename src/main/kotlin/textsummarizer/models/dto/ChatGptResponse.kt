package textsummarizer.models.dto

import kotlinx.serialization.Serializable

@Serializable
sealed class ChatGptResponse {
    abstract fun toResponse(): String
}

@Serializable
class EssayResult(val essay: String) : ChatGptResponse() {
    override fun toResponse(): String = essay
}

@Serializable
class QuestionsResult(
    val questions: List<Question>,
) : ChatGptResponse() {
    override fun toResponse(): String = questions.joinToString("\n") { "\n${it.question}\n${it.answer}" }
}

@Serializable
data class Question(
    val question: String,
    val answer: String,
)


@Serializable
class SummaryResult(
    val title: String,
    val summary: String,
) : ChatGptResponse() {
    override fun toResponse(): String = "Title: $title\nSummary: $summary"
}

@Serializable
class TranslationResult(
    val translation: String,
) : ChatGptResponse() {
    override fun toResponse(): String = translation
}