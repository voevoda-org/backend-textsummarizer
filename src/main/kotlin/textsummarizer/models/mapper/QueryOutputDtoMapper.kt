package textsummarizer.models.mapper

import textsummarizer.models.QueryOutputDomainModel
import textsummarizer.models.QueryOutputMessageDomainModel
import textsummarizer.models.QueryOutputResponseFormatDomainModel
import textsummarizer.models.dto.ChatGPTQuery
import textsummarizer.models.dto.QueryOutputMessageDto
import textsummarizer.models.dto.QueryOutputResponseFormatDto

object QueryOutputDtoMapper {

    fun ChatGPTQuery.toQueryOutputDomainModel() = QueryOutputDomainModel(
        model = model,
        messages = messages.map {
            QueryOutputMessageDomainModel(
                role = it.role,
                content = it.content
            )
        },
        temperature = temperature,
        responseFormat = QueryOutputResponseFormatDomainModel(
            type = responseFormat.type
        )
    )

    fun QueryOutputDomainModel.toQueryOutputDto() = ChatGPTQuery(
        model = model,
        messages = messages.map {
            QueryOutputMessageDto(
                role = it.role,
                content = it.content
            )
        },
        temperature = temperature,
        responseFormat = QueryOutputResponseFormatDto(
            type = responseFormat.type
        )
    )
}