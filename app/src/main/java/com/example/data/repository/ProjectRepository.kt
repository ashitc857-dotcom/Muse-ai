package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AspectRatioOption
import com.example.data.model.GenerationProject
import com.example.data.model.ModelTier
import com.example.data.model.StudioMediaType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

class ProjectRepository(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("muse_projects_repo", Context.MODE_PRIVATE)

    private val _projects = MutableStateFlow(loadProjects())
    val projects: StateFlow<List<GenerationProject>> = _projects.asStateFlow()

    init {
        if (_projects.value.isEmpty()) {
            initSampleProjects()
        }
    }

    private fun initSampleProjects() {
        val samples = listOf(
            GenerationProject(
                id = "proj_sample_01",
                title = "Cyberpunk Neo-Metropolis Flight",
                prompt = "Futuristic flying speeder soaring between towering neon skyscrapers in rain, cinematic anamorphic bokeh, reflections on wet chrome, dramatic lighting",
                mediaType = StudioMediaType.VIDEO,
                modelTier = ModelTier.FAST,
                durationSeconds = 15,
                aspectRatio = AspectRatioOption.RATIO_16_9,
                resolution = "1080p",
                fps = 60,
                cameraMovement = "Cinematic Drone Dolly Zoom",
                dialogueScript = "Aryan: 'The speeders are synced. Commencing the descent through Sector 7.'",
                characterName = "Commander Aryan",
                tokenCost = 1L
            ),
            GenerationProject(
                id = "proj_sample_02",
                title = "Bengali Heritage Sunset Horizon",
                prompt = "Serene golden hour river in rural Bengal, traditional boat with colorful sail, silhouette of coconut palms, gentle water ripples, cinematic 4k aesthetic",
                mediaType = StudioMediaType.IMAGE,
                modelTier = ModelTier.PRO,
                durationSeconds = 0,
                aspectRatio = AspectRatioOption.RATIO_16_9,
                resolution = "4K UHD",
                fps = 30,
                tokenCost = 1L
            ),
            GenerationProject(
                id = "proj_sample_03",
                title = "Echoes of Eternity Synth Track",
                prompt = "Dynamic synthwave rhythm with deep bassline, arpeggiated cosmic chords, emotional melody, cinematic build-up",
                mediaType = StudioMediaType.MUSIC,
                modelTier = ModelTier.FAST,
                durationSeconds = 30,
                tokenCost = 1L
            ),
            GenerationProject(
                id = "proj_sample_04",
                title = "Chronicles of Dhaka 2085 Screenplay",
                prompt = "Full sci-fi screenplay scene breakdown set in futurist Dhaka, high stakes dialogue, holographic AI companion interactions",
                mediaType = StudioMediaType.SCRIPT,
                modelTier = ModelTier.ADVANCED,
                dialogueScript = """
                INT. NEURAL LAB - DHAKA MEGA-TOWER - NIGHT
                Tania gazes at the glowing quantum core suspended in magnetic field.
                
                TANIA:
                "If we initiate the neural link now, the entire district's consciousness will harmonize."
                
                KABIR:
                (Checking tactical tablet)
                "Then initiate it, Tania. Let tomorrow begin."
                """.trimIndent(),
                tokenCost = 1L
            )
        )
        saveProjects(samples)
    }

    private fun loadProjects(): List<GenerationProject> {
        val str = prefs.getString("projects_json", null) ?: return emptyList()
        val list = mutableListOf<GenerationProject>()
        try {
            val arr = JSONArray(str)
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                list.add(
                    GenerationProject(
                        id = o.getString("id"),
                        title = o.getString("title"),
                        prompt = o.getString("prompt"),
                        mediaType = StudioMediaType.valueOf(o.getString("mediaType")),
                        modelTier = ModelTier.valueOf(o.getString("modelTier")),
                        durationSeconds = o.getInt("durationSeconds"),
                        aspectRatio = AspectRatioOption.valueOf(o.getString("aspectRatio")),
                        resolution = o.getString("resolution"),
                        fps = o.getInt("fps"),
                        cameraMovement = o.optString("cameraMovement", "Cinematic Pan"),
                        mediaUrl = o.optString("mediaUrl", null),
                        audioPath = o.optString("audioPath", null),
                        dialogueScript = o.optString("dialogueScript", null),
                        characterName = o.optString("characterName", "Hero"),
                        timestamp = o.getLong("timestamp"),
                        status = o.optString("status", "Completed"),
                        tokenCost = o.optLong("tokenCost", 1L)
                    )
                )
            }
        } catch (_: Exception) {
            // fallback
        }
        return list
    }

    private fun saveProjects(list: List<GenerationProject>) {
        val arr = JSONArray()
        list.forEach { p ->
            val o = JSONObject().apply {
                put("id", p.id)
                put("title", p.title)
                put("prompt", p.prompt)
                put("mediaType", p.mediaType.name)
                put("modelTier", p.modelTier.name)
                put("durationSeconds", p.durationSeconds)
                put("aspectRatio", p.aspectRatio.name)
                put("resolution", p.resolution)
                put("fps", p.fps)
                put("cameraMovement", p.cameraMovement)
                put("mediaUrl", p.mediaUrl)
                put("audioPath", p.audioPath)
                put("dialogueScript", p.dialogueScript)
                put("characterName", p.characterName)
                put("timestamp", p.timestamp)
                put("status", p.status)
                put("tokenCost", p.tokenCost)
            }
            arr.put(o)
        }
        prefs.edit().putString("projects_json", arr.toString()).apply()
        _projects.value = list
    }

    fun addProject(project: GenerationProject) {
        val updated = listOf(project) + _projects.value
        saveProjects(updated)
    }

    fun deleteProject(id: String) {
        val updated = _projects.value.filter { it.id != id }
        saveProjects(updated)
    }
}
