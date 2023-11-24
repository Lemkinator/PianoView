package com.chengtao.sample

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.chengtao.pianoview.entity.Piano.PianoKeyType
import com.chengtao.pianoview.listener.OnLoadAudioListener
import com.chengtao.pianoview.listener.OnPianoListener
import com.chengtao.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var scrollProgress = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        binding.pianoView.setSoundPollMaxStream(10)
        binding.pianoSeekbar.thumbOffset = -12 * (resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
        binding.pianoView.setPianoListener(object : OnPianoListener {
            override fun onPianoInitFinish() {
                Log.d("test", "onPianoInitFinish")
            }

            override fun onPianoClick(type: PianoKeyType, group: Int, positionOfGroup: Int) {
                Log.d("test", "type:$type,group:$group,positionOfGroup:$positionOfGroup")
            }
        })
        binding.pianoView.setLoadAudioListener(object : OnLoadAudioListener {
            override fun loadPianoAudioStart() {
                Log.d("test", "loadPianoMusicStart")
            }

            override fun loadPianoAudioFinish() {
                Log.d("test", "loadPianoMusicFinish")
            }

            override fun loadPianoAudioError(e: Exception) {
                Log.d("test", "loadPianoMusicError")
            }

            override fun loadPianoAudioProgress(progress: Int) {
                Log.d("test", "loadPianoMusicProgress:$progress")
            }
        })
        binding.pianoSeekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                binding.pianoView.scroll(progress)
            }
        })
        binding.leftArrowButton.setOnClickListener {
            scrollProgress = binding.pianoView.layoutWidth * 75 / binding.pianoView.pianoWidth
            binding.pianoSeekbar.progress = if (scrollProgress == 0) 0
            else (binding.pianoSeekbar.progress - scrollProgress).coerceAtLeast(0)
        }
        binding.rightArrowButton.setOnClickListener {
            scrollProgress = binding.pianoView.layoutWidth * 75 / binding.pianoView.pianoWidth
            binding.pianoSeekbar.progress = if (scrollProgress == 0) 100
            else (binding.pianoSeekbar.progress + scrollProgress).coerceAtMost(100)
        }
        binding.closeButton.setOnClickListener {
            finishAfterTransition()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.pianoView.destroy()
    }
}