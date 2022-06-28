package com.coldfier.feature_countries.ui.country_detail

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.coldfier.core_data.repository.models.Month
import com.coldfier.feature_countries.R
import kotlin.math.roundToInt
import kotlin.math.sqrt

class WeatherGraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val separatorsPaint = Paint().apply {
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

    private val linePaint = Paint().apply {
        strokeWidth = resources.getDimension(R.dimen.line_width)
        color = ContextCompat.getColor(context, R.color.line_paint)
    }

    private val monthDividerWidth = resources.getDimension(R.dimen.month_divider_width)

    private val rowHeight = resources.getDimension(R.dimen.temperature_degree_height).roundToInt()

    private val contentWidth: Int
        get() = monthDividerWidth.roundToInt() * 12

    private val rowRect = Rect()

    private var weatherMap = mapOf<Month, Double?>()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val width = if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            contentWidth
        } else {
            MeasureSpec.getSize(widthMeasureSpec)
        }

        val topPadding = resources.getDimension(R.dimen.top_padding)
        val monthNameLength = monthTextPaint.measureText(Month.values()[0].name.take(3))
        val monthNameHeight = monthNameLength * sqrt(2.0).toFloat() / 2
        val temperatureValuesCount = weatherMap.values.mapTo(HashSet()) { it?.toInt() }.size

        val minRowHeight = resources.getDimension(R.dimen.min_row_height)

        val minHeight = topPadding + monthNameHeight + minRowHeight * temperatureValuesCount

        val height = if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            val gridHeight = temperatureValuesCount * minRowHeight
            (gridHeight + topPadding + monthNameHeight).roundToInt()
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
        rowRect.set(0,0, w, rowHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.apply {
            drawGrid()
            drawDiagram()
        }
    }

    private fun Canvas.drawGrid() {
        val gridXYValue = 12
        val topPadding = resources.getDimension(R.dimen.top_padding)
        val columnWidth = width / gridXYValue - 2
        val columnWidthHalf = columnWidth / 2
        val rowSeparatorXStart = columnWidth.toFloat()
        val rowSeparatorXStop = rowSeparatorXStart * gridXYValue
        val monthNameLength = monthTextPaint.measureText(Month.values()[0].name.take(3))
        val monthNameHeight = monthNameLength * sqrt(2.0).toFloat() / 2
        val rowHeight =
            (height - topPadding - monthNameHeight) / gridXYValue


        val temperatureValuesCount = weatherMap.values.mapTo(HashSet()) { it?.toInt() }.size

        val columnSeparatorYStop = rowHeight * (temperatureValuesCount - 1) + topPadding

        val monthY = if (temperatureValuesCount < 2) {
            rowHeight * temperatureValuesCount + topPadding * 3
        } else {
            rowHeight * temperatureValuesCount + topPadding
        }


        repeat(gridXYValue) { index ->
            // Horizontal lines drawing
            if (index <= temperatureValuesCount - 1) {
                val rowSeparatorY = rowHeight * index + topPadding
                drawLine(
                    rowSeparatorXStart, rowSeparatorY, rowSeparatorXStop, rowSeparatorY, separatorsPaint
                )
            }

            // Vertical lines drawing
            val separatorX = (columnWidth * index).toFloat() + columnWidth / 2 + columnWidthHalf
            drawLine(separatorX, topPadding, separatorX, columnSeparatorYStop, separatorsPaint)

            // Months names drawing
            val monthNameShort = Month.values()[index].name.take(3)
            val monthNameWidthHalf = monthTextPaint.measureText(monthNameShort) / 2
            val gap = columnWidthHalf - monthNameWidthHalf
            val monthX = columnWidth * index + gap + columnWidthHalf + columnWidthHalf /2
            rotate(45f, monthX, monthY - columnWidthHalf)
            drawText(monthNameShort, monthX, monthY, monthTextPaint)
            rotate(-45f, monthX, monthY - columnWidthHalf)
        }
    }

    private fun Canvas.drawDiagram() {
        val gridXYValue = 12
        val topPadding = resources.getDimension(R.dimen.top_padding)
        val columnWidthF = (width / gridXYValue - 2).toFloat()
        val monthNameLength = monthTextPaint.measureText(Month.values()[0].name.take(3))
        val rowHeight =
            (height - topPadding - (monthNameLength * sqrt(2.0).toFloat() / 2)) / gridXYValue

        val temperatureList = weatherMap.values.mapTo(HashSet()) { it?.toInt() }.sortedBy { it }

        val lowerPosY = rowHeight * (temperatureList.size - 1) + topPadding
        val pointsList = mutableListOf<PointF>()

        temperatureList.forEachIndexed { index, temperature ->
            val monthsByTemperature = weatherMap.filterValues { it?.toInt() == temperature }.keys

            val pointY = lowerPosY - rowHeight * index
            val textX = 0f
            val textY = pointY + topPadding / 3

            monthsByTemperature.forEach { month ->
                val pointX = columnWidthF * (month.ordinal + 1)
                drawPoint(pointX, pointY, pointPaint)
                pointsList.add(PointF(pointX, pointY))

                drawText("$temperature \u2103", textX, textY, temperatureTextPaint)
            }
        }

        var prevPointX: Float? = null
        var prevPointY: Float? = null
        pointsList.sortBy { it.x }
        pointsList.forEach { point ->
            if (prevPointX != null && prevPointY != null) {
                drawLine(prevPointX!!, prevPointY!!, point.x, point.y, linePaint)
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