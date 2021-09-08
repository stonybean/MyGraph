package com.github.stonybean.mygraph

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
    private var thickness: Float = 0F

    private val pointList: ArrayList<ArrayList<Int>> = ArrayList()  // 전체 숫자
    private val xList: ArrayList<Int> = ArrayList()     // x축
    private val yList: ArrayList<Int> = ArrayList()     // y축

    private var circleSize: Int = 0
    private var circleRadius: Int = 0
    private var unit: Int = 0
    private var origin: Int = 0
    private var divide: Int = 0

    private var lineColorList: ArrayList<Int> = ArrayList()     // 선 색상(Int) 리스트
    private val linePathList: ArrayList<Path> = ArrayList()     // 선 경로(Path) 리스트

    private val circlePaintList: ArrayList<Paint> = ArrayList()  // 점별 색깔 관리 리스트 (Paint)
    private val linePaintList: ArrayList<Paint> = ArrayList()    // 선별 색깔 관리 리스트 (Paint)

    // 그래프 초기화
    fun clearGraph() {
        lineColorList.clear()
        linePathList.clear()

        circlePaintList.clear()
        linePaintList.clear()

        pointList.clear()
        xList.clear()
        yList.clear()
    }

    // 그래프 그리기 옵션
    fun setType(colorList: ArrayList<Int>) {
        colorList.forEach {
            val pointPaint = Paint()
            pointPaint.color = it
            circlePaintList.add(pointPaint)
        }

        circleSize = 20
        circleRadius = circleSize / 2

        lineColorList.addAll(colorList)
        thickness = 2F

    }

    // 그래프 정보
    fun setPoints(points: ArrayList<ArrayList<Int>>, unit: Int, origin: Int, divide: Int) {
        pointList.addAll(points)   // y축 값 리스트

        this.unit = unit       // y축 단위
        this.origin = origin   // y축 원점
        this.divide = divide   // y축 개수
    }

    // 그래프 만들기
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
            paint.strokeWidth = thickness
            paint.isAntiAlias = true

            linePaintList.add(paint)
        }
    }

    fun onDrawBeforeDrawView() {
        viewTreeObserver.addOnGlobalLayoutListener {
            drawView()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // 선 그리기 (색깔 구분)
        for (i in linePathList.indices) {
            canvas?.drawPath(linePathList[i], linePaintList[i])
        }

        // 점 그리기
        val length = xList.size
        for (i in 0 until length) {

            // TODO : 점 색깔 구분 처리.... ?
            val paint = Paint()
            paint.color = Color.BLACK
//            if (i < 3) {
//               paint = pointPaintList[0]
//            } else {
//               paint = pointPaintList[1]
//            }
            canvas?.drawCircle(
                xList[i].toFloat(),
                yList[i].toFloat(), circleRadius.toFloat(),
                paint
            )
        }
    }
}