package com.chengtao.pianoview.entity;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class PianoKey {
    private Piano.PianoKeyType type;
    private int group;
    private int index;
    private Drawable keyDrawable;
    private int voiceId;
    private boolean isPressed;
    private Rect[] areaOfKey;
    private String letterName;
    private int fingerID = -1;

    public Piano.PianoKeyType getType() {
        return type;
    }

    public void setType(Piano.PianoKeyType type) {
        this.type = type;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Drawable getKeyDrawable() {
        return keyDrawable;
    }

    public void setKeyDrawable(Drawable keyDrawable) {
        this.keyDrawable = keyDrawable;
    }

    public int getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(int voiceId) {
        this.voiceId = voiceId;
    }

    public boolean isPressed() {
        return isPressed;
    }

    public void setPressed(boolean pressed) {
        isPressed = pressed;
    }

    public Rect[] getAreaOfKey() {
        return areaOfKey;
    }

    public void setAreaOfKey(Rect[] areaOfKey) {
        this.areaOfKey = areaOfKey;
    }

    public String getLetterName() {
        return letterName;
    }

    public void setLetterName(String letterName) {
        this.letterName = letterName;
    }

    public boolean contains(int x, int y) {
        boolean isContain = false;
        Rect[] areas = getAreaOfKey();
        int length = getAreaOfKey().length;
        for (int i = 0; i < length; i++) {
            if (areas[i] != null && areas[i].contains(x, y)) {
                isContain = true;
                break;
            }
        }
        return isContain;
    }

    public void resetFingerID() {
        fingerID = -1;
    }

    public void setFingerID(int fingerIndex) {
        this.fingerID = fingerIndex;
    }

    public int getFingerID() {
        return fingerID;
    }
}