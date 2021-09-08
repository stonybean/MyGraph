package com.github.stonybean.mygraph

import android.Manifest
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.databinding.DataBindingUtil
import com.github.stonybean.mygraph.databinding.ActivityMainBinding
import com.github.stonybean.mygraph.databinding.ItemCellBinding
import com.github.stonybean.mygraph.databinding.ItemCellTextBinding
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.github.stonybean.mygraph.utils.FilePathGetter
import com.github.stonybean.mygraph.utils.RandomColorMaker
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by Joo on 2021/09/03
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // ActivityResultLauncher
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    // Permission 처리
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            Toast.makeText(this, "권한을 허용해 주세요.", Toast.LENGTH_SHORT).show()
        }

    // 그래프 넘겨줄 전체 포인트 리스트
    private val pointList: HashMap<Int, ArrayList<Int>> = HashMap()

    // 항목별 색상 리스트
    private val colorList: ArrayList<Int> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.activity = this@MainActivity

        // Floating Action Button
        binding.fab.setOnClickListener {
            addCell()   // 셀 동적 추가
        }

        // LongClick -> onClickSavePdf() -> resultLauncher -> savePdf()
        binding.fab.setOnLongClickListener {
            hideKeyboard()
            onClickSavePdf()    // PDF로 저장
            true
        }

        // PDF 파일 저장소 선택 후 처리
        resultLauncher = registerForActivityResult(
            StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val uri = result.data!!.data
                val uriPath = FilePathGetter().getPath(this, uri!!)
                savePdf(binding.rlGraphLayout, uriPath)
            }
        }

        // 리스트 초기화
        pointList.clear()
        colorList.clear()

        // 그래프 타이틀, y축 데이터 초기화
        binding.llCellTitle.removeAllViews()
    }

    override fun onDestroy() {
        super.onDestroy()

        // 리스트 초기화
        pointList.clear()
        colorList.clear()
    }

    // 그래프 타이틀 동적 추가
    fun addCellTitle(childCount: Int, title: String) {
        val itemCellTextBinding = ItemCellTextBinding.inflate(layoutInflater)

        itemCellTextBinding.tvCellTitle.tag = childCount
        itemCellTextBinding.tvCellTitle.text = title
        itemCellTextBinding.tvCellTitle.setTextColor(colorList[childCount])

        binding.llCellTitle.addView(itemCellTextBinding.root)
    }

    // 셀 동적 추가 (binding.fab)
    private fun addCell() {
        val layout = binding.llCellItem
        val itemCellBinding = ItemCellBinding.inflate(layoutInflater)

        // 현재 뷰의 자식뷰 개수만큼 태그값으로 지정 (index 0부터 시작)
        itemCellBinding.etTitle.tag = layout.childCount

        val titleTag = itemCellBinding.etTitle.tag as Int
        // title TextView와 똑같이 맞추기 위함
        itemCellBinding.etItem1.tag = "${titleTag}-0"
        itemCellBinding.etItem2.tag = "${titleTag}-1"
        itemCellBinding.etItem3.tag = "${titleTag}-2"

        itemCellBinding.etTitle.addTextChangedListener(CellTitleTextChangedListener(titleTag))
        itemCellBinding.etItem1.addTextChangedListener(CellItemTextChangedListener(titleTag))
        itemCellBinding.etItem2.addTextChangedListener(CellItemTextChangedListener(titleTag))
        itemCellBinding.etItem3.addTextChangedListener(CellItemTextChangedListener(titleTag))
        layout.addView(itemCellBinding.root)

        colorList.add(titleTag, RandomColorMaker().randomColor())  // 색깔 배정(타이틀, 그래프 선)
    }

    // 그래프 그리기
    fun drawGraph() {
        val graphPointList: ArrayList<ArrayList<Int>> = ArrayList()

        var sum = 0
        var total = 0
        for (i in pointList.keys) {
            // 모든 항목 전체 합과 개수 구하기
            graphPointList.add(pointList[i]!!.toList() as ArrayList<Int>)
            sum += pointList[i]!!.sum()
            total += pointList[i]!!.count()
        }

        binding.gvCell.clearGraph()
        binding.gvCell.setType(colorList)
        binding.gvCell.setPoints(
            graphPointList,
            sum / total,    // 평균값
            0,             // 시작점
            10            // 나눠서 표시할 구역수
        )

        binding.rlGraphLayout.visibility = View.VISIBLE
        binding.gvCell.onDrawBeforeDrawView()
    }

    /***** PDF 저장 *****/
    // View -> Bitmap 변환
    private fun getBitmapFromView(view: View): Bitmap? {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    // Bitmap -> PDF 변환 후 저장
    private fun savePdf(view: View, uriPath: String) {
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
            Toast.makeText(this, "저장에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // PdfDocument 닫기
        pdfDocument.close()
        Toast.makeText(this, "저장에 성공하였습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun onClickSavePdf() {
        if (binding.rlGraphLayout.visibility == View.GONE) {
            return
        }

        // 권한 확인
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            )
            == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent()
            intent.type = "application/pdf"
            intent.action = Intent.ACTION_CREATE_DOCUMENT
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            resultLauncher.launch(intent)
        } else {
            // 권한 요청
            val requestPermissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            permissionLauncher.launch(requestPermissions)
        }
    }

    /***** 키보드 숨기기 *****/
    private fun hideKeyboard() {
        try {
            val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (currentFocus!!.windowToken != null) {
                inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /***** 텍스트 변화 리스너 *****/
    // 셀 - 타이틀 텍스트 감지 리스너 (문자)
    inner class CellTitleTextChangedListener(tag: Int) : TextWatcher {
        private val currentTag = tag
        override fun afterTextChanged(p0: Editable?) {
            val currentTextView = binding.llCellTitle.findViewWithTag<TextView>(currentTag)

            if (currentTextView != null) {
                currentTextView.text = p0.toString()
                currentTextView.setTextColor(colorList[currentTag])
            } else {
                addCellTitle(currentTag, p0.toString())
            }
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }
    }

    // 셀 - 각각 아이템 텍스트 감지 리스너 (숫자)
    inner class CellItemTextChangedListener(tag: Int) : TextWatcher {
        private val currentTag = tag

        override fun afterTextChanged(p0: Editable?) {
            if (p0!!.isNotEmpty() && p0.toString().toInt() > 800) {
                Toast.makeText(this@MainActivity, "800 이하로만 입력할 수 있습니다.", Toast.LENGTH_SHORT).show()
                p0.clear()
                return
            }

            val editTextPoint: ArrayList<Int> = ArrayList()
            for (i in 0..2) {   // EditText 3개이므로
                val editText = binding.llCellItem.findViewWithTag<EditText>("$currentTag-$i")

                if (editText.text.isNotEmpty()) {
                    editTextPoint.add(editText.text.toString().toInt())
                } else {
                    return
                }
            }
            pointList[currentTag] = editTextPoint   // 각 항목별 포인트들 저장
            drawGraph()
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }
    }
}
