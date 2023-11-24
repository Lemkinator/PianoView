package com.chengtao.pianoview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.chengtao.pianoview.R;
import com.chengtao.pianoview.entity.Piano;
import com.chengtao.pianoview.entity.PianoKey;
import com.chengtao.pianoview.listener.OnLoadAudioListener;
import com.chengtao.pianoview.listener.OnPianoListener;
import com.chengtao.pianoview.utils.AudioUtils;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class PianoView extends View {
    private final static String TAG = "PianoView";
    private Piano piano = null;
    private ArrayList<PianoKey[]> whitePianoKeys;
    private ArrayList<PianoKey[]> blackPianoKeys;
    private CopyOnWriteArrayList<PianoKey> pressedKeys = new CopyOnWriteArrayList<>();
    private Paint paint;
    private RectF square;
    private int[] pianoColors = {
            getContext().getColor(R.color.piano_key_description_0),
            getContext().getColor(R.color.piano_key_description_1),
            getContext().getColor(R.color.piano_key_description_2),
            getContext().getColor(R.color.piano_key_description_3),
            getContext().getColor(R.color.piano_key_description_4),
            getContext().getColor(R.color.piano_key_description_5),
            getContext().getColor(R.color.piano_key_description_6),
            getContext().getColor(R.color.piano_key_description_7),
            getContext().getColor(R.color.piano_key_description_8),
    };
    private AudioUtils utils = null;
    private Context context;
    private int layoutWidth = 0;
    private float scale = 1;
    private OnLoadAudioListener loadAudioListener;
    private OnPianoListener pianoListener;
    private int progress = 0;
    private boolean canPress = true;
    private boolean isInitFinish = false;
    private int maxStream;

    public PianoView(Context context) {
        this(context, null);
    }

    public PianoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PianoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        square = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e(TAG, "onMeasure");
        Drawable whiteKeyDrawable = ContextCompat.getDrawable(context, R.drawable.white_piano_key);
        int whiteKeyHeight = whiteKeyDrawable.getIntrinsicHeight();
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        switch (heightMode) {
            case MeasureSpec.AT_MOST:
                height = Math.min(height, whiteKeyHeight);
                break;
            case MeasureSpec.UNSPECIFIED:
                height = whiteKeyHeight;
                break;
            default:
                break;
        }
        scale = (float) (height - getPaddingTop() - getPaddingBottom()) / (float) (whiteKeyHeight);
        layoutWidth = width - getPaddingLeft() - getPaddingRight();
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if (piano == null) {
            piano = new Piano(context, scale);
            whitePianoKeys = piano.getWhitePianoKeys();
            blackPianoKeys = piano.getBlackPianoKeys();
            if (utils == null) {
                if (maxStream > 0) {
                    utils = AudioUtils.getInstance(getContext(), loadAudioListener, maxStream);
                } else {
                    utils = AudioUtils.getInstance(getContext(), loadAudioListener);
                }
                try {
                    utils.loadMusic(piano);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            scroll(50);
        }
        if (whitePianoKeys != null) {
            for (int i = 0; i < whitePianoKeys.size(); i++) {
                for (PianoKey key : whitePianoKeys.get(i)) {
                    paint.setColor(pianoColors[i]);
                    key.getKeyDrawable().draw(canvas);
                    Rect r = key.getKeyDrawable().getBounds();
                    int sideLength = (r.right - r.left) / 2;
                    int left = r.left + sideLength / 2;
                    int top = r.bottom - sideLength - sideLength / 3;
                    int right = r.right - sideLength / 2;
                    int bottom = r.bottom - sideLength / 3;
                    square.set(left, top, right, bottom);
                    canvas.drawRoundRect(square, 25f, 25f, paint);
                    paint.setColor(Color.BLACK);
                    paint.setTextSize(sideLength / 1.8f);
                    Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
                    int baseline =
                            (int) ((square.bottom + square.top - fontMetrics.bottom - fontMetrics.top) / 2);
                    paint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText(key.getLetterName(), square.centerX(), baseline, paint);
                }
            }
        }
        if (blackPianoKeys != null) {
            for (int i = 0; i < blackPianoKeys.size(); i++) {
                for (PianoKey key : blackPianoKeys.get(i)) {
                    key.getKeyDrawable().draw(canvas);
                }
            }
        }
        if (!isInitFinish && piano != null && pianoListener != null) {
            isInitFinish = true;
            pianoListener.onPianoInitFinish();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if (!canPress) {
            return false;
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                handleDown(event.getActionIndex(), event);
                break;
            case MotionEvent.ACTION_MOVE:
                for (int i = 0; i < event.getPointerCount(); i++) {
                    handleMove(i, event);
                }
                for (int i = 0; i < event.getPointerCount(); i++) {
                    handleDown(i, event);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                handlePointerUp(event.getPointerId(event.getActionIndex()));
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handleUp();
                return false;
            default:
                break;
        }
        return true;
    }

    private void handleDown(int which, MotionEvent event) {
        int x = (int) event.getX(which) + this.getScrollX();
        int y = (int) event.getY(which);
        for (int i = 0; i < whitePianoKeys.size(); i++) {
            for (PianoKey key : whitePianoKeys.get(i)) {
                if (!key.isPressed() && key.contains(x, y)) {
                    handleWhiteKeyDown(which, event, key);
                }
            }
        }
        for (int i = 0; i < blackPianoKeys.size(); i++) {
            for (PianoKey key : blackPianoKeys.get(i)) {
                if (!key.isPressed() && key.contains(x, y)) {
                    handleBlackKeyDown(which, event, key);
                }
            }
        }
    }

    private void handleWhiteKeyDown(int which, MotionEvent event, PianoKey key) {
        key.getKeyDrawable().setState(new int[]{android.R.attr.state_pressed});
        key.setPressed(true);
        if (event != null) {
            key.setFingerID(event.getPointerId(which));
        }
        pressedKeys.add(key);
        invalidate(key.getKeyDrawable().getBounds());
        utils.playMusic(key);
        if (pianoListener != null) {
            pianoListener.onPianoClick(key.getType(), key.getGroup(),
                    key.getIndex());
        }
    }

    private void handleBlackKeyDown(int which, MotionEvent event, PianoKey key) {
        key.getKeyDrawable().setState(new int[]{android.R.attr.state_pressed});
        key.setPressed(true);
        if (event != null) {
            key.setFingerID(event.getPointerId(which));
        }
        pressedKeys.add(key);
        invalidate(key.getKeyDrawable().getBounds());
        utils.playMusic(key);
        if (pianoListener != null) {
            pianoListener.onPianoClick(key.getType(), key.getGroup(),
                    key.getIndex());
        }
    }

    private void handleMove(int which, MotionEvent event) {
        int x = (int) event.getX(which) + this.getScrollX();
        int y = (int) event.getY(which);
        for (PianoKey key : pressedKeys) {
            if (key.getFingerID() == event.getPointerId(which)) {
                if (!key.contains(x, y)) {
                    key.getKeyDrawable().setState(new int[]{-android.R.attr.state_pressed});
                    invalidate(key.getKeyDrawable().getBounds());
                    key.setPressed(false);
                    key.resetFingerID();
                    pressedKeys.remove(key);
                }
            }
        }
    }

    private void handlePointerUp(int pointerId) {
        for (PianoKey key : pressedKeys) {
            if (key.getFingerID() == pointerId) {
                key.setPressed(false);
                key.resetFingerID();
                key.getKeyDrawable().setState(new int[]{-android.R.attr.state_pressed});
                invalidate(key.getKeyDrawable().getBounds());
                pressedKeys.remove(key);
                break;
            }
        }
    }

    private void handleUp() {
        if (pressedKeys.size() > 0) {
            for (PianoKey key : pressedKeys) {
                key.getKeyDrawable().setState(new int[]{-android.R.attr.state_pressed});
                key.setPressed(false);
                invalidate(key.getKeyDrawable().getBounds());
            }
            pressedKeys.clear();
        }
    }

    public void destroy() {
        if (utils != null) {
            utils.stop();
        }
    }

    public int getPianoWidth() {
        if (piano != null) {
            return piano.getPianoWith();
        }
        return 0;
    }

    public int getLayoutWidth() {
        return layoutWidth;
    }

    public void setPianoColors(int[] pianoColors) {
        if (pianoColors.length == 9) {
            this.pianoColors = pianoColors;
        }
    }

    public void setCanPress(boolean canPress) {
        this.canPress = canPress;
    }

    public void scroll(int progress) {
        int x;
        switch (progress) {
            case 0:
                x = 0;
                break;
            case 100:
                x = getPianoWidth() - getLayoutWidth();
                break;
            default:
                x = (int) (((float) progress / 100f) * (float) (getPianoWidth() - getLayoutWidth()));
                break;
        }
        this.scrollTo(x, 0);
        this.progress = progress;
    }

    public void setSoundPollMaxStream(int maxStream) {
        this.maxStream = maxStream;
    }

    public void setPianoListener(OnPianoListener pianoListener) {
        this.pianoListener = pianoListener;
    }

    public void setLoadAudioListener(OnLoadAudioListener loadAudioListener) {
        this.loadAudioListener = loadAudioListener;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        postDelayed(() -> scroll(progress), 200);
    }
}
