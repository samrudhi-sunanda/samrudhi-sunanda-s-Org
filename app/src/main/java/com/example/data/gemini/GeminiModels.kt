package com.example.data.gemini

object GeminiModels {
    // User requested models
    const val GEMINI_3_5_FLASH = "gemini-3.5-flash"
    const val GEMINI_3_1_PRO = "gemini-3.1-pro-preview"
    const val GEMINI_3_1_FLASH_LITE = "gemini-3.1-flash-lite-preview"
    const val GEMINI_3_5_TRANSCRIBE = "gemini-3.5-transcribe"
    const val GEMINI_3_8_LIVE = "gemini-3.8-live"

    // Model options for UI selection
    val CHAT_MODELS = listOf(
        ModelOption(
            id = GEMINI_3_5_FLASH,
            name = "Gemini 3.5 Flash",
            badge = "GENERAL",
            description = "General tasks, Maps & Search grounding"
        ),
        ModelOption(
            id = GEMINI_3_1_PRO,
            name = "Gemini 3.1 Pro",
            badge = "COMPLEX",
            description = "Complex micro-climate & route optimization"
        ),
        ModelOption(
            id = GEMINI_3_1_FLASH_LITE,
            name = "Gemini 3.1 Flash Lite",
            badge = "FAST",
            description = "High-speed live hazard triage & quick Q&A"
        )
    )
}

data class ModelOption(
    val id: String,
    val name: String,
    val badge: String,
    val description: String
)
