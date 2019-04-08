package de.mario222k.wavedrawable

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable


class WaveDrawable : Drawable() {

    private var height = 0f
    private var width = 0f
    private var animator: AnimatorSet? = null

    private val waves = mutableListOf<Wave>()

    override fun draw(canvas: Canvas) {
        waves.forEach {
            it.path.moveTo(0f, height)
            it.path.lineTo(0f, it.y)

            val steps = (width * it.samples).toInt()
            for (i in 0 until (width + steps).toInt() step steps) {
                val x = i.toFloat()
                val sin = it.amplitude * Math.sin((i + it.x) * Math.PI / it.frequency)
                val y = it.y + sin.toFloat()
                it.path.lineTo(x, y)
            }
            it.path.lineTo(width, height)
            canvas.drawPath(it.path, it.paint)
        }
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        width = bounds?.width()?.toFloat() ?: 0f
        height = bounds?.height()?.toFloat() ?: 0f
    }

    override fun setAlpha(alpha: Int) {
        waves.forEach {
            it.paint.alpha = alpha
        }
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        waves.forEach {
            it.paint.colorFilter = colorFilter
        }
    }

    fun speed(speed: Float, set: AnimatorSet? = null) {
        (set ?: animator)?.childAnimations?.forEach {
            if (it is AnimatorSet) {
                speed(speed, it)
                return@forEach
            }

            if (it.duration >= 0) {
                it.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationCancel(animation: Animator?) {
                        super.onAnimationCancel(animation)
                        it.duration = (it.duration / speed).toLong()
                        it.removeListener(this)
                    }

                    override fun onAnimationRepeat(animation: Animator?) {
                        super.onAnimationRepeat(animation)
                        it.duration = (it.duration / speed).toLong()
                        it.removeListener(this)
                    }
                })
            }
        }
    }

    fun start(animations: List<WaveAnimator>) {
        val set = mutableListOf<Animator>()
        animations.forEach {
            waves.add(it.wave)
            it.onUpdate { wave ->
                wave.path.reset()
                invalidateSelf()
            }
            set.add(it.build())
        }
        this.animator?.cancel()
        this.animator = AnimatorSet().apply {
            playTogether(set)
            start()
        }
    }
}