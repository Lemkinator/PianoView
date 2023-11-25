package com.chengtao.pianoview.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.SparseIntArray
import com.chengtao.pianoview.entity.Piano
import com.chengtao.pianoview.entity.Piano.PianoKeyType
import com.chengtao.pianoview.entity.PianoKey
import com.chengtao.pianoview.listener.LoadAudioMessage
import com.chengtao.pianoview.listener.OnLoadAudioListener
import java.util.concurrent.Executors

class AudioUtils constructor(context: Context, loadAudioListener: OnLoadAudioListener?, maxStream: Int = 11) : LoadAudioMessage {
    private val service = Executors.newCachedThreadPool()
    private var pool: SoundPool

    private var context: Context?
    private val loadAudioListener: OnLoadAudioListener?
    private var whiteKeyMusics: SparseIntArray = SparseIntArray()
    private var blackKeyMusics: SparseIntArray = SparseIntArray()
    private var isLoadFinish = false
    private var isLoading = false
    private val handler: Handler
    private val audioManager: AudioManager
    private var currentTime: Long = 0
    private var loadNum = 0
    private var minSoundId = -1
    private var maxSoundId = -1

    init {
        this.context = context
        this.loadAudioListener = loadAudioListener
        handler = AudioStatusHandler(context.mainLooper)
        pool = SoundPool.Builder().setMaxStreams(maxStream)
            .setAudioAttributes(
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            .build()
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    fun loadMusic(piano: Piano?) {
        if (piano != null) {
            if (!isLoading && !isLoadFinish) {
                isLoading = true
                pool.setOnLoadCompleteListener { _: SoundPool?, _: Int, _: Int ->
                    loadNum++
                    if (loadNum == 88) {
                        isLoadFinish = true
                        sendProgressMessage(100)
                        sendFinishMessage()
                        pool.play(whiteKeyMusics[0], 0f, 0f, 1, -1, 2f)
                    } else {
                        if (System.currentTimeMillis() - currentTime >= SEND_PROGRESS_MESSAGE_BREAK_TIME) {
                            sendProgressMessage((loadNum.toFloat() / 88 * 100f).toInt())
                            currentTime = System.currentTimeMillis()
                        }
                    }
                }
                service.execute {
                    sendStartMessage()
                    val whiteKeys = piano.whitePianoKeys
                    var whiteKeyPos = 0
                    for (i in whiteKeys.indices) {
                        for (key in whiteKeys[i]) {
                            try {
                                val soundID = pool.load(context, key.voiceId, 1)
                                whiteKeyMusics.put(whiteKeyPos, soundID)
                                if (minSoundId == -1) {
                                    minSoundId = soundID
                                }
                                whiteKeyPos++
                            } catch (e: Exception) {
                                isLoading = false
                                sendErrorMessage(e)
                                return@execute
                            }
                        }
                    }
                    val blackKeys = piano.blackPianoKeys
                    var blackKeyPos = 0
                    for (i in blackKeys.indices) {
                        for (key in blackKeys[i]) {
                            try {
                                val soundID = pool.load(context, key.voiceId, 1)
                                blackKeyMusics.put(blackKeyPos, soundID)
                                blackKeyPos++
                                if (soundID > maxSoundId) {
                                    maxSoundId = soundID
                                }
                            } catch (e: Exception) {
                                isLoading = false
                                sendErrorMessage(e)
                                return@execute
                            }
                        }
                    }
                }
            }
        }
    }

    fun playMusic(key: PianoKey) {
        if (isLoadFinish) {
            service.execute {
                when (key.type) {
                    PianoKeyType.BLACK -> playBlackKeyMusic(key.group, key.index)
                    PianoKeyType.WHITE -> playWhiteKeyMusic(key.group, key.index)
                }
            }
        }
    }

    private fun playWhiteKeyMusic(group: Int, index: Int) {
        play(whiteKeyMusics[if (group == 0) index else (group - 1) * 7 + 2 + index])
    }

    private fun playBlackKeyMusic(group: Int, index: Int) {
        play(blackKeyMusics[if (group == 0) index else (group - 1) * 5 + 1 + index])
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

    fun stop() {
        context = null
        pool.release()
        whiteKeyMusics.clear()
        blackKeyMusics.clear()
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
        if (loadAudioListener != null) {
            when (msg.what) {
                LOAD_START -> loadAudioListener.loadPianoAudioStart()
                LOAD_FINISH -> loadAudioListener.loadPianoAudioFinish()
                LOAD_ERROR -> loadAudioListener.loadPianoAudioError(msg.obj as Exception)
                LOAD_PROGRESS -> loadAudioListener.loadPianoAudioProgress(msg.obj as Int)
                else -> {}
            }
        }
    }

    companion object {
        private const val LOAD_START = 1
        private const val LOAD_FINISH = 2
        private const val LOAD_ERROR = 3
        private const val LOAD_PROGRESS = 4
        private const val SEND_PROGRESS_MESSAGE_BREAK_TIME = 500
    }
}