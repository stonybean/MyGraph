package com.github.stonybean.mygraph

import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import com.github.stonybean.mygraph.databinding.ActivityMainBinding
import com.github.stonybean.mygraph.databinding.ItemCellBinding
import com.github.stonybean.mygraph.databinding.ItemCellTextBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val pointList: HashMap<Int, ArrayList<Int>> = HashMap()      // 그래프 넘겨줄 전체 포인트 리스트
    private val colorList: ArrayList<Int> = ArrayList() // 각 항목별 색상 리스트

    private val viewModel: ViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.activity = this@MainActivity

        val button = binding.fab
        button.setOnClickListener {
//            viewModel.addCell(binding.llCellItem, ItemCellBinding.inflate(layoutInflater))
            addCell()
        }

        pointList.clear()
        colorList.clear()

        // 그래프 타이틀, y축 데이터 초기화
        binding.llCellTitle.removeAllViews()
    }

    override fun onDestroy() {
        super.onDestroy()

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

    // 셀 동적 추가
    private fun addCell() {
        val layout = binding.llCellItem
        val itemCellBinding = ItemCellBinding.inflate(layoutInflater)

        // 현재 뷰의 자식뷰 개수만큼 태그값으로 지정 (index 0부터 시작)
        itemCellBinding.etTitle.tag = layout.childCount

        val titleTag = itemCellBinding.etTitle.tag
        // title TextView와 똑같이 맞추기 위함
        itemCellBinding.etItem1.tag = "${titleTag}-0"
        itemCellBinding.etItem2.tag = "${titleTag}-1"
        itemCellBinding.etItem3.tag = "${titleTag}-2"

        itemCellBinding.etTitle.addTextChangedListener(CellTitleTextChangedListener(titleTag as Int))
        itemCellBinding.etItem1.addTextChangedListener(CellItemTextChangedListener(titleTag))
        itemCellBinding.etItem2.addTextChangedListener(CellItemTextChangedListener(titleTag))
        itemCellBinding.etItem3.addTextChangedListener(CellItemTextChangedListener(titleTag))
        layout.addView(itemCellBinding.root)

        colorList.add(titleTag, randomColor())  // 셀 생성 순간, 색깔 배정(그래프 점,선)
    }

    // 랜덤 색상 만들기
    private fun randomColor(): Int {
        val red = (Math.random() * 255).toInt()
        val blue = (Math.random() * 255).toInt()
        val green = (Math.random() * 255).toInt()

        return Color.rgb(red, blue, green)
    }

    // 셀 - 타이틀 텍스트 감지 리스너 (문자)
    inner class CellTitleTextChangedListener(tag: Int) : TextWatcher {
        private val currentTag = tag
        override fun afterTextChanged(p0: Editable?) {

        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val currentTextView = binding.llCellTitle.findViewWithTag<TextView>(currentTag)

            if (currentTextView != null) {
                currentTextView.text = p0.toString()
                currentTextView.setTextColor(colorList[currentTag])
            } else {
                addCellTitle(currentTag, p0.toString())
            }
        }
    }

    // 셀 - 각각 아이템 텍스트 감지 리스너 (숫자)
    inner class CellItemTextChangedListener(tag: Int) : TextWatcher {
        private val currentTag = tag

        override fun afterTextChanged(p0: Editable?) {
            val editTextPoint: ArrayList<Int> = ArrayList()
            for (i in 0..2) {   // EditText 3개이므로
                val editText = binding.llCellItem.findViewWithTag<EditText>("$currentTag-$i")

                if (editText.text.isNotEmpty()) {
                    editTextPoint.add(editText.text.toString().toInt())
                } else {
                    return
                }
            }

            pointList[currentTag] = editTextPoint

            val graphPointList: ArrayList<ArrayList<Int>> = ArrayList()

            var sum = 0
            var total = 0
            for (i in pointList.keys) {
                graphPointList.add(pointList[i]!!.toList() as ArrayList<Int>)
                sum += pointList[i]!!.sum()
                total += pointList[i]!!.count()
            }

            binding.gvCell.clearGraph()
            binding.gvCell.setType(colorList)
            binding.gvCell.setPoints(
                graphPointList,
                sum / total,
                0,
                10
            )

            binding.rlGraphLayout.visibility = View.VISIBLE
            binding.gvCell.drawForBeforeDrawView()
        }


        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }
    }
}
