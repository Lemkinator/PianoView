package com.chengtao.pianoview.entity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.ScaleDrawable
import android.view.Gravity
import androidx.core.content.ContextCompat
import com.chengtao.pianoview.R
import kotlin.math.sqrt


class Piano(private val context: Context, private val scale: Float) {
    val blackPianoKeys: MutableList<List<PianoKey>> = mutableListOf()
    val whitePianoKeys: MutableList<List<PianoKey>> = mutableListOf()
    private var blackKeyWidth = 0
    private var blackKeyHeight = 0
    private var whiteKeyWidth = 0
    private var whiteKeyHeight = 0
    var pianoWith = 0

    init {
        initPiano()
    }


    private fun initPiano() {
        if (scale > 0) {
            val blackDrawable = ContextCompat.getDrawable(context, R.drawable.black_piano_key)
            val whiteDrawable = ContextCompat.getDrawable(context, R.drawable.white_piano_key)
            val xInches = context.resources.displayMetrics.widthPixels / context.resources.displayMetrics.xdpi
            //val yInches = context.resources.displayMetrics.heightPixels / context.resources.displayMetrics.ydpi
            //val diagonalInches = sqrt((xInches * xInches + yInches * yInches).toDouble())
            blackKeyWidth = blackDrawable!!.intrinsicWidth
            blackKeyWidth = (blackDrawable.intrinsicWidth * sqrt(xInches) / 2.15).toInt()
            blackKeyHeight = (blackDrawable.intrinsicHeight * scale).toInt()
            whiteKeyWidth = whiteDrawable!!.intrinsicWidth
            whiteKeyWidth = (whiteDrawable.intrinsicWidth * sqrt(xInches) / 2.15).toInt()
            whiteKeyHeight = (whiteDrawable.intrinsicHeight * scale).toInt()
            for (group in 0 until 8) {
                val keys: List<PianoKey> = List(if (group == 0) 1 else 5) { index ->
                    val drawable = ScaleDrawable(
                        ContextCompat.getDrawable(context, R.drawable.black_piano_key),
                        Gravity.NO_GRAVITY, scale, scale
                    ).drawable!!
                    setBlackKeyDrawableBounds(group, index, drawable)
                    PianoKey(
                        PianoKeyType.BLACK,
                        group,
                        index,
                        drawable,
                        getVoiceFromResources(PianoKeyType.BLACK, group, index),
                        listOf(drawable.bounds)
                    )
                }
                blackPianoKeys.add(keys)
            }
            for (group in 0 until 9) {
                val keys: List<PianoKey> = List(
                    when (group) {
                        0 -> 2
                        8 -> 1
                        else -> 7
                    }
                ) { index ->
                    pianoWith += whiteKeyWidth
                    val drawable = ScaleDrawable(
                        ContextCompat.getDrawable(context, R.drawable.white_piano_key),
                        Gravity.NO_GRAVITY, scale, scale
                    ).drawable!!
                    setWhiteKeyDrawableBounds(group, index, drawable)
                    PianoKey(
                        PianoKeyType.WHITE,
                        group,
                        index,
                        drawable,
                        getVoiceFromResources(PianoKeyType.WHITE, group, index),
                        when (group) {
                            0 -> when (index) {
                                0 -> getWhitePianoKeyArea(group, index, BlackKeyPosition.RIGHT)
                                else -> getWhitePianoKeyArea(group, index, BlackKeyPosition.LEFT) // 1
                            }

                            8 -> listOf(drawable.bounds)
                            else -> when (index) {
                                0 -> getWhitePianoKeyArea(group, index, BlackKeyPosition.RIGHT)
                                1 -> getWhitePianoKeyArea(group, index, BlackKeyPosition.LEFT_RIGHT)
                                2 -> getWhitePianoKeyArea(group, index, BlackKeyPosition.LEFT)
                                3 -> getWhitePianoKeyArea(group, index, BlackKeyPosition.RIGHT)
                                4 -> getWhitePianoKeyArea(group, index, BlackKeyPosition.LEFT_RIGHT)
                                5 -> getWhitePianoKeyArea(group, index, BlackKeyPosition.LEFT_RIGHT)
                                else -> getWhitePianoKeyArea(group, index, BlackKeyPosition.LEFT) // 6
                            }
                        }
                    )
                }
                whitePianoKeys.add(keys)
            }
        }
    }

