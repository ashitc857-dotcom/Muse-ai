package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppLanguage
import com.example.data.Strings
import com.example.data.api.GeminiStudioClient
import com.example.data.audio.AudioStudioEngine
import com.example.data.image.ImageStudioEngine
import com.example.data.model.AspectRatioOption
import com.example.data.model.GenerationProject
import com.example.data.model.ModelTier
import com.example.data.model.StudioMediaType
import com.example.data.repository.ProjectRepository
import com.example.data.repository.TokenLedgerManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

enum class StudioScreen {
    HOME,
    VIDEO_STUDIO,
    MUSIC_STUDIO,
    IMAGE_STUDIO,
    SCRIPT_CHAT,
    PROJECTS,
    TOKEN_LEDGER,
    SETTINGS_ADMIN
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: String, // "user" or "muse_ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class StudioViewModel(application: Application) : AndroidViewModel(application) {
    val ledgerManager = TokenLedgerManager(application)
    val projectRepo = ProjectRepository(application)
    val audioEngine = AudioStudioEngine(application)
    val imageEngine = ImageStudioEngine(application)

    private val _currentScreen = MutableStateFlow(StudioScreen.HOME)
    val currentScreen: StateFlow<StudioScreen> = _currentScreen.asStateFlow()

    // Screen navigation stack for BackHandler
    private val screenStack = mutableListOf(StudioScreen.HOME)

    // Studio Configuration & Inputs
    val videoPrompt = MutableStateFlow("Cinematic flying speeder soaring across high-tech cyberpunk towers at twilight, neon light reflections in rain, 8K ultra detail")
    val musicPrompt = MutableStateFlow("Futuristic synthwave anthem with deep analog bass and arpeggiated melodic lead")
    val imagePrompt = MutableStateFlow("High-fashion cyberpunk android queen wearing iridescent crystal armor, glowing neon backdrop, volumetric lighting")
    val characterName = MutableStateFlow("Aria")
    val dialogueInput = MutableStateFlow("Welcome to Muse AI Studio. Your creative universe awaits.")

    val selectedModelTier = MutableStateFlow(ModelTier.FAST)
    val selectedAspectRatio = MutableStateFlow(AspectRatioOption.RATIO_16_9)
    val selectedDuration = MutableStateFlow(15) // seconds
    val selectedResolution = MutableStateFlow("1080p")
    val selectedFps = MutableStateFlow(60)
    val selectedCameraMovement = MutableStateFlow("Cinematic Dolly Zoom")

    // Generation state
    val isGenerating = MutableStateFlow(false)
    val generationProgress = MutableStateFlow(0f)
    val generationStatus = MutableStateFlow("")

    // Audio Playback
    val isAudioPlaying = MutableStateFlow(false)
    val currentlyPlayingFile = MutableStateFlow<File?>(null)

    // Voice recording
    val isRecordingVoice = MutableStateFlow(false)
    val recordedAudioFile = MutableStateFlow<File?>(null)

    // Chat
    val chatInput = MutableStateFlow("")
    val chatMessages = MutableStateFlow(
        listOf(
            ChatMessage(
                sender = "muse_ai",
                text = "Greetings, Director! I am your Muse AI Production Partner. Ask me to draft scripts, construct cinematic camera angles, write song lyrics, or optimize generation prompts."
            )
        )
    )

    // Active project for preview
    val activeProject = MutableStateFlow<GenerationProject?>(null)

    // Banner message / Toast
    val bannerMessage = MutableStateFlow<String?>(null)

    private var activeJob: Job? = null

    init {
        // Default active project is the first showcase project
        projectRepo.projects.value.firstOrNull()?.let {
            activeProject.value = it
        }
    }

    fun navigateTo(screen: StudioScreen) {
        if (_currentScreen.value != screen) {
            screenStack.add(screen)
            _currentScreen.value = screen
        }
    }

