package textsummarizer.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthenticateDto(
    @SerialName("object") val obj: String,
    @SerialName("data") val data: List<AuthenticateDataDto>
)

@Serializable
data class AuthenticateDataDto(
    @SerialName("id") val id: String,
    @SerialName("object") val ob: String,
    @SerialName("created") val created: Long,
    @SerialName("owned_by") val ownedBy: String,
)