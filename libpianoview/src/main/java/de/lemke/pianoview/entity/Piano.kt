package de.lemke.pianoview.entity

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.ScaleDrawable
import androidx.appcompat.content.res.AppCompatResources
import de.lemke.pianoview.R
import de.lemke.pianoview.utils.predefinedSortedPianoKeys


class Piano {
    private var blackKeyWidth = 1
    private var blackKeyHeight = 1
    var whiteKeyWidth = 1
    private var whiteKeyHeight = 1
    val pianoWith
        get() = whiteKeyWidth * 52
    var uiInitialized = false
    val pianoKeys: MutableList<PianoKey> = predefinedSortedPianoKeys

    fun setDrawableAndBounds(context: Context, scale: Float, layoutWith: Int, visibleKeys: Int) {
        if (scale > 0) {
            uiInitialized = true
            val blackDrawable = AppCompatResources.getDrawable(context, R.drawable.black_piano_key)
            val whiteDrawable = AppCompatResources.getDrawable(context, R.drawable.white_piano_key)
            val widthScale: Float = (layoutWith.toFloat() / visibleKeys.toFloat()) / whiteDrawable!!.intrinsicWidth.toFloat()
            blackKeyWidth = (blackDrawable!!.intrinsicWidth * widthScale).toInt()
            blackKeyHeight = (blackDrawable.intrinsicHeight * scale).toInt()
            whiteKeyWidth = layoutWith / visibleKeys
            whiteKeyHeight = (whiteDrawable.intrinsicHeight * scale).toInt()
            pianoKeys.forEach {
                if (it.type == PianoKeyType.WHITE) {
                    val drawable = ScaleDrawable(
                        AppCompatResources.getDrawable(context, R.drawable.white_piano_key),
                        0, scale, scale
                    ).drawable!!
                    setWhiteKeyDrawableBounds(it.group, it.indexInGroup, drawable)
                    it.keyDrawable = drawable
                    it.areaOfKey = getWhitePianoKeyArea(it.group, it.indexInGroup, drawable)
                } else {
                    val drawable = ScaleDrawable(
                        AppCompatResources.getDrawable(context, R.drawable.black_piano_key),
                        0, scale, scale
                    ).drawable!!
                    setBlackKeyDrawableBounds(it.group, it.indexInGroup, drawable)
                    it.keyDrawable = drawable
                    it.areaOfKey = listOf(drawable.bounds)
                }
            }
        }
    }


    private enum class BlackKeyPosition {
        LEFT, LEFT_RIGHT, RIGHT
    }

    private fun getWhitePianoKeyArea(group: Int, indexInGroup: Int, drawable: Drawable): List<Rect> {
        if (group == 8) return listOf(drawable.bounds)
        val blackKeyPosition = when (group) {
            0 -> when (indexInGroup) {
                0 -> BlackKeyPosition.RIGHT
                else -> BlackKeyPosition.LEFT
            }

            else -> when (indexInGroup) {
                0 -> BlackKeyPosition.RIGHT
                1 -> BlackKeyPosition.LEFT_RIGHT
                2 -> BlackKeyPosition.LEFT
                3 -> BlackKeyPosition.RIGHT
                4 -> BlackKeyPosition.LEFT_RIGHT
                5 -> BlackKeyPosition.LEFT_RIGHT
                else -> BlackKeyPosition.LEFT
            }
        }
        val offset = if (group == 0) 5 else 0
        return when (blackKeyPosition) {
            BlackKeyPosition.LEFT -> {
                listOf(
                    Rect(
                        (7 * group - 5 + offset + indexInGroup) * whiteKeyWidth, blackKeyHeight,
                        (7 * group - 5 + offset + indexInGroup) * whiteKeyWidth + blackKeyWidth / 2,
                        whiteKeyHeight
                    ),
                    Rect(
                        (7 * group - 5 + offset + indexInGroup) * whiteKeyWidth + blackKeyWidth / 2,
                        0, (7 * group - 4 + offset + indexInGroup) * whiteKeyWidth, whiteKeyHeight
                    )
                )
            }

            BlackKeyPosition.LEFT_RIGHT -> {
                listOf(
                    Rect(
                        (7 * group - 5 + indexInGroup) * whiteKeyWidth, blackKeyHeight,
                        (7 * group - 5 + indexInGroup) * whiteKeyWidth + blackKeyWidth / 2,
                        whiteKeyHeight
                    ),
                    Rect(
                        (7 * group - 5 + indexInGroup) * whiteKeyWidth + blackKeyWidth / 2,
                        0, (7 * group - 4 + indexInGroup) * whiteKeyWidth - blackKeyWidth / 2,
                        whiteKeyHeight
                    ),
                    Rect(
                        (7 * group - 4 + indexInGroup) * whiteKeyWidth - blackKeyWidth / 2,
                        blackKeyHeight, (7 * group - 4 + indexInGroup) * whiteKeyWidth,
                        whiteKeyHeight
                    )
                )
            }

            BlackKeyPosition.RIGHT -> {
                listOf(
                    Rect(
                        (7 * group - 5 + offset + indexInGroup) * whiteKeyWidth, 0,
                        (7 * group - 4 + offset + indexInGroup) * whiteKeyWidth - blackKeyWidth / 2,
                        whiteKeyHeight
                    ),
                    Rect(
                        (7 * group - 4 + offset + indexInGroup) * whiteKeyWidth - blackKeyWidth / 2,
                        blackKeyHeight, (7 * group - 4 + offset + indexInGroup) * whiteKeyWidth, whiteKeyHeight
                    )
                )
            }
        }
    }

    private fun setWhiteKeyDrawableBounds(group: Int, indexInGroup: Int, drawable: Drawable) {
        val offset = if (group == 0) 5 else 0
        drawable.setBounds(
            (7 * group - 5 + offset + indexInGroup) * whiteKeyWidth, 0,
            (7 * group - 4 + offset + indexInGroup) * whiteKeyWidth, whiteKeyHeight
        )
    }

    private fun setBlackKeyDrawableBounds(group: Int, indexInGroup: Int, drawable: Drawable) {
        val whiteOffset = if (group == 0) 5 else 0
        val blackOffset = if (indexInGroup == 2 || indexInGroup == 3 || indexInGroup == 4) 1 else 0
        drawable.setBounds(
            (7 * group - 4 + whiteOffset + blackOffset + indexInGroup) * whiteKeyWidth
                    - blackKeyWidth / 2, 0, (7 * group - 4 + whiteOffset + blackOffset + indexInGroup) * whiteKeyWidth
                    + blackKeyWidth / 2, blackKeyHeight
        )
    }
}