package de.lemke.pianoview.entity

import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.Log
import de.lemke.pianoview.utils.getSoundResId

class PianoKey private constructor(
    val type: PianoKeyType,
    val group: Int,
    val indexInGroup: Int,
    private val givenNoteName: NoteName? = null,
) {
    val noteName: NoteName
        get() = givenNoteName ?: NoteName.firstWith(type, group, indexInGroup)

    @Suppress("unused")
    val germanNoteName: String get() = noteName.germanNoteName

    val soundResId
        get() = getSoundResId()

    var soundPoolId: Int? = null

    var keyDrawable: Drawable? = null

    var areaOfKey: List<Rect>? = null

    var isPressed: Boolean = false

    var fingerID = -1

    fun resetFingerID() {
        fingerID = -1
    }

    fun contains(x: Int, y: Int): Boolean = areaOfKey?.any { it.contains(x, y) } == true

    companion object {
        private fun keyExists(type: PianoKeyType, group: Int, indexInGroup: Int): Boolean {
            return when (type) {
                PianoKeyType.BLACK -> {
                    group in 1..7 && indexInGroup in 0..4 || group == 0 && indexInGroup == 0
                }

                PianoKeyType.WHITE -> {
                    group == 0 && indexInGroup in 0..1 || group == 8 && indexInGroup == 0 || group in 1..7 && indexInGroup in 0..6
                }
            }
        }

        @Suppress("unused")
        fun createList(noteNames: String): List<PianoKey> {
            val list = mutableListOf<PianoKey>()
            noteNames.split(" ").forEach {
                val key = create(it)
                if (key != null) list.add(key)
            }
            return list
        }

        fun create(noteName: String?): PianoKey? {
            if (noteName == null) return null
            return create(NoteName.create(noteName))

        }

        fun create(noteName: NoteName?): PianoKey? {
            if (noteName == null) return null
            var group = noteName.octave
            var index = when (noteName.natural) {
                "C" -> 0
                "D" -> 2
                "E" -> 4
                "F" -> 5
                "G" -> 7
                "A" -> 9
                "B" -> 11
                else -> {
                    Log.e("PianoKey", "invalid natural, noteName: $noteName")
                    return null
                }
            }
            index += when (noteName.accidental) {
                "#" -> 1
                "b" -> -1
                "bb" -> -2
                "x" -> 2
                "" -> 0
                else -> {
                    Log.e("PianoKey", "invalid accidental, noteName: $noteName")
                    return null
                }
            }
            if (index > 11) {
                group++
                index -= 12
            } else if (index < 0) {
                group--
                index += 12
            }
            val keyType = when (index) {
                1, 3, 6, 8, 10 -> PianoKeyType.BLACK
                else -> PianoKeyType.WHITE
            }
            var indexInGroup = if (keyType == PianoKeyType.BLACK) {
                when (index) {
                    1 -> 0
                    3 -> 1
                    6 -> 2
                    8 -> 3
                    10 -> 4
                    else -> {
                        Log.e("PianoKey", "invalid index, noteName: $noteName")
                        return null
                    }
                }
            } else {
                when (index) {
                    0 -> 0
                    2 -> 1
                    4 -> 2
                    5 -> 3
                    7 -> 4
                    9 -> 5
                    11 -> 6
                    else -> {
                        Log.e("PianoKey", "invalid index, noteName: $noteName")
                        return null
                    }
                }
            }
            if (keyType == PianoKeyType.BLACK && group == 0 && indexInGroup == 4) {
                indexInGroup = 0
            } else if (keyType == PianoKeyType.WHITE && group == 0 && indexInGroup == 5) {
                indexInGroup = 0
            } else if (keyType == PianoKeyType.WHITE && group == 0 && indexInGroup == 6) {
                indexInGroup = 1
            }
            if (!keyExists(keyType, group, indexInGroup)) {
                Log.e("PianoKey", "key does not exist: $keyType, $group, $indexInGroup")
                return null
            }
            return PianoKey(keyType, group, indexInGroup, noteName)
        }

        fun create(
            type: PianoKeyType,
            group: Int,
            index: Int,
        ): PianoKey = PianoKey(type, group, index, NoteName.firstWith(type, group, index))
    }
}

