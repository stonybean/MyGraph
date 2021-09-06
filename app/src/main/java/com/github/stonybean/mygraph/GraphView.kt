package com.github.stonybean.mygraph

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.PathShape
import android.util.AttributeSet
import android.view.View

/**
 * Created by Joo on 2021/09/03
 */
class GraphView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    //    private lateinit var mLineShape: ShapeDrawable
    private val pointPaintList: ArrayList<Paint> = ArrayList()
    private val mLineShape: ArrayList<ShapeDrawable> = ArrayList()
    private lateinit var pointPaint: Paint

    private var thickness: Float = 0F

    private val pointList: ArrayList<ArrayList<Int>> = ArrayList()
    private val xList: ArrayList<Int> = ArrayList()
    private val yList: ArrayList<Int> = ArrayList()

    private var pointSize: Int = 0
    private var pointRadius: Int = 0
    private var lineColorList: ArrayList<Int> = ArrayList()
    private var unit: Int = 0
    private var origin: Int = 0
    private var divide: Int = 0


    fun setType(colorList: ArrayList<Int>) {

        pointPaint = Paint()
        pointPaint.color = Color.BLACK

        pointSize = 20
        pointRadius = pointSize / 2

        lineColorList.addAll(colorList)
        thickness = 2F

    }

    //그래프 정보를 받는다
    fun setPoints(points: ArrayList<ArrayList<Int>>, unit: Int, origin: Int, divide: Int) {
        pointList.addAll(points)   //y축 값 배열

        this.unit = unit       //y축 단위
        this.origin = origin   //y축 원점
        this.divide = divide   //y축 값 갯수
    }

    //그래프를 만든다
    private fun draw() {
        val path = Path()
        val height = height

        println("mLineColor : ${lineColorList.toList()}")
        for (i in pointList.indices) {
            val points = pointList[i]

            //x축 점 사이의 거리
            val gapX = width.toFloat() / points.size

            //y축 단위 사이의 거리
            val gapY = (height - pointSize) / divide

            val halfGab = gapX / 2

            val length = points.size

            for (j in 0 until length) {
                // 점 좌표 구하기
                val x = halfGab + (j * gapX)
                val y = height - pointRadius - (((points[j] / unit) - origin) * gapY)

                xList.add(x.toInt())
                yList.add(y)

                // 선 그리기
                if (j == 0)
                    path.moveTo(x, y.toFloat())
                else
                    path.lineTo(x, y.toFloat())
            }

            // 그려진 선으로 shape을 만든다
            val shape = ShapeDrawable(PathShape(path, 1F, 1F))
            shape.setBounds(0, 0, 1, 1)

            val paint = shape.paint
            paint.style = Paint.Style.STROKE
            paint.color = lineColorList[i]
//            println("paint.color : ${paint.color}")
            paint.strokeWidth = thickness
            paint.isAntiAlias = true

            mLineShape.add(shape)
        }
    }

    fun drawForBeforeDrawView() {
        viewTreeObserver.addOnGlobalLayoutListener {
            println("drawForBeforeDrawView")
            draw()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        println("onDraw")
        super.onDraw(canvas)

        // 선 그리기
        mLineShape.forEachIndexed { index, shape ->
            canvas?.let { mLineShape[index].draw(it) }
        }

        // 점 그리기
        val length = xList.size
        for (i in 0 until length) {
            canvas?.drawCircle(
                xList[i].toFloat(),
                yList[i].toFloat(), pointRadius.toFloat(), pointPaint
            )
        }
    }

//    //그래프 정보를 받는다
//    fun setPoints(points: IntArray, unit: Int, origin: Int, divide: Int) {
//        mPoints = points   //y축 값 배열
//
//        mUnit = unit       //y축 단위
//        mOrigin = origin   //y축 원점
//        mDivide = divide   //y축 값 갯수
//    }

//    //그래프를 만든다
//    private fun draw() {
//        val path = Path()
//
//        val height = height
//        val points = mPoints
//
//        //x축 점 사이의 거리
//        val gapX = width.toFloat() / points.size
//
//        //y축 단위 사이의 거리
//        val gapY = (height - mPointSize) / mDivide
//
//        val halfGab = gapX / 2
//
//        val length = points.size
//        mPointX = IntArray(length)
//        mPointY = IntArray(length)
//
//
//        for (i in 0 until length) {
//            // 점 좌표 구하기
//            val x = halfGab + (i * gapX)
//            val y = height - mPointRadius - (((points[i] / mUnit) - mOrigin) * gapY)
//
//            mPointX[i] = x.toInt()
//            mPointY[i] = y
//
//            // 선 그리기
//            if (i == 0)
//                path.moveTo(x, y.toFloat())
//            else
//                path.lineTo(x, y.toFloat())
//        }
//
//        // 그려진 선으로 shape을 만든다
//        val shape = ShapeDrawable(PathShape(path, 1F, 1F))
//        shape.setBounds(0, 0, 1, 1)
//
//        val paint = shape.paint
//        paint.style = Paint.Style.STROKE
//        paint.color = mLineColor
//        paint.strokeWidth = mThickness
//        paint.isAntiAlias = true
//
//        mLineShape = shape
//    }
//
//    fun drawForBeforeDrawView() {
//        viewTreeObserver.addOnGlobalLayoutListener {
//            println("drawForBeforeDrawView")
//            draw()
//        }
//    }
//
//    override fun onDraw(canvas: Canvas?) {
//        println("onDraw")
//        super.onDraw(canvas)
//
//        // 선 그리기
//        canvas?.let { mLineShape.draw(it) }
//
//        // 점 그리기
//        val length = mPointX.size
//        for (i in 0 until length) {
//            canvas?.drawCircle(
//                mPointX[i].toFloat(),
//                mPointY[i].toFloat(), mPointRadius.toFloat(), mPointPaint
//            )
//        }
//    }
}