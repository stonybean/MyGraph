package com.github.stonybean.mygraph

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.PathShape
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver

/**
 * Created by Joo on 2021/09/03
 */
class GraphView(context: Context, attrs: AttributeSet) : View(context, attrs),
    ViewTreeObserver.OnGlobalLayoutListener {

    private lateinit var mLineShape: ShapeDrawable
    private lateinit var mPointPaint: Paint

    private var mThickness: Float = 0F
    private lateinit var mPoints: IntArray
    private lateinit var mPointX: IntArray
    private lateinit var mPointY: IntArray

    private var mPointSize: Int = 0
    private var mPointRadius: Int = 0
    private var mLineColor: Int = 0
    private var mUnit: Int = 0
    private var mOrigin: Int = 0
    private var mDivide: Int = 0

    init {
        setType(context, attrs)
    }

    fun setType(context: Context, attrs: AttributeSet) {
//        val types: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.GraphView)

//        mPointPaint = Paint()
//        mPointPaint.color = types.getColor(R.styleable.GraphView_pointColor, Color.BLACK)
//        mPointSize = types.getDimension(R.styleable.GraphView_pointSize, 10F).toInt()
//        mPointRadius = mPointSize / 2
//
//        mLineColor = types.getColor(R.styleable.GraphView_lineColor, Color.BLACK)
//        mThickness = types.getDimension(R.styleable.GraphView_lineThickness, 1F)

        mPointPaint = Paint()
        mPointPaint.color = Color.BLACK
        mPointSize = 20
        mPointRadius = mPointSize / 2

        mLineColor = Color.BLACK
        mThickness = 2F
    }

    //그래프 정보를 받는다
    fun setPoints(title: String, points: IntArray, unit: Int, origin: Int, divide: Int) {
        mPoints = points   //y축 값 배열

        mUnit = unit       //y축 단위
        mOrigin = origin   //y축 원점
        mDivide = divide   //y축 값 갯수
    }


    //그래프를 만든다
    private fun draw() {
        println("draw")
        val path = Path()

        val height = height
        val points = mPoints

        //x축 점 사이의 거리
        val gapX = width.toFloat() / points.size

        //y축 단위 사이의 거리
        val gapY = (height - mPointSize) / mDivide

        val halfGab = gapX / 2

        val length = points.size
        mPointX = IntArray(length)
        mPointY = IntArray(length)


        for (i in 0 until length) {
            // 점 좌표 구하기
            val x = halfGab + (i * gapX)
            val y = height - mPointRadius - (((points[i] / mUnit) - mOrigin) * gapY)

            mPointX[i] = x.toInt()
            mPointY[i] = y

            // 선 그리기
            if (i == 0)
                path.moveTo(x, y.toFloat())
            else
                path.lineTo(x, y.toFloat())
        }

        // 그려진 선으로 shape을 만든다
        val shape = ShapeDrawable(PathShape(path, 1F, 1F))
        shape.setBounds(0, 0, 1, 1)

        val paint = shape.paint
        paint.style = Paint.Style.STROKE
        paint.color = mLineColor
        paint.strokeWidth = mThickness
        paint.isAntiAlias = true

        mLineShape = shape
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


        // 텍스트 그리기 (타이틀, y축 값)
        val textPaint = Paint()
        textPaint.color = Color.BLUE
        textPaint.textSize = 50F
        textPaint.textAlign = Paint.Align.RIGHT

        for (i in mPointY.indices) {
            canvas?.drawText(mPoints[i].toString(), 150F, mPointY[i].toFloat(), textPaint)
        }

        canvas?.drawText("Title", 300F, mPointY.lastIndex.toFloat() + 100, textPaint)

        // 선 그리기
        canvas?.let { mLineShape.draw(it) }


        // 점 그리기
        val length = mPointX.size
        for (i in 0 until length) {
            canvas?.drawCircle(
                mPointX[i].toFloat(),
                mPointY[i].toFloat(), mPointRadius.toFloat(), mPointPaint
            )
        }


    }

    override fun onGlobalLayout() {
    }
}