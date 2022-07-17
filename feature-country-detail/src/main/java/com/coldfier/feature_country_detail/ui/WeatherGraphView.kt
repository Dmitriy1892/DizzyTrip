package com.coldfier.feature_country_detail.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.coldfier.core_data.repository.models.Month
import com.coldfier.feature_country_detail.R
import kotlin.math.roundToInt
import kotlin.math.sqrt

class WeatherGraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val separatorPaint = Paint().apply {
        strokeWidth = resources.getDimension(R.dimen.stroke_width)
        color = ContextCompat.getColor(context, R.color.separator_color)
    }

    private val monthTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = resources.getDimension(R.dimen.month_text_size)
        color = ContextCompat.getColor(context, R.color.month_text_color)
    }

    private val temperatureTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = resources.getDimension(R.dimen.temperature_text_size)
        color = ContextCompat.getColor(context, R.color.month_text_color)
    }

    private val pointPaint = Paint().apply {
        strokeWidth = resources.getDimension(R.dimen.point_width)
        color = ContextCompat.getColor(context, R.color.point_color)
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private val graphLinePaint = Paint().apply {
        strokeWidth = resources.getDimension(R.dimen.line_width)
        color = ContextCompat.getColor(context, R.color.line_paint)
    }

    private val minRowHeight = resources.getDimension(R.dimen.min_row_height)

    private val rowRect = Rect()

    private var weatherMap = mapOf<Month, Double?>()

    private val gridXYValue = 12

    private val topPadding = resources.getDimension(R.dimen.top_padding)

    private val leftPadding = resources.getDimension(R.dimen.left_padding)

    private val monthNameHeight =
        monthTextPaint.measureText(Month.values()[0].name.take(3)) * sqrt(2.0).toFloat() / 2

    private val columnWidth
        get() = (width  - leftPadding.toInt() - monthNameHeight) / gridXYValue

    private val temperatureValuesCount
        get() = weatherMap.values.mapTo(HashSet()) { it?.toInt() }.size

    private val rowHeight
        get() = if (temperatureValuesCount > 0) {
            (height - topPadding - monthNameHeight) / temperatureValuesCount
        } else { height - topPadding - monthNameHeight }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val width = if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            (leftPadding + columnWidth * gridXYValue
                    + temperatureTextPaint.measureText("77 \u2103")).roundToInt()
        } else {
            MeasureSpec.getSize(widthMeasureSpec)
        }

        val minHeight = topPadding + monthNameHeight + minRowHeight * temperatureValuesCount

        val height = if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            minHeight.roundToInt()
        } else {
            val specifiedHeight = MeasureSpec.getSize(heightMeasureSpec)
            if (specifiedHeight < minHeight) {
                throw IllegalArgumentException(
                    "Weather graph height measured incorrect - change \"android:height\' " +
                            "attribute to \"wrap_content\" or increase value"
                )
            } else {
                specifiedHeight
            }
        }

        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        rowRect.set(0,0, w, minRowHeight.roundToInt())
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.apply {
            drawGrid()
            drawGraph()
        }
    }

    private fun Canvas.drawGrid() {
        val rowSeparatorXStart = columnWidth + leftPadding
        val rowSeparatorXStop = columnWidth * gridXYValue + leftPadding
        val columnSeparatorYStop = rowHeight * (temperatureValuesCount - 1) + topPadding
        val monthY = rowHeight * temperatureValuesCount + topPadding

        repeat(gridXYValue) { index ->
            // Horizontal lines drawing
            if (index <= temperatureValuesCount - 1) {
                val rowSeparatorY = rowHeight * index + topPadding
                drawLine(
                    rowSeparatorXStart, rowSeparatorY, rowSeparatorXStop, rowSeparatorY, separatorPaint
                )
            }

            // Vertical lines drawing
            val columnSeparatorX = columnWidth * (index + 1) + leftPadding
            drawLine(
                columnSeparatorX, topPadding, columnSeparatorX, columnSeparatorYStop, separatorPaint
            )

            // Months names drawing
            val monthNameShort = Month.values()[index].name.take(3)
            val monthNameWidthHalf = monthTextPaint.measureText(monthNameShort) / 2
            val gap = columnWidth / 2 - monthNameWidthHalf
            val monthX = columnWidth * (index + 0.75f) + gap + leftPadding
            val rotationY = monthY - columnWidth / 2
            rotate(45f, monthX, rotationY)
            drawText(monthNameShort, monthX, monthY, monthTextPaint)
            rotate(-45f, monthX, rotationY)
        }
    }

    private fun Canvas.drawGraph() {
        val temperatureList = weatherMap.values.mapTo(HashSet()) { it?.toInt() }.sortedBy { it }
        val lowerPosY = rowHeight * (temperatureList.size - 1) + topPadding
        val pointsList = mutableListOf<PointF>()

        temperatureList.forEachIndexed { index, temperature ->
            val monthsByTemperature = weatherMap.filterValues { it?.toInt() == temperature }.keys

            val pointY = lowerPosY - rowHeight * index

            val textX = 0f
            val textY = pointY + topPadding / 3
            drawText("$temperature \u2103", textX, textY, temperatureTextPaint)

            monthsByTemperature.forEach { month ->
                val pointX = columnWidth * (month.ordinal + 1) + leftPadding
                drawPoint(pointX, pointY, pointPaint)
                pointsList.add(PointF(pointX, pointY))
            }
        }

        var prevPointX: Float? = null
        var prevPointY: Float? = null
        pointsList.sortBy { it.x }
        pointsList.forEach { point ->
            if (prevPointX != null && prevPointY != null) {
                drawLine(prevPointX!!, prevPointY!!, point.x, point.y, graphLinePaint)
            }

            prevPointX = point.x
            prevPointY = point.y
        }
    }

    fun setWeather(weatherList: List<ViewWeather>) {
        weatherMap = mapOf()

        val map = mutableMapOf<Month, Double?>()

        weatherList.forEach {
            map[it.month] = it.temperatureAverage
        }

        weatherMap = map
        invalidate()
    }
}

data class ViewWeather(
    val month: Month,
    val temperatureAverage : Double? = null
)