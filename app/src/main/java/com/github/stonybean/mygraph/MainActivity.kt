package com.github.stonybean.mygraph

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import android.widget.Toast
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
    private val pointList: ArrayList<ArrayList<Int>> = ArrayList()      // 그래프 그릴 때 넘겨줄 전체 포인트값
    private val pointHashMap: HashMap<Int, ArrayList<Int>> = HashMap()  // Tag 별도 관리 (EditText별)

    private val viewModel: ViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.activity = this@MainActivity

        val points: ArrayList<Int> = ArrayList()
        val points2: ArrayList<Int> = ArrayList()
        val color: ArrayList<Int> = ArrayList()

        points.add(20)
        points.add(100)
        points.add(50)
        color.add(Color.BLUE)

        points2.add(80)
        points2.add(200)
        points2.add(100)
        color.add(Color.RED)

        val test: ArrayList<ArrayList<Int>> = ArrayList()
        test.add(points)
        test.add(points2)


        // 전체 개수 + 2 (맨 아래, 위)
        // 전체 합 / , 원점은 0, 총 10줄로 나누어진 그래프를 그린다
        binding.gvCell.setType(color)
        binding.gvCell.setPoints(test, points.sum() / 3, 0, points.size)
        binding.gvCell.drawForBeforeDrawView()


        val button = binding.fab
        button.setOnClickListener {
//            viewModel.addCell(binding.llCellItem, ItemCellBinding.inflate(layoutInflater))
            addCell()
        }

        // 그래프 타이틀, y축 데이터 초기화
        binding.llCellTitle.removeAllViews()
        binding.llCellUnit.removeAllViews()
    }


    fun addCellTitle(childCount: Int, title: String) {

        val itemCellTextBinding = ItemCellTextBinding.inflate(layoutInflater)

        itemCellTextBinding.tvCellTitle.tag = childCount
        itemCellTextBinding.tvCellTitle.text = title

        binding.llCellTitle.addView(itemCellTextBinding.root)
    }

    fun addCellUnit(childCount: Int, point: String) {

        val itemCellTextBinding = ItemCellTextBinding.inflate(layoutInflater)

        itemCellTextBinding.tvCellTitle.tag = childCount
        itemCellTextBinding.tvCellTitle.text = title

        binding.llCellUnit.addView(itemCellTextBinding.root)
    }

    fun addCell() {

        val layout = binding.llCellItem
        val itemCellBinding = ItemCellBinding.inflate(layoutInflater)

//        itemCellBinding.etTitle.tag = layout.childCount + 1
        itemCellBinding.etTitle.tag = layout.childCount

        val titleTag = itemCellBinding.etTitle.tag
        // title TextView와 똑같이 맞추기 위함
        itemCellBinding.etItem1.tag = titleTag
        itemCellBinding.etItem2.tag = titleTag
        itemCellBinding.etItem3.tag = titleTag

        itemCellBinding.etTitle.addTextChangedListener(CellTitleTextChangedListener(titleTag as Int))
        itemCellBinding.etItem1.addTextChangedListener(CellItemTextChangedListener(titleTag))
        itemCellBinding.etItem2.addTextChangedListener(CellItemTextChangedListener(titleTag))
        itemCellBinding.etItem3.addTextChangedListener(CellItemTextChangedListener(titleTag))
        
        layout.addView(itemCellBinding.root)
    }

    inner class CellTitleTextChangedListener(tag: Int) : TextWatcher {
        private val currentTag = tag
        override fun afterTextChanged(p0: Editable?) {

        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (binding.llCellTitle.findViewWithTag<TextView>(currentTag) != null) {
                binding.llCellTitle.findViewWithTag<TextView>(currentTag).text = p0.toString()
            } else {
                addCellTitle(currentTag, p0.toString())
            }
        }

    }

    inner class CellItemTextChangedListener(tag: Int) : TextWatcher {
        private val currentTag = tag
        override fun afterTextChanged(p0: Editable?) {

        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val point: ArrayList<Int> = ArrayList()
            point.add(p0.toString().toInt())

            //        val points: ArrayList<Int> = ArrayList()
            //        val points2: ArrayList<Int> = ArrayList()
            //        points.add(20)
            //        points.add(100)
            //        points.add(50)
            // points = (20, 100, 50)
            // points2 = (120, 200, 30)

            if (pointHashMap[currentTag] == null) {
                pointList[currentTag] = point
                pointHashMap[currentTag] = point
            } else {
                pointList[currentTag].add(point.toString().toInt())
                pointHashMap[currentTag]?.add(p0.toString().toInt())
            }

            var isCorrect = true
            pointList.forEach {
                if (it.size != 3) {
                    // 하나의 아이템이라도 비어있으면 false
                    isCorrect = false
                }
            }

            if (isCorrect) {
                // 그래프 그리기 요청
                binding.gvCell.setPoints(
                    pointList,
                    pointList[currentTag].sum() / 3,
                    0,
                    pointList[currentTag].size
                )
                binding.gvCell.drawForBeforeDrawView()
            } else {
                Toast.makeText(this@MainActivity, "항목을 모두 채워주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
