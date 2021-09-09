package com.github.stonybean.mygraph.view

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
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.github.stonybean.mygraph.viewmodel.GraphViewModel
import com.github.stonybean.mygraph.R
import com.github.stonybean.mygraph.utils.FilePathGetter
import com.github.stonybean.mygraph.utils.RandomColorMaker
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Created by Joo on 2021/09/03
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // ViewModel DI
    private val viewModel: GraphViewModel by viewModel()

    // ActivityResultLauncher
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    // Permission 처리
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            Toast.makeText(this, getString(R.string.toast_request_permission), Toast.LENGTH_SHORT).show()
        }

    // 그래프 넘겨줄 전체 포인트 리스트
    private val pointList: HashMap<Int, ArrayList<Int>> = HashMap()

    // 항목별 색상 리스트
    private val colorList: ArrayList<Int> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.activity = this@MainActivity

        viewModel.isSave.observe(this, { isSave ->
            if (isSave) {
                Toast.makeText(this, getString(R.string.toast_save_success), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, getString(R.string.toast_save_fail), Toast.LENGTH_SHORT).show()
            }
        })

        // Floating Action Button
        binding.fab.setOnClickListener {
            addCell()   // 셀 동적 추가
        }

        // LongClick -> onClickSavePdf() -> resultLauncher -> savePdf()
        binding.fab.setOnLongClickListener {
            hideKeyboard()
            requestSavePdf()    // PDF로 저장
            true
        }

        // PDF 파일 저장소 선택 후 처리
        resultLauncher = registerForActivityResult(
            StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val uri = result.data!!.data
                val uriPath = FilePathGetter().getPath(this, uri!!)
                viewModel.savePdf(binding.rlGraphLayout, uriPath)
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

    /***** 그래프 타이틀 동적 추가 *****/
    fun addCellTitle(childCount: Int, title: String) {
        val itemCellTextBinding = ItemCellTextBinding.inflate(layoutInflater)

        itemCellTextBinding.tvCellTitle.tag = childCount
        itemCellTextBinding.tvCellTitle.text = title
        itemCellTextBinding.tvCellTitle.setTextColor(colorList[childCount])

        binding.llCellTitle.addView(itemCellTextBinding.root)
    }

    /***** 셀 동적 추가 (binding.fab) *****/
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

    /***** 그래프 그리기 *****/
    fun drawGraph() {
        val graphPointList: ArrayList<ArrayList<Int>> = ArrayList()

        for (i in pointList.keys) {
            // 모든 항목 전체
            graphPointList.add(pointList[i]!!.toList() as ArrayList<Int>)
        }

        binding.gvCell.clearGraph()
        binding.gvCell.setColors(colorList)
        binding.gvCell.setPoints(graphPointList)
        binding.rlGraphLayout.visibility = View.VISIBLE
        binding.gvCell.drawViewBeforeOnDraw()
    }

    /***** 권한 확인/요청, 파일 탐색기 열기 (저장할 곳) *****/
    private fun requestSavePdf() {
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
                Toast.makeText(this@MainActivity, getString(R.string.toast_limit_number), Toast.LENGTH_SHORT).show()
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
