package com.chengtao.pianoview.entity;

import com.google.gson.annotations.SerializedName;


public class AutoPlayEntity {

  private Piano.PianoKeyType type;
  private int group;

  private int position;

  @SerializedName("break") private long currentBreakTime;

  public AutoPlayEntity() {
  }

  public AutoPlayEntity(Piano.PianoKeyType type, int group, int position, long currentBreakTime) {
    this.type = type;
    this.group = group;
    this.position = position;
    this.currentBreakTime = currentBreakTime;
  }

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

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public long getCurrentBreakTime() {
    return currentBreakTime;
  }

  public void setCurrentBreakTime(long currentBreakTime) {
    this.currentBreakTime = currentBreakTime;
  }

  @Override public String toString() {
    return "AutoPlayEntity{"
        + "type="
        + type
        + ", group="
        + group
        + ", position="
        + position
        + ", currentBreakTime="
        + currentBreakTime
        + '}';
  }
}
