package com.mkielar.szewc.core.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View
import com.mkielar.szewc.R
import com.mkielar.szewc.core.model.Grid
import com.mkielar.szewc.setAlphaComponent
import kotlin.math.floor

class GridView(context: Context) : View(context) {
    private lateinit var grid: Grid
    private var initialized = false

    @Suppress("DEPRECATION") //todo check if possible to use themes
    private val backgroundColor = resources.getColor(R.color.gridBackground)
    private val blankLineColor = resources.getColor(R.color.blankLineColor)
    private val marginPercent = 0.075F
    private val lineWidth = 15F
    private val cellAlpha = 80

    private val paint = Paint()
    private val linePaint = Paint()
    private val cellPaint = Paint()
    private var cellSize = 0F
    private var margin = 0F

    var horizontalCallback: ((Int) -> Unit)? = null
    var verticalCallback: ((Int) -> Unit)? = null

    fun drawGrid(grid: Grid) {
        paint.color = Color.argb(80, 0, 255, 0)
        this.grid = grid
        this.initialized = true
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.apply {
            drawBackground()
            if (initialized) {
                drawCells()
                drawLines()
            }
        }
    }

    private fun Canvas.drawBackground() {
        drawColor(backgroundColor)
    }

    private fun Canvas.drawCells() {
        grid.cells.forEachIndexed { i, cell ->
            cell.owner?.also {
                cellPaint.color = it.color.setAlphaComponent(cellAlpha)
                drawRect(
                    i / 3 * cellSize + margin,
                    i % 3 * cellSize + margin,
                    (i / 3 + 1) * cellSize + margin,
                    (i % 3 + 1) * cellSize + margin,
                    cellPaint
                )
            }
        }
    }

    private fun Canvas.drawLines() {
        grid.vertical.forEachIndexed { index, line ->
            linePaint.color = line.owner?.color ?: blankLineColor
            drawVerticalLine(index / grid.size, index % grid.size, linePaint)
        }
        grid.horizontal.forEachIndexed { index, line ->
            linePaint.color = line.owner?.color ?: blankLineColor
            drawHorizontalLine(index / grid.size, index % grid.size, linePaint)
        }
    }

    private fun Canvas.drawVerticalLine(i: Int, j: Int, paint: Paint) {
        drawPath(Path().apply {
            moveTo(i * cellSize + margin, j * cellSize + margin)
            lineTo(i * cellSize + margin - lineWidth, j * cellSize + margin + lineWidth)
            lineTo(i * cellSize + margin - lineWidth, j * cellSize + cellSize + margin - lineWidth)
            lineTo(i * cellSize + margin, j * cellSize + cellSize + margin)
            lineTo(i * cellSize + margin + lineWidth, j * cellSize + cellSize + margin - lineWidth)
            lineTo(i * cellSize + margin + lineWidth, j * cellSize + margin + lineWidth)
            lineTo(i * cellSize + margin, j * cellSize + margin)
        }, paint)
    }

    private fun Canvas.drawHorizontalLine(i: Int, j: Int, paint: Paint) {
        drawPath(Path().apply {
            moveTo(j * cellSize + margin, i * cellSize + margin)
            lineTo(j * cellSize + margin + lineWidth, i * cellSize + margin - lineWidth)
            lineTo(j * cellSize + cellSize + margin - lineWidth, i * cellSize + margin - lineWidth)
            lineTo(j * cellSize + cellSize + margin, i * cellSize + margin)
            lineTo(j * cellSize + cellSize + margin - lineWidth, i * cellSize + margin + lineWidth)
            lineTo(j * cellSize + margin + lineWidth, i * cellSize + margin + lineWidth)
            lineTo(j * cellSize + margin, i * cellSize + margin)
        }, paint)
    }

    @SuppressLint("ClickableViewAccessibility") //todo check possible solutions
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.also {
            if (event.action != MotionEvent.ACTION_DOWN) return false
            for (i in 0..grid.size) {
                for (j in 0 until grid.size) {
                    if (horizontalLineTouched(i, j, it)) {
                        horizontalCallback?.invoke(i * grid.size + j)
                        break
                    }
                    if (verticalLineTouched(i, j, it)) {
                        verticalCallback?.invoke(i * grid.size + j)
                        break
                    }
                }
            }
        }
        return true
    }

    private fun horizontalLineTouched(i: Int, j: Int, it: MotionEvent) = contains(
        floatArrayOf(
            j * cellSize + margin,
            j * cellSize + cellSize / 2 + margin,
            j * cellSize + cellSize + margin,
            j * cellSize + cellSize / 2 + margin
        ),
        floatArrayOf(
            i * cellSize + margin,
            i * cellSize - cellSize / 2 + margin,
            i * cellSize + margin,
            i * cellSize + cellSize / 2 + margin
        ),
        it.x,
        it.y
    )

    private fun verticalLineTouched(i: Int, j: Int, it: MotionEvent) = contains(
        floatArrayOf(
            i * cellSize + margin - cellSize / 2,
            i * cellSize + margin,
            i * cellSize + margin + cellSize / 2,
            i * cellSize + margin
        ),
        floatArrayOf(
            j * cellSize + margin + cellSize / 2,
            j * cellSize + margin,
            j * cellSize + margin + cellSize / 2,
            j * cellSize + cellSize + margin
        ),
        it.x,
        it.y
    )

    private fun contains(
        vertX: FloatArray,
        vertY: FloatArray,
        testX: Float,
        testY: Float
    ): Boolean {
        val nvert = vertX.size
        var i = 0
        var j = nvert - 1
        var c = false
        while (i < nvert) {
            if (vertY[i] > testY != vertY[j] > testY &&
                testX < (vertX[j] - vertX[i]) * (testY - vertY[i]) / (vertY[j] - vertY[i]) + vertX[i]
            ) c = !c
            j = i++
        }
        return c
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = MeasureSpec.getSize(widthMeasureSpec)
        cellSize = (size - margin * 2) / grid.size
        margin = floor(marginPercent * size)
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}