    enum class PianoKeyType {
        BLACK, WHITE
    }

    private enum class BlackKeyPosition {
        LEFT, LEFT_RIGHT, RIGHT
    }

    @SuppressLint("DiscouragedApi")
    private fun getVoiceFromResources(keyType: PianoKeyType, group: Int, index: Int): Int = context.resources.getIdentifier(
        if (keyType == PianoKeyType.BLACK) { "b" } else { "w" } + group + index,
        "raw",
        context.packageName
    )

    private fun getWhitePianoKeyArea(group: Int, positionOfGroup: Int, blackKeyPosition: BlackKeyPosition): List<Rect> {
        val offset = if (group == 0) 5 else 0
        return when (blackKeyPosition) {
            BlackKeyPosition.LEFT -> {
                listOf(
                    Rect(
                        (7 * group - 5 + offset + positionOfGroup) * whiteKeyWidth, blackKeyHeight,
                        (7 * group - 5 + offset + positionOfGroup) * whiteKeyWidth + blackKeyWidth / 2,
                        whiteKeyHeight
                    ),
                    Rect(
                        (7 * group - 5 + offset + positionOfGroup) * whiteKeyWidth + blackKeyWidth / 2,
                        0, (7 * group - 4 + offset + positionOfGroup) * whiteKeyWidth, whiteKeyHeight
                    )
                )
            }

            BlackKeyPosition.LEFT_RIGHT -> {
                listOf(
                    Rect(
                        (7 * group - 5 + offset + positionOfGroup) * whiteKeyWidth, blackKeyHeight,
                        (7 * group - 5 + offset + positionOfGroup) * whiteKeyWidth + blackKeyWidth / 2,
                        whiteKeyHeight
                    ),
                    Rect(
                        (7 * group - 5 + offset + positionOfGroup) * whiteKeyWidth + blackKeyWidth / 2,
                        0, (7 * group - 4 + offset + positionOfGroup) * whiteKeyWidth - blackKeyWidth / 2,
                        whiteKeyHeight
                    ),
                    Rect(
                        (7 * group - 4 + offset + positionOfGroup) * whiteKeyWidth - blackKeyWidth / 2,
                        blackKeyHeight, (7 * group - 4 + offset + positionOfGroup) * whiteKeyWidth,
                        whiteKeyHeight
                    )
                )
            }

            BlackKeyPosition.RIGHT -> {
                listOf(
                    Rect(
                        (7 * group - 5 + offset + positionOfGroup) * whiteKeyWidth, blackKeyHeight,
                        (7 * group - 5 + offset + positionOfGroup) * whiteKeyWidth + blackKeyWidth / 2,
                        whiteKeyHeight
                    ),
                    Rect(
                        (7 * group - 5 + offset + positionOfGroup) * whiteKeyWidth + blackKeyWidth / 2,
                        0, (7 * group - 4 + offset + positionOfGroup) * whiteKeyWidth, whiteKeyHeight
                    )
                )
            }
        }
    }

    private fun setWhiteKeyDrawableBounds(group: Int, positionOfGroup: Int, drawable: Drawable) {
        val offset = if (group == 0) 5 else 0
        drawable.setBounds(
            (7 * group - 5 + offset + positionOfGroup) * whiteKeyWidth, 0,
            (7 * group - 4 + offset + positionOfGroup) * whiteKeyWidth, whiteKeyHeight
        )
    }

    private fun setBlackKeyDrawableBounds(group: Int, index: Int, drawable: Drawable) {
        val whiteOffset = if (group == 0) 5 else 0
        val blackOffset = if (index == 2 || index == 3 || index == 4) 1 else 0
        drawable.setBounds(
            (7 * group - 4 + whiteOffset + blackOffset + index) * whiteKeyWidth
                    - blackKeyWidth / 2, 0, (7 * group - 4 + whiteOffset + blackOffset + index) * whiteKeyWidth
                    + blackKeyWidth / 2, blackKeyHeight
        )
    }
}