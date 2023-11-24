package com.chengtao.sample

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.chengtao.pianoview.entity.AutoPlayEntity
import com.chengtao.pianoview.entity.Piano.PianoKeyType
import com.chengtao.pianoview.entity.Piano.PianoVoice
import com.chengtao.pianoview.listener.OnLoadAudioListener
import com.chengtao.pianoview.listener.OnPianoAutoPlayListener
import com.chengtao.pianoview.listener.OnPianoListener
import com.chengtao.pianoview.utils.AutoPlayUtils
import com.chengtao.sample.databinding.ActivityMainBinding
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var litterStarList: MutableList<AutoPlayEntity>
    private var scrollProgress = 0
    private var isPlaying = false

    //flight_of_the_bumble_bee,simple_little_star_config
    private val configFilename = "simple_little_star_config"
    private val useConfigFile = true
    private val litterStarBreakShortTime: Long = 500
    private val litterStarBreakLongTime: Long = 1000
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

            override fun onPianoClick(type: PianoKeyType, voice: PianoVoice, group: Int, positionOfGroup: Int) {
                Log.d("test", "type:$type,voice:$voice,group:$group,positionOfGroup:$positionOfGroup")
            }
        })
        binding.pianoView.setAutoPlayListener(object : OnPianoAutoPlayListener {
            override fun onPianoAutoPlayStart() {
                Log.d("test", "onPianoAutoPlayStart")
            }

            override fun onPianoAutoPlayEnd() {
                Log.d("test", "onPianoAutoPlayEnd")
                isPlaying = false
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
        binding.musicButton.setOnClickListener { if (!isPlaying) binding.pianoView.autoPlay(litterStarList) }
        if (useConfigFile) {
            try {
                litterStarList = AutoPlayUtils.getAutoPlayEntityListByCustomConfigInputStream(assets.open(configFilename))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            initLitterStarList()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.pianoView.releaseAutoPlay()
    }

    private fun initLitterStarList() {
        litterStarList = mutableListOf()
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 0, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 0, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 4, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 4, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 5, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 5, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 4, litterStarBreakLongTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 3, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 3, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 2, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 2, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 1, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 1, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 0, litterStarBreakLongTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 4, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 4, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 3, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 3, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 2, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 2, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 1, litterStarBreakLongTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 4, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 4, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 3, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 3, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 2, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 2, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 1, litterStarBreakLongTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 0, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 0, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 4, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 4, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 5, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 5, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 4, litterStarBreakLongTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 3, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 3, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 2, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 2, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 1, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 1, litterStarBreakShortTime))
        litterStarList.add(AutoPlayEntity(PianoKeyType.WHITE, 4, 0, litterStarBreakLongTime))
    }
}