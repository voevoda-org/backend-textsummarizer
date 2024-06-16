package textsummarizer.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class TranslationResult(
    val translation: String,
)