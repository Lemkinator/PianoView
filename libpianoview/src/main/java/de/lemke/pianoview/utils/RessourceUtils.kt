package de.lemke.pianoview.utils

import de.lemke.pianoview.R
import de.lemke.pianoview.entity.PianoKey
import de.lemke.pianoview.entity.PianoKeyType


fun PianoKey.getSoundResId(): Int {
    return when (type) {
        PianoKeyType.BLACK -> {
            when (group) {
                0 -> R.raw.b00
                1 -> when (indexInGroup) {
                    0 -> R.raw.b10
                    1 -> R.raw.b11
                    2 -> R.raw.b12
                    3 -> R.raw.b13
                    else -> R.raw.b14
                }

                2 -> when (indexInGroup) {
                    0 -> R.raw.b20
                    1 -> R.raw.b21
                    2 -> R.raw.b22
                    3 -> R.raw.b23
                    else -> R.raw.b24
                }

                3 -> when (indexInGroup) {
                    0 -> R.raw.b30
                    1 -> R.raw.b31
                    2 -> R.raw.b32
                    3 -> R.raw.b33
                    else -> R.raw.b34
                }

                4 -> when (indexInGroup) {
                    0 -> R.raw.b40
                    1 -> R.raw.b41
                    2 -> R.raw.b42
                    3 -> R.raw.b43
                    else -> R.raw.b44
                }

                5 -> when (indexInGroup) {
                    0 -> R.raw.b50
                    1 -> R.raw.b51
                    2 -> R.raw.b52
                    3 -> R.raw.b53
                    else -> R.raw.b54
                }

                6 -> when (indexInGroup) {
                    0 -> R.raw.b60
                    1 -> R.raw.b61
                    2 -> R.raw.b62
                    3 -> R.raw.b63
                    else -> R.raw.b64
                }

                else -> {
                    when (indexInGroup) {
                        0 -> R.raw.b70
                        1 -> R.raw.b71
                        2 -> R.raw.b72
                        3 -> R.raw.b73
                        else -> R.raw.b74
                    }
                }
            }
        }

        PianoKeyType.WHITE -> {
            when (group) {
                0 -> when (indexInGroup) {
                    0 -> R.raw.w00
                    else -> R.raw.w01
                }

                1 -> when (indexInGroup) {
                    0 -> R.raw.w10
                    1 -> R.raw.w11
                    2 -> R.raw.w12
                    3 -> R.raw.w13
                    4 -> R.raw.w14
                    5 -> R.raw.w15
                    else -> R.raw.w16
                }

                2 -> when (indexInGroup) {
                    0 -> R.raw.w20
                    1 -> R.raw.w21
                    2 -> R.raw.w22
                    3 -> R.raw.w23
                    4 -> R.raw.w24
                    5 -> R.raw.w25
                    else -> R.raw.w26
                }

                3 -> when (indexInGroup) {
                    0 -> R.raw.w30
                    1 -> R.raw.w31
                    2 -> R.raw.w32
                    3 -> R.raw.w33
                    4 -> R.raw.w34
                    5 -> R.raw.w35
                    else -> R.raw.w36
                }

                4 -> when (indexInGroup) {
                    0 -> R.raw.w40
                    1 -> R.raw.w41
                    2 -> R.raw.w42
                    3 -> R.raw.w43
                    4 -> R.raw.w44
                    5 -> R.raw.w45
                    else -> R.raw.w46
                }

                5 -> when (indexInGroup) {
                    0 -> R.raw.w50
                    1 -> R.raw.w51
                    2 -> R.raw.w52
                    3 -> R.raw.w53
                    4 -> R.raw.w54
                    5 -> R.raw.w55
                    else -> R.raw.w56
                }

                6 -> when (indexInGroup) {
                    0 -> R.raw.w60
                    1 -> R.raw.w61
                    2 -> R.raw.w62
                    3 -> R.raw.w63
                    4 -> R.raw.w64
                    5 -> R.raw.w65
                    else -> R.raw.w66
                }

                7 -> when (indexInGroup) {
                    0 -> R.raw.w70
                    1 -> R.raw.w71
                    2 -> R.raw.w72
                    3 -> R.raw.w73
                    4 -> R.raw.w74
                    5 -> R.raw.w75
                    else -> R.raw.w76
                }

                else -> R.raw.w80
            }
        }
    }
}

