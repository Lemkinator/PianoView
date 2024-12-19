package de.lemke.pianoview.view

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.os.Parcelable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.content.res.AppCompatResources
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
    private var layoutWidth = 0
    private var scale = 0f
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
    var loadAudioListener: OnLoadAudioListener?
        get() = audioUtils.loadAudioListener
        set(value) {
            audioUtils.loadAudioListener = value
        }

    private var firstVisibleWhiteKeyIndex = 20  //G3, index from 0 to 51
        set(value) {
            field = value.coerceIn(0, 52 - visibleKeys)
        }

    var visibleKeys = 12
        set(value) {
            field = if (value == -1) 12
            else value.coerceIn(6, 30)
            if (field + firstVisibleWhiteKeyIndex > 52) {
                firstVisibleWhiteKeyIndex = 52 - field
            }
            invalidate()
            piano.setDrawableAndBounds(context, scale, layoutWidth, field)
            setSeekBarThumbWidth()
            scrollToWhiteKey(firstVisibleWhiteKeyIndex)
        }

    var seekBar: SeekBar? = null
        set(value) {
            field = value
            field?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    if (fromUser) seekbarScroll(progress)
                }
            })
            field?.background = AppCompatResources.getDrawable(context, R.drawable.piano_bar)
            field?.progressDrawable = null
        }

    private fun setSeekBarThumbWidth() {
        seekBar?.let {
            val thumbOffset = 21 * (resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
            val bitmap =
                Bitmap.createBitmap(
                    (it.measuredWidth / 52 * visibleKeys).coerceAtLeast(1),
                    it.measuredHeight.coerceAtLeast(1),
                    Bitmap.Config.ARGB_8888
                )
            val canvas = Canvas(bitmap)
            val drawable = AppCompatResources.getDrawable(context, R.drawable.seekbar_thumb)
            drawable!!.setBounds(0, 0, bitmap.width, bitmap.height)
            drawable.draw(canvas)
            it.thumb = BitmapDrawable(resources, bitmap)
            it.thumbOffset = thumbOffset
        }
    }

    /**
     * Note: Setting this, will destroy the current [AudioUtils] instance and create a new one, so audio will be reloaded.
     */
    @Suppress("unused")
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
        val whiteKeyDrawable = AppCompatResources.getDrawable(context, R.drawable.white_piano_key)
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
            piano.setDrawableAndBounds(context, scale, layoutWidth, visibleKeys)
            setSeekBarThumbWidth()
            scrollToWhiteKey(firstVisibleWhiteKeyIndex)
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

    @Suppress("unused")
    fun setPianoColors(vararg pianoColors: Int) {
        if (pianoColors.size == 9) {
            this.pianoColors = pianoColors
        }
    }

    fun goToNextWhiteKey() {
        firstVisibleWhiteKeyIndex++
        smoothScrollToWhiteKey(firstVisibleWhiteKeyIndex)
    }

    fun goToPreviousWhiteKey() {
        firstVisibleWhiteKeyIndex--
        smoothScrollToWhiteKey(firstVisibleWhiteKeyIndex)
    }

    fun scrollLeft() {
        firstVisibleWhiteKeyIndex -= 7
        smoothScrollToWhiteKey(firstVisibleWhiteKeyIndex)
    }

    fun scrollRight() {
        firstVisibleWhiteKeyIndex += 7
        smoothScrollToWhiteKey(firstVisibleWhiteKeyIndex)
    }

    fun seekbarScroll(progress: Int) {
        val x: Int = when (progress) {
            0 -> 0
            100 -> piano.pianoWith - layoutWidth
            else -> (progress.toFloat() / 100f * (piano.pianoWith - layoutWidth).toFloat()).toInt()
        }
        scrollTo(x, 0)
        firstVisibleWhiteKeyIndex = (x.toFloat() / piano.whiteKeyWidth.toFloat()).toInt()
    }

    private fun scrollToWhiteKey(index: Int) {
        val x = index * piano.whiteKeyWidth
        scrollTo(x, 0)
        seekBar?.progress = (x.toFloat() / (piano.pianoWith - layoutWidth).toFloat() * 100f).toInt()
    }

    private fun smoothScrollToWhiteKey(index: Int) {
        val x = index * piano.whiteKeyWidth
        ObjectAnimator.ofInt(this, "scrollX", scrollX, x).setDuration(200).start()
        seekBar?.let {
            val newProgress = (x.toFloat() / (piano.pianoWith - layoutWidth).toFloat() * 100f).toInt()
            ObjectAnimator.ofInt(it, "progress", it.progress, newProgress).setDuration(200).start()
        }
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(state)
        postDelayed({ scrollToWhiteKey(firstVisibleWhiteKeyIndex) }, 200)
    }
}