package com.chengtao.pianoview.listener

interface OnLoadAudioListener {
    fun loadPianoAudioStart()
    fun loadPianoAudioFinish()
    fun loadPianoAudioError(e: Exception)
    fun loadPianoAudioProgress(progress: Int)
}