@Suppress("unused")
val predefinedPianoKeys: MutableList<PianoKey> = mutableListOf(
    PianoKey.create(PianoKeyType.WHITE, 0, 0), //A0
    PianoKey.create(PianoKeyType.BLACK, 0, 0), //A#0
    PianoKey.create(PianoKeyType.WHITE, 0, 1), //B0
    PianoKey.create(PianoKeyType.WHITE, 1, 0), //C1
    PianoKey.create(PianoKeyType.BLACK, 1, 0), //C#1
    PianoKey.create(PianoKeyType.WHITE, 1, 1), //D1
    PianoKey.create(PianoKeyType.BLACK, 1, 1), //D#1
    PianoKey.create(PianoKeyType.WHITE, 1, 2), //E1
    PianoKey.create(PianoKeyType.WHITE, 1, 3), //F1
    PianoKey.create(PianoKeyType.BLACK, 1, 2), //F#1
    PianoKey.create(PianoKeyType.WHITE, 1, 4), //G1
    PianoKey.create(PianoKeyType.BLACK, 1, 3), //G#1
    PianoKey.create(PianoKeyType.WHITE, 1, 5), //A1
    PianoKey.create(PianoKeyType.BLACK, 1, 4), //A#1
    PianoKey.create(PianoKeyType.WHITE, 1, 6), //B1
    PianoKey.create(PianoKeyType.WHITE, 2, 0), //C2
    PianoKey.create(PianoKeyType.BLACK, 2, 0), //C#2
    PianoKey.create(PianoKeyType.WHITE, 2, 1), //D2
    PianoKey.create(PianoKeyType.BLACK, 2, 1), //D#2
    PianoKey.create(PianoKeyType.WHITE, 2, 2), //E2
    PianoKey.create(PianoKeyType.WHITE, 2, 3), //F2
    PianoKey.create(PianoKeyType.BLACK, 2, 2), //F#2
    PianoKey.create(PianoKeyType.WHITE, 2, 4), //G2
    PianoKey.create(PianoKeyType.BLACK, 2, 3), //G#2
    PianoKey.create(PianoKeyType.WHITE, 2, 5), //A2
    PianoKey.create(PianoKeyType.BLACK, 2, 4), //A#2
    PianoKey.create(PianoKeyType.WHITE, 2, 6), //B2
    PianoKey.create(PianoKeyType.WHITE, 3, 0), //C3
    PianoKey.create(PianoKeyType.BLACK, 3, 0), //C#3
    PianoKey.create(PianoKeyType.WHITE, 3, 1), //D3
    PianoKey.create(PianoKeyType.BLACK, 3, 1), //D#3
    PianoKey.create(PianoKeyType.WHITE, 3, 2), //E3
    PianoKey.create(PianoKeyType.WHITE, 3, 3), //F3
    PianoKey.create(PianoKeyType.BLACK, 3, 2), //F#3
    PianoKey.create(PianoKeyType.WHITE, 3, 4), //G3
    PianoKey.create(PianoKeyType.BLACK, 3, 3), //G#3
    PianoKey.create(PianoKeyType.WHITE, 3, 5), //A3
    PianoKey.create(PianoKeyType.BLACK, 3, 4), //A#3
    PianoKey.create(PianoKeyType.WHITE, 3, 6), //B3
    PianoKey.create(PianoKeyType.WHITE, 4, 0), //C4
    PianoKey.create(PianoKeyType.BLACK, 4, 0), //C#4
    PianoKey.create(PianoKeyType.WHITE, 4, 1), //D4
    PianoKey.create(PianoKeyType.BLACK, 4, 1), //D#4
    PianoKey.create(PianoKeyType.WHITE, 4, 2), //E4
    PianoKey.create(PianoKeyType.WHITE, 4, 3), //F4
    PianoKey.create(PianoKeyType.BLACK, 4, 2), //F#4
    PianoKey.create(PianoKeyType.WHITE, 4, 4), //G4
    PianoKey.create(PianoKeyType.BLACK, 4, 3), //G#4
    PianoKey.create(PianoKeyType.WHITE, 4, 5), //A4
    PianoKey.create(PianoKeyType.BLACK, 4, 4), //A#4
    PianoKey.create(PianoKeyType.WHITE, 4, 6), //B4
    PianoKey.create(PianoKeyType.WHITE, 5, 0), //C5
    PianoKey.create(PianoKeyType.BLACK, 5, 0), //C#5
    PianoKey.create(PianoKeyType.WHITE, 5, 1), //D5
    PianoKey.create(PianoKeyType.BLACK, 5, 1), //D#5
    PianoKey.create(PianoKeyType.WHITE, 5, 2), //E5
    PianoKey.create(PianoKeyType.WHITE, 5, 3), //F5
    PianoKey.create(PianoKeyType.BLACK, 5, 2), //F#5
    PianoKey.create(PianoKeyType.WHITE, 5, 4), //G5
    PianoKey.create(PianoKeyType.BLACK, 5, 3), //G#5
    PianoKey.create(PianoKeyType.WHITE, 5, 5), //A5
    PianoKey.create(PianoKeyType.BLACK, 5, 4), //A#5
    PianoKey.create(PianoKeyType.WHITE, 5, 6), //B5
    PianoKey.create(PianoKeyType.WHITE, 6, 0), //C6
    PianoKey.create(PianoKeyType.BLACK, 6, 0), //C#6
    PianoKey.create(PianoKeyType.WHITE, 6, 1), //D6
    PianoKey.create(PianoKeyType.BLACK, 6, 1), //D#6
    PianoKey.create(PianoKeyType.WHITE, 6, 2), //E6
    PianoKey.create(PianoKeyType.WHITE, 6, 3), //F6
    PianoKey.create(PianoKeyType.BLACK, 6, 2), //F#6
    PianoKey.create(PianoKeyType.WHITE, 6, 4), //G6
    PianoKey.create(PianoKeyType.BLACK, 6, 3), //G#6
    PianoKey.create(PianoKeyType.WHITE, 6, 5), //A6
    PianoKey.create(PianoKeyType.BLACK, 6, 4), //A#6
    PianoKey.create(PianoKeyType.WHITE, 6, 6), //B6
    PianoKey.create(PianoKeyType.WHITE, 7, 0), //C7
    PianoKey.create(PianoKeyType.BLACK, 7, 0), //C#7
    PianoKey.create(PianoKeyType.WHITE, 7, 1), //D7
    PianoKey.create(PianoKeyType.BLACK, 7, 1), //D#7
    PianoKey.create(PianoKeyType.WHITE, 7, 2), //E7
    PianoKey.create(PianoKeyType.WHITE, 7, 3), //F7
    PianoKey.create(PianoKeyType.BLACK, 7, 2), //F#7
    PianoKey.create(PianoKeyType.WHITE, 7, 4), //G7
    PianoKey.create(PianoKeyType.BLACK, 7, 3), //G#7
    PianoKey.create(PianoKeyType.WHITE, 7, 5), //A7
    PianoKey.create(PianoKeyType.BLACK, 7, 4), //A#7
    PianoKey.create(PianoKeyType.WHITE, 7, 6), //B7
    PianoKey.create(PianoKeyType.WHITE, 8, 0), //C8
)

