package de.lemke.pianoviewsample

import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import de.lemke.pianoview.entity.Piano.PianoKeyType
import de.lemke.pianoview.listener.OnLoadAudioListener
import de.lemke.pianoview.listener.OnPianoListener
import de.lemke.pianoviewsample.databinding.ActivityMainBinding
import dev.oneuiproject.oneui.dialog.ProgressDialog

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var scrollProgress = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, android.R.anim.fade_in, android.R.anim.fade_out)
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val dialog = ProgressDialog(this@MainActivity)
        dialog.setProgressStyle(ProgressDialog.STYLE_CIRCLE)
        dialog.setCancelable(false)
        dialog.show()
        binding.pianoView.setSoundPollMaxStream(10)
        binding.pianoSeekbar.thumbOffset = -12 * (resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
        binding.pianoView.setPianoListener(object : OnPianoListener {
            override fun onPianoClick(type: PianoKeyType, group: Int, index: Int) {}
            override fun onPianoInitFinish() {
                Log.d("MainActivity", "onPianoInitFinish")
            }
        })
        binding.pianoView.setLoadAudioListener(object : OnLoadAudioListener {
            override fun loadPianoAudioStart() {
                Log.d("ActivityMain", "loadPianoAudioStart")
                dialog.show()
            }

            override fun loadPianoAudioFinish() {
                Log.d("ActivityMain", "loadPianoAudioFinish")
                dialog.dismiss()
            }

            override fun loadPianoAudioError(e: Exception) {
                Log.e("ActivityMain", "loadPianoAudioError: $e")
            }

            override fun loadPianoAudioProgress(progress: Int) {
                Log.d("ActivityMain", "loadPianoAudioProgress: $progress")
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
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.pianoView.destroy()
    }
}