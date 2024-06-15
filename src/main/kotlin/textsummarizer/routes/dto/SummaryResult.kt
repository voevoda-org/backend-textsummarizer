package textsummarizer.routes.dto

import kotlinx.serialization.Serializable

@Serializable
data class SummaryResult(
    val title: String,
    val summary: String,
) {
    override fun toString(): String {
        return "Title: $title\nSummary: $summary"
    }
}