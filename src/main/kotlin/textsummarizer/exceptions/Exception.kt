package textsummarizer.exceptions

import io.ktor.http.HttpStatusCode

sealed class Exception(
    open val errorDescription: String,
    open val statusCodeToReturn: HttpStatusCode
) : Throwable()