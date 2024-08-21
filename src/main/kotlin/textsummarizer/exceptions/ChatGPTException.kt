package textsummarizer.exceptions

import io.ktor.http.HttpStatusCode

abstract class ChatGPTException(
    override val errorDescription: String,
    override val statusCodeToReturn: HttpStatusCode,
    val chatGptStatusCode: HttpStatusCode
) : Exception(errorDescription, statusCodeToReturn)

class ChatGPTApiAuthorizationException(
    statusCode: HttpStatusCode,
) : ChatGPTException(
    errorDescription = "Bearer token incorrect/missing.",
    statusCodeToReturn = HttpStatusCode.InternalServerError,
    chatGptStatusCode = statusCode
)

class ChatGPTGenericErrorException(
    statusCode: HttpStatusCode,
) : ChatGPTException(
    errorDescription = "Something went wrong.",
    statusCodeToReturn = HttpStatusCode.InternalServerError,
    chatGptStatusCode = statusCode
)