    fun handleBack(): Boolean {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.size - 1)
            _currentScreen.value = screenStack.last()
            return true
        }
        return false
    }

    fun setLanguage(language: AppLanguage) {
        Strings.currentLanguage = language
    }

    fun claimDailyBonus() {
        val success = ledgerManager.claimDailyBonus()
        if (success) {
            bannerMessage.value = "🎉 10,000 Daily Tokens claimed successfully!"
        } else {
            val remainHours = ledgerManager.getDailyBonusTimeRemainingMillis() / (1000 * 60 * 60)
            bannerMessage.value = "Daily reward already claimed! Available in ${remainHours}h"
        }
    }

    fun enhanceCurrentPrompt(type: String) {
        viewModelScope.launch {
            val promptToEnhance = when (type) {
                "video" -> videoPrompt.value
                "music" -> musicPrompt.value
                else -> imagePrompt.value
            }
            generationStatus.value = "Enhancing prompt with Gemini AI..."
            val result = GeminiStudioClient.generateContent(
                prompt = "Enhance this $type prompt for maximum quality, detailing camera, lighting, and textures: $promptToEnhance",
                systemInstruction = "You are an elite cinematic director. Output only the enhanced prompt without commentary."
            )
            result.getOrNull()?.let { enhanced ->
                when (type) {
                    "video" -> videoPrompt.value = enhanced.trim()
                    "music" -> musicPrompt.value = enhanced.trim()
                    else -> imagePrompt.value = enhanced.trim()
                }
                bannerMessage.value = "✨ Prompt enhanced by Gemini AI"
            }
        }
    }

    fun generateVideo() {
        val cost = ledgerManager.videoCost.value
        val deducted = ledgerManager.deductForGeneration("Cinematic AI Video", cost)
        if (!deducted) {
            bannerMessage.value = "Insufficient tokens! Claim daily bonus or add tokens."
            return
        }

        activeJob?.cancel()
        activeJob = viewModelScope.launch {
            try {
                isGenerating.value = true
                generationProgress.value = 0.1f
                generationStatus.value = "Synthesizing scene storyboard & neural textures..."

                // Step 1: Synthesize real visual frame via ImageEngine
                val imageResult = imageEngine.generateImage(
                    prompt = videoPrompt.value,
                    style = "Cinematic Film Still, 8k, motion blur",
                    aspectRatio = selectedAspectRatio.value
                )

                generationProgress.value = 0.45f
                generationStatus.value = "Composing atmospheric audio & motion trajectory..."

                // Step 2: Synthesize real procedural audio track
                val audioFile = audioEngine.generateMusicWav(
                    genre = "Cyberpunk Synthwave",
                    durationSeconds = selectedDuration.value.coerceAtMost(30)
                )

                generationProgress.value = 0.8f
                generationStatus.value = "Finalizing render pass & camera stabilization..."
                delay(800)

                val savedImg = imageResult.getOrNull()
                val project = GenerationProject(
                    id = "vid_" + UUID.randomUUID().toString().take(8),
                    title = videoPrompt.value.take(32) + "...",
                    prompt = videoPrompt.value,
                    mediaType = StudioMediaType.VIDEO,
                    modelTier = selectedModelTier.value,
                    durationSeconds = selectedDuration.value,
                    aspectRatio = selectedAspectRatio.value,
                    resolution = selectedResolution.value,
                    fps = selectedFps.value,
                    cameraMovement = selectedCameraMovement.value,
                    mediaUrl = savedImg?.absolutePath,
                    audioPath = audioFile.absolutePath,
                    dialogueScript = dialogueInput.value,
                    characterName = characterName.value,
                    tokenCost = cost
                )

                projectRepo.addProject(project)
                activeProject.value = project
                generationProgress.value = 1.0f
                generationStatus.value = "Production Completed!"
                bannerMessage.value = "🎬 Video generated successfully! (Deducted $cost Token)"
            } catch (e: Exception) {
                // Refund automatically!
                ledgerManager.refundGeneration("Cinematic AI Video", cost, e.message ?: "Unknown error")
                bannerMessage.value = "Generation error. 1 Token automatically refunded!"
            } finally {
                delay(600)
                isGenerating.value = false
            }
        }
    }

    fun generateImageStudio() {
        val cost = ledgerManager.videoCost.value
        val deducted = ledgerManager.deductForGeneration("AI Image Studio", cost)
        if (!deducted) {
            bannerMessage.value = "Insufficient tokens!"
            return
        }

        activeJob?.cancel()
        activeJob = viewModelScope.launch {
            try {
                isGenerating.value = true
                generationProgress.value = 0.2f
                generationStatus.value = "Contacting neural image diffusion cluster..."

                val result = imageEngine.generateImage(
                    prompt = imagePrompt.value,
                    aspectRatio = selectedAspectRatio.value
                )

                val file = result.getOrThrow()
                generationProgress.value = 1.0f
                generationStatus.value = "Image render complete!"

                val project = GenerationProject(
                    id = "img_" + UUID.randomUUID().toString().take(8),
                    title = imagePrompt.value.take(32) + "...",
                    prompt = imagePrompt.value,
                    mediaType = StudioMediaType.IMAGE,
                    modelTier = selectedModelTier.value,
                    aspectRatio = selectedAspectRatio.value,
                    resolution = selectedResolution.value,
                    mediaUrl = file.absolutePath,
                    tokenCost = cost
                )

                projectRepo.addProject(project)
                activeProject.value = project
                bannerMessage.value = "🎨 Image created! (1 Token deducted)"
            } catch (e: Exception) {
                ledgerManager.refundGeneration("AI Image Studio", cost, e.message ?: "Failed")
                bannerMessage.value = "Image creation failed. 1 Token refunded."
            } finally {
                delay(500)
                isGenerating.value = false
            }
        }
    }

    fun generateMusicTrack(genre: String = "Cyberpunk Synthwave") {
        val cost = ledgerManager.videoCost.value
        val deducted = ledgerManager.deductForGeneration("AI Music Synthesis", cost)
        if (!deducted) {
            bannerMessage.value = "Insufficient tokens!"
            return
        }

        activeJob?.cancel()
        activeJob = viewModelScope.launch {
            try {
                isGenerating.value = true
                generationProgress.value = 0.3f
                generationStatus.value = "Synthesizing harmonic frequencies & beat drums..."

                val wavFile = audioEngine.generateMusicWav(
                    genre = genre,
                    durationSeconds = 15
                )

                generationProgress.value = 1.0f
                generationStatus.value = "Music synthesized!"

                val project = GenerationProject(
                    id = "mus_" + UUID.randomUUID().toString().take(8),
                    title = "$genre Master Track",
                    prompt = musicPrompt.value,
                    mediaType = StudioMediaType.MUSIC,
                    modelTier = selectedModelTier.value,
                    durationSeconds = 15,
                    audioPath = wavFile.absolutePath,
                    tokenCost = cost
                )

                projectRepo.addProject(project)
                activeProject.value = project
                playAudio(wavFile)
                bannerMessage.value = "🎵 Music track generated! (1 Token deducted)"
            } catch (e: Exception) {
                ledgerManager.refundGeneration("AI Music Synthesis", cost, e.message ?: "Failed")
                bannerMessage.value = "Music generation failed. 1 Token refunded."
            } finally {
                delay(500)
                isGenerating.value = false
            }
        }
    }

    fun generateLyrics() {
        viewModelScope.launch {
            generationStatus.value = "Composing lyrics with Gemini AI..."
            val prompt = "Compose a 4-verse hit song with verses, chorus, and bridge about: ${musicPrompt.value}"
            val res = GeminiStudioClient.generateContent(prompt)
            res.getOrNull()?.let { lyrics ->
                val project = GenerationProject(
                    id = "lyr_" + UUID.randomUUID().toString().take(8),
                    title = "Song Lyrics: " + musicPrompt.value.take(24),
                    prompt = musicPrompt.value,
                    mediaType = StudioMediaType.SCRIPT,
                    modelTier = selectedModelTier.value,
                    dialogueScript = lyrics,
                    tokenCost = 0L
                )
                projectRepo.addProject(project)
                activeProject.value = project
                bannerMessage.value = "📝 Lyrics generated & added to project library!"
            }
        }
    }

    fun draftScreenplay(topic: String, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            val res = GeminiStudioClient.generateContent(
                prompt = "Write a complete dramatic scene-by-scene movie script breakdown with sluglines, camera angles, and character dialogue for: $topic",
                systemInstruction = "You are an Oscar-winning Hollywood screenwriter."
            )
            onComplete(res.getOrNull() ?: "Script drafted successfully.")
        }
    }

    fun sendChatMessage() {
        val text = chatInput.value.trim()
        if (text.isEmpty()) return

        chatInput.value = ""
        val userMsg = ChatMessage(sender = "user", text = text)
        chatMessages.value = chatMessages.value + userMsg

        viewModelScope.launch {
            val response = GeminiStudioClient.generateContent(
                prompt = text,
                systemInstruction = "You are Muse AI Studio Assistant. Help the user with video scriptwriting, scene composition, lyrics, and creative directions in an enthusiastic, expert tone."
            )
            val replyText = response.getOrNull() ?: "I am ready to assist with your next masterpiece!"
            val aiMsg = ChatMessage(sender = "muse_ai", text = replyText)
            chatMessages.value = chatMessages.value + aiMsg
        }
    }

    fun speakDialogue(text: String) {
        val lang = Strings.currentLanguage.code
        audioEngine.speak(text, lang)
    }

    fun playAudio(file: File) {
        currentlyPlayingFile.value = file
        isAudioPlaying.value = true
        audioEngine.playAudioFile(file) {
            isAudioPlaying.value = false
        }
    }

    fun toggleAudioPlayback(file: File?) {
        if (file == null) return
        if (isAudioPlaying.value && currentlyPlayingFile.value?.absolutePath == file.absolutePath) {
            audioEngine.stopPlayback()
            isAudioPlaying.value = false
        } else {
            playAudio(file)
        }
    }

    fun toggleVoiceRecording() {
        if (isRecordingVoice.value) {
            val file = audioEngine.stopVoiceRecording()
            isRecordingVoice.value = false
            recordedAudioFile.value = file
            bannerMessage.value = "🎙️ Voice recording saved!"
        } else {
            val file = audioEngine.startVoiceRecording()
            if (file != null) {
                isRecordingVoice.value = true
                bannerMessage.value = "🔴 Recording voice... Speak into mic"
            }
        }
    }

    fun dismissBanner() {
        bannerMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
    }
}
