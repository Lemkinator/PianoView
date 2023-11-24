package com.chengtao.pianoview.listener

interface LoadAudioMessage {
    fun sendStartMessage()
    fun sendFinishMessage()
    fun sendErrorMessage(e: Exception)
    fun sendProgressMessage(progress: Int)
}