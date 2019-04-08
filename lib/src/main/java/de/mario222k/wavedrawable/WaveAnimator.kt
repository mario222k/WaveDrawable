package de.mario222k.wavedrawable

import android.animation.*

fun Wave.animate() = WaveAnimator(this)

class WaveAnimator(val wave: Wave) {

    private var xAnimator: Animator? = null
    private var yAnimator: Animator? = null
    private var frequencyAnimator: Animator? = null
    private var amplitudeAnimator: Animator? = null
    private var samplesAnimator: Animator? = null
    private var colorAnimator: Animator? = null
    private var updateAnimator: Animator? = null

    fun x(
        duration: Long,
        delay: Long,
        interpolator: TimeInterpolator?,
        vararg values: Float
    ): WaveAnimator {
        xAnimator =  getAnimator("x", duration, delay, interpolator, *values)
        return this
    }

    fun y(
        duration: Long,
        delay: Long,
        interpolator: TimeInterpolator?,
        vararg values: Float
    ): WaveAnimator {
        yAnimator = getAnimator("y", duration, delay, interpolator, *values)
        return this
    }

    fun amplitude(
        duration: Long,
        delay: Long,
        interpolator: TimeInterpolator?,
        vararg values: Float
    ): WaveAnimator {
        amplitudeAnimator = getAnimator("amplitude", duration, delay, interpolator, *values)
        return this
    }

    fun frequency(
        duration: Long,
        delay: Long,
        interpolator: TimeInterpolator?,
        vararg values: Float
    ): WaveAnimator {
        frequencyAnimator = getAnimator("frequency", duration, delay, interpolator, *values)
        return this
    }

    fun samples(
        duration: Long,
        delay: Long,
        interpolator: TimeInterpolator?,
        vararg values: Float
    ): WaveAnimator {
        samplesAnimator = getAnimator("samples", duration, delay, interpolator, *values)
        return this
    }

    fun color(
        duration: Long,
        delay: Long,
        interpolator: TimeInterpolator?,
        vararg values: Int
    ): WaveAnimator {
        colorAnimator = ObjectAnimator.ofArgb(wave, "color", *values).apply {
            setDuration(duration)
            startDelay = delay
            setInterpolator(interpolator)
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
        }
        return this
    }

    fun onUpdate(callback: (wave: Wave)->Unit): WaveAnimator {
        updateAnimator = ValueAnimator.ofInt(0, 100).apply {
            addUpdateListener { callback.invoke(wave) }
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
        }
        return this
    }

    fun build() = AnimatorSet().apply {
        val list = mutableListOf<Animator>()
        xAnimator?.let { list.add(it) }
        yAnimator?.let { list.add(it) }
        samplesAnimator?.let { list.add(it) }
        colorAnimator?.let { list.add(it) }
        amplitudeAnimator?.let { list.add(it) }
        frequencyAnimator?.let { list.add(it) }
        updateAnimator?.let { list.add(it) }
        playTogether(list)
    }

    private fun getAnimator(
        property: String,
        duration: Long,
        delay: Long,
        interpolator: TimeInterpolator?,
        vararg values: Float
    ) = ObjectAnimator.ofFloat(wave, property, *values).apply {
        setDuration(duration)
        startDelay = delay
        setInterpolator(interpolator)
        repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.RESTART
    }
}