package com.github.stonybean.mygraph.utils

import android.graphics.Color

/**
 * Created by Joo on 2021/09/08
 */
class RandomColorMaker {
    // 랜덤 색상 만들기
    fun randomColor(): Int {
        val red = (Math.random() * 255).toInt()
        val blue = (Math.random() * 255).toInt()
        val green = (Math.random() * 255).toInt()

        return Color.rgb(red, blue, green)
    }
}