package textsummarizer.models.dto.response

import kotlinx.serialization.Serializable

@Serializable
sealed class ChatGptResponse {
    abstract fun toText(): String
}

@Serializable
class EssayResult(private val essay: String) : ChatGptResponse() {
    override fun toText(): String = essay
}

@Serializable
class QuestionsResult(
    private val questions: List<Question>,
) : ChatGptResponse() {
    override fun toText(): String = questions.joinToString("\n") { "\n${it.question}\n${it.answer}" }
}

@Serializable
data class Question(
    val question: String,
    val answer: String,
)


@Serializable
class SummaryResult(
    private val title: String,
    private val summary: String,
) : ChatGptResponse() {
    override fun toText(): String = "Title: $title\nSummary: $summary"
}

@Serializable
class TranslationResult(
    private val translation: String,
) : ChatGptResponse() {
    override fun toText(): String = translation
}