enum class PianoKeyType {
    BLACK, WHITE
}


/*
valid note names with octave 0:
0   C0  B#0 Dbb0    -> C    His     Deses
1   C#0 Db0 Bx0     -> Cis  Des     Hisis
2   D0  Cx0 Ebb0    -> D    Cisis   Eses
3   D#0 Eb0 Fbb0    -> Dis  Es      Feses
4   E0  Fb0 Dx0     -> E    Fes     Dis
5   F0  E#0 Gbb0    -> F    Eis     Geses
6   F#0 Gb0 Ex0     -> Fis  Ges     Eisis
7   G0  Fx0 Abb0    -> G    Fisis   Ases
8   G#0 Ab0         -> Gis  As
9   A0  Gx0 Bbb0    -> A    Gisis   Heses
10  A#0 Bb0 Cbb0    -> Ais  Hes     Ceses
11  B0  Cb0 Ax0     -> B    Ces     Aisis
... octave 1 ...
 */
data class NoteName(
    val natural: String,
    val octave: Int,
    val accidental: String,
) {
    override fun toString(): String = "$natural$accidental$octave"

    val germanNoteName: String
        get() {
            when (natural) {
                "B" -> {
                    return when (accidental) {
                        "b" -> "B$octave"
                        "bb" -> "Heses$octave"
                        "#" -> "His$octave"
                        "x" -> "Hisis$octave"
                        else -> "H$octave"
                    }
                }

                "E" -> {
                    when (accidental) {
                        "b" -> return "Es$octave"
                        "bb" -> return "Eses$octave"
                    }
                }

                "A" -> {
                    when (accidental) {
                        "b" -> return "As$octave"
                        "bb" -> return "Ases$octave"
                    }
                }
            }
            val germanAccidental = when (accidental) {
                "b" -> "es"
                "bb" -> "eses"
                "#" -> "is"
                "x" -> "isis"
                else -> ""
            }
            return "$natural$germanAccidental$octave"
        }

    companion object {
        fun create(noteName: String): NoteName? {
            //match regex A-G, "" or # or b or bb or x, 0-8
            if (!noteName.matches(Regex("^[A-G](#|b|bb|x)?[0-8]$"))) {
                Log.e("NoteName", "invalid note name: $noteName")
                return null
            }
            val natural = noteName[0].toString()
            val octave = noteName.last().toString().toInt()
            val accidental = when (noteName.length) {
                4 -> noteName[1].toString() + noteName[2].toString()
                3 -> noteName[1].toString()
                else -> ""
            }
            return NoteName(natural, octave, accidental)
        }

        fun firstWith(type: PianoKeyType, group: Int, indexInGroup: Int): NoteName = when (type) {
            PianoKeyType.BLACK -> {
                when (group) {
                    0 -> create("A#0")!!
                    else -> when (indexInGroup) {
                        0 -> create("C#$group")!!
                        1 -> create("D#$group")!!
                        2 -> create("F#$group")!!
                        3 -> create("G#$group")!!
                        4 -> create("A#$group")!!
                        else -> create("C#$group")!!
                    }
                }
            }

            PianoKeyType.WHITE -> {
                when (group) {
                    0 -> if (indexInGroup == 0) create("A0")!! else create("B0")!!
                    8 -> create("C8")!!
                    else ->
                        when (indexInGroup) {
                            0 -> create("C$group")!!
                            1 -> create("D$group")!!
                            2 -> create("E$group")!!
                            3 -> create("F$group")!!
                            4 -> create("G$group")!!
                            5 -> create("A$group")!!
                            6 -> create("B$group")!!
                            else -> create("C$group")!!
                        }
                }
            }
        }
    }
}