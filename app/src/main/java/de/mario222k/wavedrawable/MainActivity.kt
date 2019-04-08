package de.mario222k.wavedrawable

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private var disposable: Disposable? = null

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
        disposable = Observable.timer(5, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.trampoline())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                (waveview.background as? WaveDrawable)?.speed(6f)
            }
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

    private fun getAnimations(view: View): List<WaveAnimator> {
        val colors = arrayOf(
            Color.parseColor("#A0FFFFFF"),
            Color.parseColor("#EEFFFFFF")
        )
        val waves = listOf(
            Wave(colors[0], 20f, 500f, 0f, view.height * 0.5f, 0.1f).apply {
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
            Wave(colors[1], 60f, 400f, 0f, view.height * 0.7f, 0.08f).apply {
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

        return listOf(
            waves[0].animate()
                .x(6000L, 0, LinearInterpolator(), 0f, (waves[0].frequency * 2))
                .y(
                    24000L, 0, AccelerateDecelerateInterpolator(),
                    waves[0].y, waves[0].y * 0.8f, waves[0].y * 1.3f, waves[0].y
                )
                .amplitude(7000L, 0, LinearInterpolator(), 20f, 80f, 60f, 20f),
            waves[1].animate()
                .x(8000L, 0, LinearInterpolator(), 0f, (waves[1].frequency * 2))
                .y(
                    20000L, 0, AccelerateDecelerateInterpolator(),
                    waves[1].y, waves[1].y * 0.85f, waves[1].y * 1.25f, waves[1].y
                )
                .amplitude(9000L, 0, LinearInterpolator(), 10f, 50f, 10f)
        )
    }
}
