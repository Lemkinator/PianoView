package com.chengtao.pianoview.listener;

public interface OnLoadAudioListener {

  void loadPianoAudioStart();

  void loadPianoAudioFinish();


  void loadPianoAudioError(Exception e);

  void loadPianoAudioProgress(int progress);
}
