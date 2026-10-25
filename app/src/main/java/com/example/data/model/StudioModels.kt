package com.example.data.model

enum class ModelTier(val displayName: String, val speed: String, val backendEngine: String) {
    FAST("Fast (Turbo)", "Ultra-Fast (3-5s)", "Pollinations & Local Neural Core"),
    PRO("Pro (HD Creative)", "High Quality (10-15s)", "Gemini 3.5 Flash & High-Res VAE"),
    ADVANCED("Advanced (Studio Cinema)", "Deep Cinematic (20s)", "Gemini 3.1 Pro + Neural Director"),
    EXPERT("Expert (Ultra 4K)", "Extreme Detail (30s)", "Gemini Nano Banana + Multi-Modal VAE"),
    ENTERPRISE("Enterprise (Cloud Cluster)", "Full Production", "Dedicated Cloud GPU Pipeline")
}

enum class StudioMediaType(val label: String) {
    VIDEO("Video"),
    IMAGE("Image"),
    MUSIC("Music & Audio"),
    SCRIPT("Screenplay & Script"),
    THUMBNAIL("Thumbnail")
}

enum class AspectRatioOption(val label: String, val widthRatio: Float, val heightRatio: Float) {
    RATIO_16_9("16:9 Landscape", 16f, 9f),
    RATIO_9_16("9:16 Portrait / Reel", 9f, 16f),
    RATIO_1_1("1:1 Square", 1f, 1f)
}

data class GenerationProject(
    val id: String,
    val title: String,
    val prompt: String,
    val mediaType: StudioMediaType,
    val modelTier: ModelTier,
    val durationSeconds: Int = 10,
    val aspectRatio: AspectRatioOption = AspectRatioOption.RATIO_16_9,
    val resolution: String = "1080p",
    val fps: Int = 30,
    val cameraMovement: String = "Cinematic Dolly Zoom",
    val mediaUrl: String? = null,
    val audioPath: String? = null,
    val dialogueScript: String? = null,
    val characterName: String = "Hero",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Completed", // Queued, Processing, Completed, Failed
    val tokenCost: Long = 1L
)

enum class TransactionType {
    INITIAL_CREDIT,
    DAILY_BONUS,
    GENERATION_DEBIT,
    REFUND,
    ADMIN_ADJUSTMENT
}

data class TokenTransaction(
    val id: String,
    val timestamp: Long,
    val type: TransactionType,
    val amount: Long,
    val balanceAfter: Long,
    val description: String
)

data class UserAccount(
    val id: String = "usr_muse_001",
    val username: String = "Creative Director",
    val email: String = "director@muse.studio",
    val tokenBalance: Long = 1_000_000_000L,
    val totalGenerations: Int = 0,
    val lastDailyBonusTimestamp: Long = 0L,
    val isGuest: Boolean = false,
    val activeLanguage: String = "en"
)
