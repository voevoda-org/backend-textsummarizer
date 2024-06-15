package textsummarizer.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import textsummarizer.services.QueryService

private val service = QueryService()

fun Route.queriesRoute() {
    route("/v1/queries/") {
        post {
            val queryDto = call.receive<QueryDto>()
            runBlocking {
                call.respond(service.query(queryDto.queryText))
            }
        }

        route("{id}") {
            get {
                call.parameters["id"]?.toIntOrNull()
                    ?.let {
                        call.respond(service.getQuery(it) ?: call.respond(HttpStatusCode.NotFound))
                    } ?: call.respond(HttpStatusCode.BadRequest, "Invalid id")
            }
        }
    }
}

data class QueryDto(
    val queryText: String,
    val queryType: String
)