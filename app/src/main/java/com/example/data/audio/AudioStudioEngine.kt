package com.example.data.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.util.Locale
import kotlin.math.PI
import kotlin.math.sin

class AudioStudioEngine(private val context: Context) : TextToSpeech.OnInitListener {
    private val TAG = "AudioStudioEngine"

    private var tts: TextToSpeech? = TextToSpeech(context, this)
    private var isTtsReady = false

    private var mediaPlayer: MediaPlayer? = null
    private var mediaRecorder: MediaRecorder? = null
    private var currentRecordingFile: File? = null

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsReady = true
            tts?.language = Locale.ENGLISH
        } else {
            Log.e(TAG, "TTS Initialization failed")
        }
    }

    fun speak(text: String, languageCode: String = "en", pitch: Float = 1.0f, speed: Float = 1.0f) {
        if (!isTtsReady) return
        val locale = when (languageCode) {
            "bn" -> Locale("bn", "BD")
            "hi" -> Locale("hi", "IN")
            else -> Locale.ENGLISH
        }
        tts?.language = locale
        tts?.setPitch(pitch)
        tts?.setSpeechRate(speed)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "MUSE_TTS_ID")
    }

    fun stopSpeaking() {
        tts?.stop()
    }

    suspend fun generateMusicWav(
        genre: String = "Cyberpunk Synthwave",
        durationSeconds: Int = 10,
        bpm: Int = 120
    ): File = withContext(Dispatchers.IO) {
        val sampleRate = 44100
        val totalSamples = sampleRate * durationSeconds
        val audioDir = File(context.filesDir, "muse_audio").apply { mkdirs() }
        val outputFile = File(audioDir, "track_${System.currentTimeMillis()}.wav")

        val pcmData = ShortArray(totalSamples)

        // Musical frequency notes (Hz)
        val chordNotes = when (genre) {
            "Cyberpunk Synthwave" -> doubleArrayOf(130.81, 155.56, 174.61, 196.00) // C3, Eb3, F3, G3
            "Cinematic Ambient" -> doubleArrayOf(110.00, 164.81, 220.00, 293.66) // A2, E3, A3, D4
            "Lofi Chill Beats" -> doubleArrayOf(146.83, 174.61, 220.00, 261.63) // D3, F3, A3, C4
            else -> doubleArrayOf(130.81, 164.81, 196.00, 246.94) // C Major 7
        }

        val samplesPerBeat = (sampleRate * 60) / bpm

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val beatIndex = (i / samplesPerBeat) % chordNotes.size
            val baseFreq = chordNotes[beatIndex]

            // Bassline (Sine + Sub)
            var sample = 0.35 * sin(2.0 * PI * baseFreq * t)

            // Melody overtone (Sawtooth-like)
            val leadFreq = baseFreq * 2.0
            sample += 0.20 * sin(2.0 * PI * leadFreq * t) + 0.10 * sin(2.0 * PI * (leadFreq * 1.5) * t)

            // Beat kick / pulse envelope at the start of each beat
            val beatSamplePos = i % samplesPerBeat
            val beatFrac = beatSamplePos.toDouble() / samplesPerBeat
            val kickDecay = Math.exp(-beatFrac * 10.0)
            val kickFreq = 120.0 * Math.exp(-beatFrac * 15.0) + 45.0
            sample += 0.35 * kickDecay * sin(2.0 * PI * kickFreq * t)

            // Soft clamp to prevent clipping
            val clamped = (sample.coerceIn(-1.0, 1.0) * 32767.0).toInt().toShort()
            pcmData[i] = clamped
        }

        writeWavFile(outputFile, pcmData, sampleRate, 1)
        outputFile
    }

    private fun writeWavFile(file: File, pcmData: ShortArray, sampleRate: Int, channels: Int) {
        val totalAudioLen = pcmData.size * 2L
        val totalDataLen = totalAudioLen + 36L
        val byteRate = sampleRate * channels * 2L

        FileOutputStream(file).use { out ->
            val header = ByteArray(44)
            header[0] = 'R'.code.toByte()
            header[1] = 'I'.code.toByte()
            header[2] = 'F'.code.toByte()
            header[3] = 'F'.code.toByte()
            header[4] = (totalDataLen and 0xffL).toByte()
            header[5] = ((totalDataLen shr 8) and 0xffL).toByte()
            header[6] = ((totalDataLen shr 16) and 0xffL).toByte()
            header[7] = ((totalDataLen shr 24) and 0xffL).toByte()
            header[8] = 'W'.code.toByte()
            header[9] = 'A'.code.toByte()
            header[10] = 'V'.code.toByte()
            header[11] = 'E'.code.toByte()
            header[12] = 'f'.code.toByte()
            header[13] = 'm'.code.toByte()
            header[14] = 't'.code.toByte()
            header[15] = ' '.code.toByte()
            header[16] = 16 // 16 for PCM
            header[17] = 0
            header[18] = 0
            header[19] = 0
            header[20] = 1 // PCM = 1
            header[21] = 0
            header[22] = channels.toByte()
            header[23] = 0
            header[24] = (sampleRate and 0xff).toByte()
            header[25] = ((sampleRate shr 8) and 0xff).toByte()
            header[26] = ((sampleRate shr 16) and 0xff).toByte()
            header[27] = ((sampleRate shr 24) and 0xff).toByte()
            header[28] = (byteRate and 0xffL).toByte()
            header[29] = ((byteRate shr 8) and 0xffL).toByte()
            header[30] = ((byteRate shr 16) and 0xffL).toByte()
            header[31] = ((byteRate shr 24) and 0xffL).toByte()
            header[32] = (channels * 2).toByte() // block align
            header[33] = 0
            header[34] = 16 // bits per sample
            header[35] = 0
            header[36] = 'd'.code.toByte()
            header[37] = 'a'.code.toByte()
            header[38] = 't'.code.toByte()
            header[39] = 'a'.code.toByte()
            header[40] = (totalAudioLen and 0xffL).toByte()
            header[41] = ((totalAudioLen shr 8) and 0xffL).toByte()
            header[42] = ((totalAudioLen shr 16) and 0xffL).toByte()
            header[43] = ((totalAudioLen shr 24) and 0xffL).toByte()

            out.write(header)

            val byteBuf = ByteArray(pcmData.size * 2)
            for (i in pcmData.indices) {
                val s = pcmData[i]
                byteBuf[i * 2] = (s.toInt() and 0xff).toByte()
                byteBuf[i * 2 + 1] = ((s.toInt() shr 8) and 0xff).toByte()
            }
            out.write(byteBuf)
        }
    }

    fun playAudioFile(file: File, onComplete: () -> Unit = {}) {
        stopPlayback()
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                prepare()
                setOnCompletionListener { onComplete() }
                start()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error playing audio file", e)
        }
    }

    fun stopPlayback() {
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = null
    }

    fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true

    fun startVoiceRecording(): File? {
        val audioDir = File(context.filesDir, "muse_recordings").apply { mkdirs() }
        val file = File(audioDir, "rec_${System.currentTimeMillis()}.mp4")
        currentRecordingFile = file

        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)
            try {
                prepare()
                start()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start recording", e)
                return null
            }
        }
        return file
    }

    fun stopVoiceRecording(): File? {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop recording", e)
        }
        mediaRecorder = null
        return currentRecordingFile
    }

    fun release() {
        stopPlayback()
        tts?.stop()
        tts?.shutdown()
    }
}
