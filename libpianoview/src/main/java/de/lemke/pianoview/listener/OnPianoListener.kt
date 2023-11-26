package de.lemke.pianoview.listener

import de.lemke.pianoview.entity.PianoKeyType

interface OnPianoListener {
    fun onPianoClick(type: PianoKeyType, group: Int, indexInGroup: Int)
}