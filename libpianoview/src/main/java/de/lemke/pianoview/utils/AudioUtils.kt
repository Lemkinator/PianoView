package de.lemke.pianoview.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import de.lemke.pianoview.entity.PianoKey
import de.lemke.pianoview.listener.LoadAudioMessage
import de.lemke.pianoview.listener.OnLoadAudioListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class AudioUtils(
    private val context: Context,
    private val pianoKeys: MutableList<PianoKey>,
    var loadAudioListener: OnLoadAudioListener? = null,
    var maxStreams: Int? = null
) : LoadAudioMessage {
    private val service = Executors.newCachedThreadPool()
    private val audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val handler: Handler = AudioStatusHandler(context.mainLooper)
    private var pool: SoundPool = SoundPool.Builder().setMaxStreams(maxStreams ?: 10)
        .setAudioAttributes(
            AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        ).build()

    @Suppress("MemberVisibilityCanBePrivate")
    val initialized
        get() = loadedKeys == pianoKeys.size
    private var loadedKeys = 0

    init {
        pool.setOnLoadCompleteListener { _: SoundPool?, _: Int, _: Int ->
            loadedKeys++
            sendProgressMessage(loadedKeys * 100 / pianoKeys.size)
            if (initialized) {
                sendFinishMessage()
                pool.play(pianoKeys.first().soundPoolId!!, 0f, 0f, 1, -1, 2f)
            }
        }
        service.execute {
            sendStartMessage()
            pianoKeys.forEach {
                it.soundPoolId = pool.load(context, it.soundResId, 1)
            }
        }
    }

    @Suppress("unused")
    suspend fun playAllAsStartingPitch() {
        withContext(Dispatchers.Default) {
            while (!initialized) {
                delay(500)
            }
            pianoKeys.forEach {
                playKey(it)
                delay(700)
            }
            delay(500)
            pianoKeys.forEach { playKey(it) }
        }
    }

    fun playKey(index: Int) = playKey(pianoKeys[index])

    private fun playKey(key: PianoKey) = service.execute {
        if (initialized) {
            if (key.soundPoolId == null) Log.e("AudioUtils", "playKey: soundPoolId is null for key $key")
            else key.soundPoolId?.let { play(it) }
        }
    }

    private fun play(soundId: Int) {
        val actualVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
        var volume = actualVolume / maxVolume
        if (volume <= 0) {
            volume = 1f
        }
        pool.play(soundId, volume, volume, 1, 0, 1f)
    }

    fun destroy() {
        pool.release()
    }

    override fun sendStartMessage() {
        handler.sendEmptyMessage(LOAD_START)
    }

    override fun sendFinishMessage() {
        handler.sendEmptyMessage(LOAD_FINISH)
    }

    override fun sendErrorMessage(e: Exception) {
        handler.sendMessage(Message.obtain(handler, LOAD_ERROR, e))
    }

    override fun sendProgressMessage(progress: Int) {
        handler.sendMessage(Message.obtain(handler, LOAD_PROGRESS, progress))
    }

    private inner class AudioStatusHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            handleAudioStatusMessage(msg)
        }
    }

    private fun handleAudioStatusMessage(msg: Message) {
        when (msg.what) {
            LOAD_START -> loadAudioListener?.loadPianoAudioStart()
            LOAD_FINISH -> loadAudioListener?.loadPianoAudioFinish()
            LOAD_ERROR -> loadAudioListener?.loadPianoAudioError(msg.obj as Exception)
            LOAD_PROGRESS -> loadAudioListener?.loadPianoAudioProgress(msg.obj as Int)
            else -> {}
        }
    }

    companion object {
        private const val LOAD_START = 1
        private const val LOAD_FINISH = 2
        private const val LOAD_ERROR = 3
        private const val LOAD_PROGRESS = 4
    }
}