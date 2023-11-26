package de.lemke.pianoview.listener

import de.lemke.pianoview.entity.PianoKeyType

interface OnPianoListener {
    fun onPianoInitFinish()
    fun onPianoClick(type: PianoKeyType, group: Int, indexInGroup: Int)
}