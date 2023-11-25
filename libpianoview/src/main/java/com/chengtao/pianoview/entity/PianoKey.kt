package com.chengtao.pianoview.entity

import android.graphics.Rect
import android.graphics.drawable.Drawable
import com.chengtao.pianoview.entity.Piano.PianoKeyType

data class PianoKey (
    val type: PianoKeyType,
    val group: Int,
    val index: Int,
    val keyDrawable: Drawable,
    val voiceId: Int,
    val areaOfKey: List<Rect>,
) {
    var isPressed: Boolean = false
    val letterName: String
        get() = if (type == PianoKeyType.BLACK) ""
        else {
            when (index) {
                0 -> "C"
                1 -> "D"
                2 -> "E"
                3 -> "F"
                4 -> "G"
                5 -> "A"
                6 -> "B"
                else -> ""
            } + group
        }

    var fingerID = -1
    fun resetFingerID() {
        fingerID = -1
    }

    fun contains(x: Int, y: Int): Boolean  = areaOfKey.any { it.contains(x, y) }
}