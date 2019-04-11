package de.mario222k.wavedrawable

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val MSG_SPEED_UP = 23
        private const val MSG_SLOW_DOWN = 24
        private const val MSG_RESET = 25
    }

    private val handler = MessageHandler().apply {
        speed.observe(this@MainActivity, Observer {
            (waveview.background as? WaveDrawable)?.speed(it)
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        waveview.post {
            waveview.background = WaveDrawable().apply {
                setBounds(0, 0, waveview.measuredWidth, waveview.measuredHeight)
                start(getAnimations(waveview))
            }
        }

        loading_button.setOnClickListener {
            it.isEnabled = false
            handler.sendEmptyMessage(MSG_SPEED_UP)

            handler.postDelayed({
                if(isFinishing) return@postDelayed

                it.isEnabled = true
                handler.sendEmptyMessage(MSG_SLOW_DOWN)
            }, 5000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeMessages(MSG_SLOW_DOWN)
        handler.removeMessages(MSG_SPEED_UP)
    }

    private fun getAnimations(view: View): List<WaveAnimator> {
        val colors = arrayOf(
            Color.parseColor("#A0FFFFFF"),
            Color.parseColor("#EEFFFFFF")
        )
        val waves = listOf(
            Wave(colors[0], 20f, 0.4f, 0f, view.height * 0.6f, 40).apply {
                paint.shader = LinearGradient(
                    0f,
                    0f,
                    0f,
                    view.height.toFloat(),
                    colors[0],
                    Color.TRANSPARENT,
                    Shader.TileMode.CLAMP
                )
            },
            Wave(colors[1], 40f, 0.25f, 0f, view.height * 0.8f, 80).apply {
                paint.shader = LinearGradient(
                    0f,
                    0f,
                    0f,
                    view.height.toFloat(),
                    colors[1],
                    Color.TRANSPARENT,
                    Shader.TileMode.CLAMP
                )
            }
        )

        val phase = 2f * Math.PI.toFloat()
        return listOf(
            waves[0].animate()
                .x(6000L, 0, LinearInterpolator(), 0f, phase)
                .y(
                    24000L, 0, AccelerateDecelerateInterpolator(),
                    waves[0].y, waves[0].y * 0.8f, waves[0].y * 1.3f, waves[0].y
                )
                .amplitude(9000L, 0, LinearInterpolator(), waves[0].amplitude, 50f, waves[0].amplitude),

            waves[1].animate()
                .x(8000L, 0, LinearInterpolator(), 0f, phase)
                .y(
                    20000L, 0, AccelerateDecelerateInterpolator(),
                    waves[1].y, waves[1].y * 1.2f, waves[1].y * 0.8f, waves[1].y
                )
                .amplitude(7000L, 0, LinearInterpolator(), waves[1].amplitude, 80f, 60f, waves[1].amplitude)
        )
    }

    class MessageHandler : Handler() {
        val speed = MutableLiveData<Float>()

        init {
            speed.postValue(1f)
        }

        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                MSG_SPEED_UP -> speed.postValue(6f)
                MSG_SLOW_DOWN -> speed.postValue(1f/6f)
                MSG_RESET -> speed.postValue(1f)
                else -> super.handleMessage(msg)
            }
        }
    }
}
