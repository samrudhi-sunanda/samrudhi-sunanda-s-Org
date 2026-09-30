package com.example.data.gemini

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val modelUsed: String? = null,
    val groundingSources: List<GroundingCitation> = emptyList(),
    val isStreaming: Boolean = false,
    val isAudioTranscribed: Boolean = false
)

enum class MessageSender {
    USER,
    ASSISTANT,
    SYSTEM
}

data class GroundingCitation(
    val title: String,
    val uri: String? = null,
    val sourceType: GroundingType
)

enum class GroundingType {
    GOOGLE_MAPS,
    GOOGLE_SEARCH
}

data class GeminiChatRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: String? = null,
    val useGoogleSearch: Boolean = false,
    val useGoogleMaps: Boolean = false,
    val temperature: Float = 0.7f
)

data class GeminiContent(
    val role: String,
    val parts: List<GeminiPart>
)

data class GeminiPart(
    val text: String? = null,
    val inlineData: GeminiInlineData? = null
)

data class GeminiInlineData(
    val mimeType: String,
    val base64Data: String
)

data class GeminiChatResponse(
    val text: String,
    val groundingCitations: List<GroundingCitation> = emptyList(),
    val finishReason: String? = null
)
