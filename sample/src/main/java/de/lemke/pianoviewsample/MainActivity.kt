package de.lemke.pianoviewsample

import android.R.anim.fade_in
import android.R.anim.fade_out
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import de.lemke.pianoview.entity.PianoKeyType
import de.lemke.pianoview.listener.OnLoadAudioListener
import de.lemke.pianoview.listener.OnPianoListener
import de.lemke.pianoviewsample.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var time: Long = 0
    private var isUIReady = false
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !isUIReady }
        time = System.currentTimeMillis()
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 34) overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, fade_in, fade_out)
        if (Build.VERSION.SDK_INT >= 28) window.attributes.layoutInDisplayCutoutMode = LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.pianoControlLayout) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.displayCutout())
            v.updatePadding(left = bars.left, right = bars.right, top = 0, bottom = 0)
            WindowInsetsCompat.CONSUMED
        }
        WindowCompat.getInsetsController(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
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
            }

            override fun loadPianoAudioFinish() {
                Log.d("ActivityMain", "loadPianoAudioFinish")
                lifecycleScope.launch {
                    //manually waiting for the animation to finish :/
                    delay(650 - (System.currentTimeMillis() - time).coerceAtLeast(0L))
                    isUIReady = true
                }
            }

            override fun loadPianoAudioError(e: Exception) {
                Log.e("ActivityMain", "loadPianoAudioError: $e")
            }

            override fun loadPianoAudioProgress(progress: Int) {
                Log.d("ActivityMain", "loadPianoAudioProgress: $progress")
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