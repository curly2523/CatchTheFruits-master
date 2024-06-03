package com.halil.ozel.catchthefruits

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.halil.ozel.catchthefruits.databinding.ActivityMainBinding
import java.util.Random


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var score = 0
    private val imageArray = ArrayList<ImageView>()
    private val handler = android.os.Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    private val HIGH_SCORE_KEY = "highestScore"
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.catchFruits = this

        // Set initial score to 0
        binding.score = getString(R.string.score_0)
        score = 0

        // Initialize imageArray
        imageArray.addAll(
            listOf(
                binding.ivApple, binding.ivBanana, binding.ivCherry,
                binding.ivGrapes, binding.ivKiwi, binding.ivOrange,
                binding.ivPear, binding.ivStrawberry, binding.ivWatermelon
            )
        )

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("highScore", MODE_PRIVATE)

        // Retrieve and display the highest score
        val highestScore = getHighestScore()
        val textView2 = findViewById<TextView>(R.id.textView2)
        textView2.text = getString(R.string.highest_score, highestScore)

        // Start the game
        hideImages()
        playAndRestart()
    }

    fun getHighestScore(): Int {
        return sharedPreferences?.getInt(HIGH_SCORE_KEY, 0) ?: 0
    }


    private fun hideImages() {
        runnable = Runnable {
            imageArray.forEach { it.visibility = View.INVISIBLE }
            imageArray[Random().nextInt(8)].visibility = View.VISIBLE
            handler.postDelayed(runnable, 1000)
        }
        handler.post(runnable)
    }

    @SuppressLint("SetTextI18n")
    fun increaseScore() {
        binding.score = "Score : " + (++score)
    }

    @SuppressLint("SetTextI18n")
    fun playAndRestart() {
        score = 0
        binding.score = "Score : 0"
        hideImages()
        binding.time = "Time : 10"
        imageArray.forEach { it.visibility = View.INVISIBLE }

        object : CountDownTimer(10000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                binding.time = getString(R.string.time_up)
                handler.removeCallbacks(runnable)

                val prefs = sharedPreferences // Use a local variable
                if (score > prefs?.getInt(HIGH_SCORE_KEY, 0) ?: 0) {
                    val editor = prefs?.edit()
                    editor?.putInt(HIGH_SCORE_KEY, score)
                    editor?.apply()
                }

                AlertDialog.Builder(this@MainActivity).apply {
                    setCancelable(false)
                    setTitle(getString(R.string.game_name))
                    setMessage("Your score : $score\nWould you like to play again?")
                    setPositiveButton(getString(R.string.yes)) { _, _ -> playAndRestart() }
                    setNegativeButton(getString(R.string.no)) { _, _ ->
                        score = 0
                        binding.score = "Score : 0"
                        binding.time = "Time : 0"
                        imageArray.forEach { it.visibility = View.INVISIBLE }
                        finish()
                    }
                }.create().show()
            }

            @SuppressLint("SetTextI18n")
            override fun onTick(tick: Long) {
                binding.time = "Time : " + tick / 1000
            }
        }.start()
    }


}