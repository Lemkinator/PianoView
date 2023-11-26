package de.lemke.pianoview.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import de.lemke.pianoview.R
import de.lemke.pianoview.entity.Piano
import de.lemke.pianoview.entity.PianoKey
import de.lemke.pianoview.entity.PianoKeyType
import de.lemke.pianoview.listener.OnLoadAudioListener
import de.lemke.pianoview.listener.OnPianoListener
import de.lemke.pianoview.utils.AudioUtils
import java.util.concurrent.CopyOnWriteArrayList

class PianoView @JvmOverloads constructor(private val context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(
    context, attrs, defStyleAttr
) {
    private var piano: Piano = Piano()
    private var audioUtils: AudioUtils = AudioUtils(context, piano.pianoKeys)
    private val pressedKeys: CopyOnWriteArrayList<PianoKey> = CopyOnWriteArrayList()
    private val paint: Paint = Paint()
    private val square: RectF = RectF()
    private var pianoColors = intArrayOf(
        context.getColor(R.color.piano_key_description_0),
        context.getColor(R.color.piano_key_description_1),
        context.getColor(R.color.piano_key_description_2),
        context.getColor(R.color.piano_key_description_3),
        context.getColor(R.color.piano_key_description_4),
        context.getColor(R.color.piano_key_description_5),
        context.getColor(R.color.piano_key_description_6),
        context.getColor(R.color.piano_key_description_7),
        context.getColor(R.color.piano_key_description_8)
    )
    var layoutWidth = 0
        private set
    private var scale = 0f
    private var scrollProgress = 0
    var loadAudioListener: OnLoadAudioListener?
        get() = audioUtils.loadAudioListener
        set(value) {
            audioUtils.loadAudioListener = value
        }

    /**
     * Note: Setting this, will destroy the current [AudioUtils] instance and create a new one, so audio will be reloaded.
     */
    var audioMaxStreams: Int?
        get() = audioUtils.maxStreams
        set(value) {
            audioUtils.destroy()
            audioUtils = AudioUtils(context, piano.pianoKeys, loadAudioListener, value)
        }

    var pianoListener: OnPianoListener? = null

    @Suppress("MemberVisibilityCanBePrivate")
    var canPress = true

    init {
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.d("PianoView", "onMeasure")
        val whiteKeyDrawable = context.getDrawable(R.drawable.white_piano_key)
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

    override fun onDraw(canvas: Canvas) {
        if (!piano.uiInitialized) {
            piano.setDrawableAndBounds(context, scale)
            scroll(50)
            pianoListener?.onPianoInitFinish()
        }
        piano.pianoKeys.forEach {
            it.keyDrawable!!.draw(canvas)
            if (it.type == PianoKeyType.WHITE) {
                paint.color = pianoColors[it.group]
                val r = it.keyDrawable!!.bounds
                val sideLength = (r.right - r.left) / 2
                val left = r.left + sideLength / 2
                val top = r.bottom - sideLength - sideLength / 3
                val right = r.right - sideLength / 2
                val bottom = r.bottom - sideLength / 3
                square[left.toFloat(), top.toFloat(), right.toFloat()] = bottom.toFloat()
                //canvas.drawRoundRect(square, 25f, 25f, paint)
                //paint.color = Color.BLACK
                paint.textSize = sideLength / 1.6f
                val fontMetrics = paint.fontMetricsInt
                val baseline = ((square.bottom + square.top - fontMetrics.bottom - fontMetrics.top) / 2).toInt()
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText(it.noteName.toString(), square.centerX(), baseline.toFloat(), paint)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!canPress) {
            return false
        }
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> handleDown(event.actionIndex, event)
            MotionEvent.ACTION_MOVE -> {
                for (i in 0 until event.pointerCount) {
                    handleMove(i, event)
                    handleDown(i, event)
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
        piano.pianoKeys.forEachIndexed { index, key ->
            if (!key.isPressed && key.contains(x, y)) {
                key.keyDrawable!!.state = intArrayOf(android.R.attr.state_pressed)
                key.isPressed = true
                key.fingerID = event.getPointerId(which)
                pressedKeys.add(key)
                invalidate()
                audioUtils.playKey(index)
                pianoListener?.onPianoClick(key.type, key.group, key.indexInGroup)
            }
        }
    }

    private fun handleMove(which: Int, event: MotionEvent) {
        val x = event.getX(which).toInt() + this.scrollX
        val y = event.getY(which).toInt()
        for (key in pressedKeys) {
            if (key.fingerID == event.getPointerId(which)) {
                if (!key.contains(x, y)) {
                    key.keyDrawable!!.state = intArrayOf(-android.R.attr.state_pressed)
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
                key.keyDrawable!!.state = intArrayOf(-android.R.attr.state_pressed)
                invalidate()
                pressedKeys.remove(key)
                break
            }
        }
    }

    private fun handleUp() {
        for (key in pressedKeys) {
            key.keyDrawable!!.state = intArrayOf(-android.R.attr.state_pressed)
            key.isPressed = false
            invalidate()
        }
        pressedKeys.clear()
    }

    fun destroy() {
        audioUtils.destroy()
    }

    val pianoWidth: Int
        get() = piano.pianoWith

    @Suppress("unused")
    fun setPianoColors(pianoColors: IntArray) {
        if (pianoColors.size == 9) {
            this.pianoColors = pianoColors
        }
    }

    fun scroll(progress: Int) {
        val x: Int = when (progress) {
            0 -> 0
            100 -> pianoWidth - layoutWidth
            else -> (progress.toFloat() / 100f * (pianoWidth - layoutWidth).toFloat()).toInt()
        }
        scrollTo(x, 0)
        this.scrollProgress = progress
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(state)
        postDelayed({ scroll(scrollProgress) }, 200)
    }
}