package com.chengtao.pianoview.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.chengtao.pianoview.R
import com.chengtao.pianoview.entity.Piano
import com.chengtao.pianoview.entity.PianoKey
import com.chengtao.pianoview.listener.OnLoadAudioListener
import com.chengtao.pianoview.listener.OnPianoListener
import com.chengtao.pianoview.utils.AudioUtils
import java.util.concurrent.CopyOnWriteArrayList

class PianoView @JvmOverloads constructor(private val context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(
    context, attrs, defStyleAttr
) {
    private var piano: Piano? = null
    private val pressedKeys: CopyOnWriteArrayList<PianoKey> = CopyOnWriteArrayList()
    private val paint: Paint = Paint()
    private val square: RectF
    private var pianoColors = intArrayOf(
        getContext().getColor(R.color.piano_key_description_0),
        getContext().getColor(R.color.piano_key_description_1),
        getContext().getColor(R.color.piano_key_description_2),
        getContext().getColor(R.color.piano_key_description_3),
        getContext().getColor(R.color.piano_key_description_4),
        getContext().getColor(R.color.piano_key_description_5),
        getContext().getColor(R.color.piano_key_description_6),
        getContext().getColor(R.color.piano_key_description_7),
        getContext().getColor(R.color.piano_key_description_8)
    )
    private var utils: AudioUtils? = null
    var layoutWidth = 0
        private set
    private var scale = 1f
    private var loadAudioListener: OnLoadAudioListener? = null
    private var pianoListener: OnPianoListener? = null
    private var progress = 0
    private var canPress = true
    private var isInitFinish = false
    private var maxStream = 0

    init {
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        square = RectF()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.e(TAG, "onMeasure")
        val whiteKeyDrawable = ContextCompat.getDrawable(context, R.drawable.white_piano_key)
        val whiteKeyHeight = whiteKeyDrawable!!.intrinsicHeight
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var height = MeasureSpec.getSize(heightMeasureSpec)
        when (heightMode) {
            MeasureSpec.AT_MOST -> height = height.coerceAtMost(whiteKeyHeight)
            MeasureSpec.UNSPECIFIED -> height = whiteKeyHeight
            else -> {}
        }
        scale = (height - paddingTop - paddingBottom).toFloat() / whiteKeyHeight.toFloat()
        layoutWidth = width - paddingLeft - paddingRight
        setMeasuredDimension(width, height)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        if (piano == null) {
            piano = Piano(context, scale)
            if (utils == null) {
                utils = if (maxStream > 0) {
                    AudioUtils(context, loadAudioListener, maxStream)
                } else {
                    AudioUtils(context, loadAudioListener)
                }
                try {
                    utils!!.loadMusic(piano)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            scroll(50)
        }
        if (piano?.whitePianoKeys != null) {
            for (i in piano?.whitePianoKeys!!.indices) {
                for (key in piano?.whitePianoKeys!![i]) {
                    paint.color = pianoColors[i]
                    key.keyDrawable.draw(canvas)
                    val r = key.keyDrawable.bounds
                    val sideLength = (r.right - r.left) / 2
                    val left = r.left + sideLength / 2
                    val top = r.bottom - sideLength - sideLength / 3
                    val right = r.right - sideLength / 2
                    val bottom = r.bottom - sideLength / 3
                    square[left.toFloat(), top.toFloat(), right.toFloat()] = bottom.toFloat()
                    canvas.drawRoundRect(square, 25f, 25f, paint)
                    paint.color = Color.BLACK
                    paint.textSize = sideLength / 1.8f
                    val fontMetrics = paint.fontMetricsInt
                    val baseline = ((square.bottom + square.top - fontMetrics.bottom - fontMetrics.top) / 2).toInt()
                    paint.textAlign = Paint.Align.CENTER
                    canvas.drawText(key.letterName, square.centerX(), baseline.toFloat(), paint)
                }
            }
        }
        if (piano?.blackPianoKeys != null) {
            for (i in piano?.blackPianoKeys!!.indices) {
                for (key in piano?.blackPianoKeys!![i]) {
                    key.keyDrawable.draw(canvas)
                }
            }
        }
        if (!isInitFinish && piano != null && pianoListener != null) {
            isInitFinish = true
            pianoListener!!.onPianoInitFinish()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked
        if (!canPress) {
            return false
        }
        when (action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> handleDown(event.actionIndex, event)
            MotionEvent.ACTION_MOVE -> {
                run {
                    var i = 0
                    while (i < event.pointerCount) {
                        handleMove(i, event)
                        i++
                    }
                }
                var i = 0
                while (i < event.pointerCount) {
                    handleDown(i, event)
                    i++
                }
            }

            MotionEvent.ACTION_POINTER_UP -> handlePointerUp(event.getPointerId(event.actionIndex))
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                handleUp()
                return false
            }

            else -> {}
        }
        return true
    }

    private fun handleDown(which: Int, event: MotionEvent) {
        val x = event.getX(which).toInt() + this.scrollX
        val y = event.getY(which).toInt()
        for (i in piano?.whitePianoKeys!!.indices) {
            for (key in piano?.whitePianoKeys!![i]) {
                if (!key.isPressed && key.contains(x, y)) {
                    handleWhiteKeyDown(which, event, key)
                }
            }
        }
        for (i in piano?.blackPianoKeys!!.indices) {
            for (key in piano?.blackPianoKeys!![i]) {
                if (!key.isPressed && key.contains(x, y)) {
                    handleBlackKeyDown(which, event, key)
                }
            }
        }
    }

    private fun handleWhiteKeyDown(which: Int, event: MotionEvent, key: PianoKey) {
        key.keyDrawable.state = intArrayOf(android.R.attr.state_pressed)
        key.isPressed = true
        key.fingerID = event.getPointerId(which)
        pressedKeys.add(key)
        invalidate()
        utils!!.playMusic(key)
        pianoListener?.onPianoClick(key.type, key.group, key.index)
    }

    private fun handleBlackKeyDown(which: Int, event: MotionEvent, key: PianoKey) {
        key.keyDrawable.state = intArrayOf(android.R.attr.state_pressed)
        key.isPressed = true
        key.fingerID = event.getPointerId(which)
        pressedKeys.add(key)
        invalidate()
        utils!!.playMusic(key)
        pianoListener?.onPianoClick(key.type, key.group, key.index)
    }

    private fun handleMove(which: Int, event: MotionEvent) {
        val x = event.getX(which).toInt() + this.scrollX
        val y = event.getY(which).toInt()
        for (key in pressedKeys) {
            if (key.fingerID == event.getPointerId(which)) {
                if (!key.contains(x, y)) {
                    key.keyDrawable.state = intArrayOf(-android.R.attr.state_pressed)
                    invalidate()
                    key.isPressed = false
                    key.resetFingerID()
                    pressedKeys.remove(key)
                }
            }
        }
    }

    private fun handlePointerUp(pointerId: Int) {
        for (key in pressedKeys) {
            if (key.fingerID == pointerId) {
                key.isPressed = false
                key.resetFingerID()
                key.keyDrawable.state = intArrayOf(-android.R.attr.state_pressed)
                invalidate()
                pressedKeys.remove(key)
                break
            }
        }
    }

    private fun handleUp() {
        if (pressedKeys.size > 0) {
            for (key in pressedKeys) {
                key.keyDrawable.state = intArrayOf(-android.R.attr.state_pressed)
                key.isPressed = false
                invalidate()
            }
            pressedKeys.clear()
        }
    }

    fun destroy() {
        if (utils != null) {
            utils!!.stop()
        }
    }

    val pianoWidth: Int
        get() = piano?.pianoWith ?: 0

    @Suppress("unused")
    fun setPianoColors(pianoColors: IntArray) {
        if (pianoColors.size == 9) {
            this.pianoColors = pianoColors
        }
    }

    @Suppress("unused")
    fun setCanPress(canPress: Boolean) {
        this.canPress = canPress
    }

    fun scroll(progress: Int) {
        val x: Int = when (progress) {
            0 -> 0
            100 -> pianoWidth - layoutWidth
            else -> (progress.toFloat() / 100f * (pianoWidth - layoutWidth).toFloat()).toInt()
        }
        scrollTo(x, 0)
        this.progress = progress
    }

    fun setSoundPollMaxStream(maxStream: Int) {
        this.maxStream = maxStream
    }

    fun setPianoListener(pianoListener: OnPianoListener) {
        this.pianoListener = pianoListener
    }

    fun setLoadAudioListener(loadAudioListener: OnLoadAudioListener) {
        this.loadAudioListener = loadAudioListener
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(state)
        postDelayed({ scroll(progress) }, 200)
    }

    companion object {
        private const val TAG = "PianoView"
    }
}