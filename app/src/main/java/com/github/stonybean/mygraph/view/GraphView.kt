package com.github.stonybean.mygraph.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.PathShape
import android.util.AttributeSet
import android.view.View

/**
 * Created by Joo on 2021/09/03
 */
class GraphView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val pointList: ArrayList<ArrayList<Int>> = ArrayList()  // 전체 숫자
    private val xList: ArrayList<Int> = ArrayList()     // x축
    private val yList: ArrayList<Int> = ArrayList()     // y축

    private var lineColorList: ArrayList<Int> = ArrayList()     // 선 색상(Int) 리스트
    private val linePathList: ArrayList<Path> = ArrayList()     // 선 경로(Path) 리스트

    private lateinit var circleColor: Paint
    private val linePaintList: ArrayList<Paint> = ArrayList()    // 선별 색깔 관리 리스트 (Paint)

    // 그래프 초기화
    fun clearGraph() {
        lineColorList.clear()
        linePathList.clear()

        linePaintList.clear()

        pointList.clear()
        xList.clear()
        yList.clear()
    }

    // 그래프 색상
    fun setColors(colorList: ArrayList<Int>) {
        lineColorList.addAll(colorList)

        circleColor = Paint()
        circleColor.color = Color.BLACK
    }

    // 그래프 정보 (숫자)
    fun setPoints(points: ArrayList<ArrayList<Int>>) {
        pointList.addAll(points)   // y축 값 리스트
    }

    // 그래프 껍데기 만들기
    private fun drawView() {
        val height = 810    // 높이 810으로 고정 (아래,위 여백)

        for (i in pointList.indices) {
            val path = Path()
            val points = pointList[i]

            val gapX = width.toFloat() / points.size    // 점 사이 거리 (x축)
            val halfGab = gapX / 2
            val length = points.size    // 3

            for (j in 0 until length) {
                // 점 좌표 구하기
                val x = halfGab + (j * gapX)    // x 좌표
                val y = height - points[j]      // y 좌표

                xList.add(x.toInt())
                yList.add(y)

                // 선 그리기
                if (j == 0)
                    path.moveTo(x, y.toFloat())
                else
                    path.lineTo(x, y.toFloat())
            }
            linePathList.add(path)  // 선 path 저장

            // 그려진 선 칠하기
            val shape = ShapeDrawable(PathShape(path, 1F, 1F))
            shape.setBounds(0, 0, 1, 1)

            val paint = shape.paint
            paint.style = Paint.Style.STROKE
            paint.color = lineColorList[i]
            paint.strokeWidth = 2F
            paint.isAntiAlias = true

            linePaintList.add(paint)
        }
    }

    fun drawViewBeforeOnDraw() {
        viewTreeObserver.addOnGlobalLayoutListener {
            drawView()
        }
    }

    // 그래프 그리기
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // 선 그리기 (색깔 구분)
        for (i in linePathList.indices) {
            canvas?.drawPath(linePathList[i], linePaintList[i])
        }

        // 점 그리기
        for (i in 0 until xList.size) {
            canvas?.drawCircle(
                xList[i].toFloat(),
                yList[i].toFloat(), 10F,
                circleColor
            )
        }
    }
}