package com.example.data.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.data.model.AspectRatioOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

class ImageStudioEngine(private val context: Context) {
    private val TAG = "ImageStudioEngine"

    private val client = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateImage(
        prompt: String,
        style: String = "Cinematic Movie Still",
        aspectRatio: AspectRatioOption = AspectRatioOption.RATIO_16_9,
        seed: Long = System.currentTimeMillis() % 100000
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val dimensions = when (aspectRatio) {
                AspectRatioOption.RATIO_16_9 -> Pair(1024, 576)
                AspectRatioOption.RATIO_9_16 -> Pair(576, 1024)
                AspectRatioOption.RATIO_1_1 -> Pair(768, 768)
            }

            val enhancedPrompt = "$prompt, $style, high aesthetic, 8k resolution, cinematic lighting, photorealistic, intricate details"
            val encoded = URLEncoder.encode(enhancedPrompt, StandardCharsets.UTF_8.toString())
            val imageUrl = "https://image.pollinations.ai/prompt/$encoded?width=${dimensions.first}&height=${dimensions.second}&seed=$seed&nologo=true"

            val request = Request.Builder().url(imageUrl).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Image generation failed with HTTP ${response.code}"))
            }

            val imageBytes = response.body?.bytes()
                ?: return@withContext Result.failure(Exception("Empty image response from provider"))

            val imageDir = File(context.filesDir, "muse_images").apply { mkdirs() }
            val savedFile = File(imageDir, "img_${System.currentTimeMillis()}.png")

            FileOutputStream(savedFile).use { out ->
                out.write(imageBytes)
            }

            Result.success(savedFile)
        } catch (e: Exception) {
            Log.e(TAG, "Image generation error", e)
            Result.failure(e)
        }
    }
}
