package com.github.stonybean.mygraph.viewmodel

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by Joo on 2021/09/09
 */
class GraphViewModel: ViewModel() {

    private val _isSave = MutableLiveData<Boolean>()
    val isSave = _isSave

    /***** PDF 저장 *****/
    // View -> Bitmap 변환
    private fun getBitmapFromView(view: View): Bitmap? {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    // Bitmap -> PDF 변환 후 저장
    fun savePdf(view: View, uriPath: String) {
        // 그래프 레이아웃 비트맵으로 만들기
        val bitmap = getBitmapFromView(view)

        // PdfDocument 페이지 빌더 생성 (그래프 레이아웃 가로/세로 크기)
        val pdfDocument = PdfDocument()
        val myPageInfo = PdfDocument.PageInfo.Builder(
            view.width,
            view.height,
            1
        ).create()

        val page = pdfDocument.startPage(myPageInfo)

        // page에 그래프 레이아웃 그리기
        page.canvas.drawBitmap(bitmap!!, 0F, 0F, null)
        pdfDocument.finishPage(page)

        // 사용자가 지정, 저장한 경로로 현재 PDF 그림 파일 덮어쓰기
        val filePath = File(uriPath)
        try {
            pdfDocument.writeTo(FileOutputStream(filePath))
        } catch (e: IOException) {
            e.printStackTrace()
            _isSave.postValue(false)
        }

        // PdfDocument 닫기
        pdfDocument.close()
        _isSave.postValue(true)
    }
}