package com.chengtao.pianoview.listener

import com.chengtao.pianoview.entity.Piano.PianoKeyType

interface OnPianoListener {
    fun onPianoInitFinish()
    fun onPianoClick(type: PianoKeyType, group: Int, index: Int)
}