package com.example.nothingcinema

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class DonutChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    data class ChartItem(
        val label: String,
        val value: Int,
        val color: Int
    )

    private val items = mutableListOf<ChartItem>()

    private val slicePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 80f
        strokeCap = Paint.Cap.BUTT
    }

    private val centerTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
        textSize = 42f
        typeface = Typeface.DEFAULT_BOLD
    }

    private val subTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.DKGRAY
        textAlign = Paint.Align.CENTER
        textSize = 28f
    }

    fun setChartData(chartItems: List<ChartItem>) {
        items.clear()
        items.addAll(chartItems)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (items.isEmpty()) {
            canvas.drawText("No Data", width / 2f, height / 2f, centerTextPaint)
            return
        }

        val total = items.sumOf { it.value }
        if (total <= 0) {
            canvas.drawText("No Data", width / 2f, height / 2f, centerTextPaint)
            return
        }

        val size = min(width, height).toFloat()
        val padding = 40f
        val radius = size - padding * 2
        val left = (width - radius) / 2f
        val top = (height - radius) / 2f
        val right = left + radius
        val bottom = top + radius

        val rect = RectF(left, top, right, bottom)

        var startAngle = -90f

        for (item in items) {
            val sweepAngle = (item.value.toFloat() / total.toFloat()) * 360f
            slicePaint.color = item.color
            canvas.drawArc(rect, startAngle, sweepAngle, false, slicePaint)
            startAngle += sweepAngle
        }

        canvas.drawText(total.toString(), width / 2f, height / 2f - 10f, centerTextPaint)
        canvas.drawText("Total", width / 2f, height / 2f + 35f, subTextPaint)
    }
}