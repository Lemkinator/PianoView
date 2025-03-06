@file:Suppress("unused")

package de.lemke.pianoview.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import de.lemke.pianoview.entity.PianoKey
import de.lemke.pianoview.listener.LoadAudioMessage
import de.lemke.pianoview.listener.OnLoadAudioListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class AudioUtils(
    private val context: Context,
    private val pianoKeys: List<PianoKey>,
    var loadAudioListener: OnLoadAudioListener? = null,
    var maxStreams: Int? = null
) : LoadAudioMessage {
    private val service = Executors.newCachedThreadPool()
    private val handler: Handler = AudioStatusHandler(context.mainLooper)
    private var pool: SoundPool = SoundPool.Builder().setMaxStreams(maxStreams ?: 10).setAudioAttributes(
        AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()
    ).build()
    private var volume: Float = 1f
    private var startingPitchesJob: Job? = null

    @Suppress("MemberVisibilityCanBePrivate")
    val initialized
        get() = loadedKeys == pianoKeys.size
    private var loadedKeys = 0

    init {
        pool.setOnLoadCompleteListener { _: SoundPool?, _: Int, _: Int ->
            loadedKeys++
            sendProgressMessage(loadedKeys * 100 / pianoKeys.size)
            if (initialized) sendFinishMessage()
        }
        service.execute {
            sendStartMessage()
            pianoKeys.forEach {
                it.soundPoolId = pool.load(context, it.soundResId, 1)
            }
        }
    }

    fun playAllAsStartingPitch(coroutineScope: CoroutineScope, volume: Float? = null): Job {
        startingPitchesJob?.cancel()
        return coroutineScope.launch(Dispatchers.Default) {
            while (!initialized) {
                delay(500)
            }
            volume?.let { setVolume(it) }
            pianoKeys.forEach {
                playKey(it)
                delay(700)
            }
            if (pianoKeys.size > 1) {
                delay(500)
                pianoKeys.forEach { playKey(it) }
            }
        }.also { startingPitchesJob = it }
    }

    fun playKey(index: Int) = playKey(pianoKeys[index])

    private fun playKey(key: PianoKey) = service.execute {
        if (initialized) {
            if (key.soundPoolId == null) Log.e("AudioUtils", "playKey: soundPoolId is null for key $key")
            else key.soundPoolId?.let { play(it) }
        }
    }

    private fun play(soundId: Int) {
        pool.play(soundId, volume, volume, 1, 0, 1f)
    }

    fun stop() {
        startingPitchesJob?.cancel()
        if (initialized) {
            pianoKeys.forEach { it.soundPoolId?.let { id -> pool.stop(id) } }
        }
    }

    fun setVolume(volume: Float) {
        this.volume = volume.coerceIn(0f, 1f) * volume.coerceIn(0f, 1f)
        pianoKeys.forEach { it.soundPoolId?.let { id -> pool.setVolume(id, this.volume, this.volume) } }
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