val predefinedSortedPianoKeys: MutableList<PianoKey> = mutableListOf(
    PianoKey.create(PianoKeyType.WHITE, 0, 0), //A0
    PianoKey.create(PianoKeyType.WHITE, 0, 1), //B0
    PianoKey.create(PianoKeyType.WHITE, 1, 0), //C1
    PianoKey.create(PianoKeyType.WHITE, 1, 1), //D1
    PianoKey.create(PianoKeyType.WHITE, 1, 2), //E1
    PianoKey.create(PianoKeyType.WHITE, 1, 3), //F1
    PianoKey.create(PianoKeyType.WHITE, 1, 4), //G1
    PianoKey.create(PianoKeyType.WHITE, 1, 5), //A1
    PianoKey.create(PianoKeyType.WHITE, 1, 6), //B1
    PianoKey.create(PianoKeyType.WHITE, 2, 0), //C2
    PianoKey.create(PianoKeyType.WHITE, 2, 1), //D2
    PianoKey.create(PianoKeyType.WHITE, 2, 2), //E2
    PianoKey.create(PianoKeyType.WHITE, 2, 3), //F2
    PianoKey.create(PianoKeyType.WHITE, 2, 4), //G2
    PianoKey.create(PianoKeyType.WHITE, 2, 5), //A2
    PianoKey.create(PianoKeyType.WHITE, 2, 6), //B2
    PianoKey.create(PianoKeyType.WHITE, 3, 0), //C3
    PianoKey.create(PianoKeyType.WHITE, 3, 1), //D3
    PianoKey.create(PianoKeyType.WHITE, 3, 2), //E3
    PianoKey.create(PianoKeyType.WHITE, 3, 3), //F3
    PianoKey.create(PianoKeyType.WHITE, 3, 4), //G3
    PianoKey.create(PianoKeyType.WHITE, 3, 5), //A3
    PianoKey.create(PianoKeyType.WHITE, 3, 6), //B3
    PianoKey.create(PianoKeyType.WHITE, 4, 0), //C4
    PianoKey.create(PianoKeyType.WHITE, 4, 1), //D4
    PianoKey.create(PianoKeyType.WHITE, 4, 2), //E4
    PianoKey.create(PianoKeyType.WHITE, 4, 3), //F4
    PianoKey.create(PianoKeyType.WHITE, 4, 4), //G4
    PianoKey.create(PianoKeyType.WHITE, 4, 5), //A4
    PianoKey.create(PianoKeyType.WHITE, 4, 6), //B4
    PianoKey.create(PianoKeyType.WHITE, 5, 0), //C5
    PianoKey.create(PianoKeyType.WHITE, 5, 1), //D5
    PianoKey.create(PianoKeyType.WHITE, 5, 2), //E5
    PianoKey.create(PianoKeyType.WHITE, 5, 3), //F5
    PianoKey.create(PianoKeyType.WHITE, 5, 4), //G5
    PianoKey.create(PianoKeyType.WHITE, 5, 5), //A5
    PianoKey.create(PianoKeyType.WHITE, 5, 6), //B5
    PianoKey.create(PianoKeyType.WHITE, 6, 0), //C6
    PianoKey.create(PianoKeyType.WHITE, 6, 1), //D6
    PianoKey.create(PianoKeyType.WHITE, 6, 2), //E6
    PianoKey.create(PianoKeyType.WHITE, 6, 3), //F6
    PianoKey.create(PianoKeyType.WHITE, 6, 4), //G6
    PianoKey.create(PianoKeyType.WHITE, 6, 5), //A6
    PianoKey.create(PianoKeyType.WHITE, 6, 6), //B6
    PianoKey.create(PianoKeyType.WHITE, 7, 0), //C7
    PianoKey.create(PianoKeyType.WHITE, 7, 1), //D7
    PianoKey.create(PianoKeyType.WHITE, 7, 2), //E7
    PianoKey.create(PianoKeyType.WHITE, 7, 3), //F7
    PianoKey.create(PianoKeyType.WHITE, 7, 4), //G7
    PianoKey.create(PianoKeyType.WHITE, 7, 5), //A7
    PianoKey.create(PianoKeyType.WHITE, 7, 6), //B7
    PianoKey.create(PianoKeyType.WHITE, 8, 0),  //C8
    PianoKey.create(PianoKeyType.BLACK, 0, 0), //A#0
    PianoKey.create(PianoKeyType.BLACK, 1, 0), //C#1
    PianoKey.create(PianoKeyType.BLACK, 1, 1), //D#1
    PianoKey.create(PianoKeyType.BLACK, 1, 2), //F#1
    PianoKey.create(PianoKeyType.BLACK, 1, 3), //G#1
    PianoKey.create(PianoKeyType.BLACK, 1, 4), //A#1
    PianoKey.create(PianoKeyType.BLACK, 2, 0), //C#2
    PianoKey.create(PianoKeyType.BLACK, 2, 1), //D#2
    PianoKey.create(PianoKeyType.BLACK, 2, 2), //F#2
    PianoKey.create(PianoKeyType.BLACK, 2, 3), //G#2
    PianoKey.create(PianoKeyType.BLACK, 2, 4), //A#2
    PianoKey.create(PianoKeyType.BLACK, 3, 0), //C#3
    PianoKey.create(PianoKeyType.BLACK, 3, 1), //D#3
    PianoKey.create(PianoKeyType.BLACK, 3, 2), //F#3
    PianoKey.create(PianoKeyType.BLACK, 3, 3), //G#3
    PianoKey.create(PianoKeyType.BLACK, 3, 4), //A#3
    PianoKey.create(PianoKeyType.BLACK, 4, 0), //C#4
    PianoKey.create(PianoKeyType.BLACK, 4, 1), //D#4
    PianoKey.create(PianoKeyType.BLACK, 4, 2), //F#4
    PianoKey.create(PianoKeyType.BLACK, 4, 3), //G#4
    PianoKey.create(PianoKeyType.BLACK, 4, 4), //A#4
    PianoKey.create(PianoKeyType.BLACK, 5, 0), //C#5
    PianoKey.create(PianoKeyType.BLACK, 5, 1), //D#5
    PianoKey.create(PianoKeyType.BLACK, 5, 2), //F#5
    PianoKey.create(PianoKeyType.BLACK, 5, 3), //G#5
    PianoKey.create(PianoKeyType.BLACK, 5, 4), //A#5
    PianoKey.create(PianoKeyType.BLACK, 6, 0), //C#6
    PianoKey.create(PianoKeyType.BLACK, 6, 1), //D#6
    PianoKey.create(PianoKeyType.BLACK, 6, 2), //F#6
    PianoKey.create(PianoKeyType.BLACK, 6, 3), //G#6
    PianoKey.create(PianoKeyType.BLACK, 6, 4), //A#6
    PianoKey.create(PianoKeyType.BLACK, 7, 0), //C#7
    PianoKey.create(PianoKeyType.BLACK, 7, 1), //D#7
    PianoKey.create(PianoKeyType.BLACK, 7, 2), //F#7
    PianoKey.create(PianoKeyType.BLACK, 7, 3), //G#7
    PianoKey.create(PianoKeyType.BLACK, 7, 4), //A#7
)
