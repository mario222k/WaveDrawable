package de.mario222k.wavedrawable

import android.graphics.Paint
import android.graphics.Path
import androidx.annotation.ColorInt


class Wave(
    @ColorInt color: Int,
    var amplitude: Float,
    var frequency: Float,
    var x: Float,
    var y: Float,
    var samples: Float
) {

    fun setColor(@ColorInt color: Int) {
        paint.color = color
    }

    fun getColor() = paint.color

    val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        this.color = color
    }

    val path = Path()
}