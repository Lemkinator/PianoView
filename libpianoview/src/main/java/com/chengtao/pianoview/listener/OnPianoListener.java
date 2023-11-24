package com.chengtao.pianoview.listener;


import com.chengtao.pianoview.entity.Piano;

public interface OnPianoListener {

  void onPianoInitFinish();

  void onPianoClick(Piano.PianoKeyType type, Piano.PianoVoice voice, int group,
      int positionOfGroup);
}
