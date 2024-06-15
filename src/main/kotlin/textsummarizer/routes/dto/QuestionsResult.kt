package textsummarizer.routes.dto

import kotlinx.serialization.Serializable

@Serializable
data class QuestionsResult(
    val questions: List<Question>,
) {
    override fun toString(): String {
        return "I created the following questions:\n" +
                questions.joinToString("\n") { "\n${it.question}\n${it.answer}" }
    }
}

@Serializable
data class Question(
    val question: String,
    val answer: String,
)