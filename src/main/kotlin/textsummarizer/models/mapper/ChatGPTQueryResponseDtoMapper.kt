package textsummarizer.models.mapper

import textsummarizer.models.ChatGPTResult
import textsummarizer.models.ChatGPTResultChoice
import textsummarizer.models.ChatGPTResultMessage
import textsummarizer.models.ChatGPTResultUsage
import textsummarizer.models.dto.ChatGPTQueryResponseDto
import textsummarizer.models.dto.ChatGPTResponseMessageDto

object ChatGPTQueryResponseDtoMapper {

    fun ChatGPTQueryResponseDto.toChatGPTResult() =
        ChatGPTResult(
            id = id,
            obj = obj,
            created = created,
            model = model,
            choices = choices.map {
                ChatGPTResultChoice(
                    index = it.index,
                    message = it.chatGPTResponseMessageDto.toChatGPTResultMessage(),
                    logProbs = it.logProbs,
                    finishReason = it.finishReason
                )
            },
            usage = ChatGPTResultUsage(
                promptTokens = chatGPTResponseUsageDto.promptTokens,
                completionTokens = chatGPTResponseUsageDto.completionTokens,
                totalTokens = chatGPTResponseUsageDto.totalTokens
            )
        )

    private fun ChatGPTResponseMessageDto.toChatGPTResultMessage() =
        ChatGPTResultMessage(
            role = role,
            content = content
        )
}