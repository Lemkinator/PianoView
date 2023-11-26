package de.lemke.pianoviewsample

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import de.lemke.pianoview.entity.PianoKeyType
import de.lemke.pianoview.listener.OnLoadAudioListener
import de.lemke.pianoview.listener.OnPianoListener
import de.lemke.pianoviewsample.databinding.ActivityMainBinding
import dev.oneuiproject.oneui.dialog.ProgressDialog

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, android.R.anim.fade_in, android.R.anim.fade_out)
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        val progressDialog = ProgressDialog(this)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.setCancelable(false)
        progressDialog.max = 100
        progressDialog.setTitle(R.string.virtual_piano_loading_1)
        progressDialog.show()
        binding.pianoView.visibleKeys = 12
        binding.pianoView.audioMaxStreams = 10
        binding.pianoView.pianoListener = object : OnPianoListener {
            override fun onPianoClick(type: PianoKeyType, group: Int, indexInGroup: Int) {
                //Log.d("MainActivity", "onPianoClick: $type, $group, $indexInGroup")
            }
        }
        binding.pianoView.loadAudioListener = object : OnLoadAudioListener {
            override fun loadPianoAudioStart() {
                Log.d("ActivityMain", "loadPianoAudioStart")
                progressDialog.show()
            }

            override fun loadPianoAudioFinish() {
                Log.d("ActivityMain", "loadPianoAudioFinish")
                progressDialog.dismiss()
            }

            override fun loadPianoAudioError(e: Exception) {
                Log.e("ActivityMain", "loadPianoAudioError: $e")
            }

            override fun loadPianoAudioProgress(progress: Int) {
                Log.d("ActivityMain", "loadPianoAudioProgress: $progress")
                progressDialog.progress = progress
                when (progress) {
                    in 0..33 -> progressDialog.setTitle(R.string.virtual_piano_loading_1)
                    in 33..66 -> progressDialog.setTitle(R.string.virtual_piano_loading_2)
                    else -> progressDialog.setTitle(R.string.virtual_piano_loading_3)
                }
            }
        }
        binding.pianoView.seekBar = binding.pianoSeekbar
        binding.minusButton.setOnClickListener { binding.pianoView.visibleKeys-- }
        binding.scrollLeftArrowButton.setOnClickListener { binding.pianoView.scrollLeft() }
        binding.leftArrowButton.setOnClickListener { binding.pianoView.goToPreviousWhiteKey() }
        binding.rightArrowButton.setOnClickListener { binding.pianoView.goToNextWhiteKey() }
        binding.scrollRightArrowButton.setOnClickListener { binding.pianoView.scrollRight() }
        binding.plusButton.setOnClickListener { binding.pianoView.visibleKeys++ }
        binding.closeButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.pianoView.destroy()
    }
}