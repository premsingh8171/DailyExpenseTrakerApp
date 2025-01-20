package com.example.daily_expense_tracker_app.custom_component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.min


class CircularImageView : AppCompatImageView {
    constructor(context: Context?) : super(context!!)

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    )

    override fun onDraw(canvas: Canvas) {
        val path: Path = Path()
        val radius = (min(width.toDouble(), height.toDouble()) / 2.0f).toFloat()
        path.addCircle(width / 2.0f, height / 2.0f, radius, Path.Direction.CCW)
        canvas.clipPath(path)
        super.onDraw(canvas)
    }
}
