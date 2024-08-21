package textsummarizer.models

sealed class ChatGPTQueryType(
    val systemPrompt: String,
    open val queryPrompt: String,
) {

    class Question(
        override var queryPrompt: String,
    ) : ChatGPTQueryType(
        systemPrompt = "You are a question generator. You respond with a JSON where you generate questions with answers based on the following text. Make Question/Answer pairs. Start with the 'questions' key and then add 'question' and 'answer' keys for each question.",
        queryPrompt = queryPrompt
    )

    class MultipleChoice(
        override var queryPrompt: String,
    ) : ChatGPTQueryType(
        systemPrompt = "You are a multiple choice question generator. You generate questions with 4 answers and give the correct option afterward. Create questions out of the following text.",
        queryPrompt = queryPrompt
    )

    class Summarize(
        override var queryPrompt: String,
    ) : ChatGPTQueryType(
        systemPrompt = "You are a text summarizer. Generate a summary of the following text and reply with a JSON where you give a 'title' and the 'summary' as keys.",
        queryPrompt = queryPrompt
    )

    class Translate(
        override var queryPrompt: String,
        language: String = "ENG",
    ) : ChatGPTQueryType(
        systemPrompt = "You are a text translator. Translate the following text into $language and give the answer as JSON. Put the translated text under 'translation' key.",
        queryPrompt = queryPrompt
    )

    class Essay(
        override var queryPrompt: String,
    ) : ChatGPTQueryType(
        systemPrompt = "You are a professional essay writer. Write an essay about the following text and reply in JSON with the 'essay' key.",
        queryPrompt = queryPrompt
    )
}