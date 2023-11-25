package de.lemke.pianoview.listener

import de.lemke.pianoview.entity.Piano.PianoKeyType

interface OnPianoListener {
    fun onPianoInitFinish()
    fun onPianoClick(type: PianoKeyType, group: Int, index: Int)
}