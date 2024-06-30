package textsummarizer.models.mapper

import textsummarizer.models.QueryInputChoiceDomainModel
import textsummarizer.models.QueryInputDomainModel
import textsummarizer.models.QueryInputMessageDomainModel
import textsummarizer.models.QueryInputUsageDomainModel
import textsummarizer.models.dto.QueryInputDto
import textsummarizer.models.dto.QueryInputMessageDto

object QueryInputDtoMapper {

    fun QueryInputDto.toQueryInputDomainModel() =
        QueryInputDomainModel(
            id = id,
            obj = obj,
            created = created,
            model = model,
            choices = choices.map {
                QueryInputChoiceDomainModel(
                    index = it.index, message = it.queryInputMessageDto.toQueryInputMessageDomainModel(),
                    logProbs = it.logProbs,
                    finishReason = it.finishReason
                )
            },
            usage = QueryInputUsageDomainModel(
                promptTokens = queryInputUsageDto.promptTokens,
                completionTokens = queryInputUsageDto.completionTokens,
                totalTokens = queryInputUsageDto.totalTokens
            )
        )

    private fun QueryInputMessageDto.toQueryInputMessageDomainModel() =
        QueryInputMessageDomainModel(
            role = role,
            content = content
        )
}