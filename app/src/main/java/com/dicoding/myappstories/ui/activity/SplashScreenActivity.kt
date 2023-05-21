package com.dicoding.myappstories.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.dicoding.myappstories.R
import com.dicoding.myappstories.databinding.ActivitySplashscreenBinding
import com.dicoding.myappstories.utilities.ConstValue
import com.dicoding.myappstories.utilities.SessionManager
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private var _binding: ActivitySplashscreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var pref: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        initUserDirection()

    }

    private fun initUserDirection() {
        pref = SessionManager(this)
        val isLogin = pref.isLogin
        Handler(Looper.getMainLooper()).postDelayed({
            when {
                isLogin -> {
                    val imageLogo = findViewById<ImageView>(R.id.imgLogo)
                    imageLogo.alpha = 1f
                    imageLogo.animate().setDuration(1000).alpha(0f).withEndAction {
                        val intentSplash = Intent(this, MainActivity::class.java)
                        startActivity(intentSplash)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finish()
                    }
                }

                else -> {
                    val imageLogo = findViewById<ImageView>(R.id.imgLogo)
                    imageLogo.alpha = 1f
                    imageLogo.animate().setDuration(1000).alpha(0f).withEndAction {
                        val intentSplash = Intent(this, AuthActivity::class.java)
                        startActivity(intentSplash)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finish()
                    }
                }
            }
        }, ConstValue.LOADING_TIME)
    }
}