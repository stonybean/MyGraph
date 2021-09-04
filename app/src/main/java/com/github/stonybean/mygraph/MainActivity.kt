package com.github.stonybean.mygraph

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.marginEnd
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.stonybean.mygraph.databinding.ActivityMainBinding
import com.github.stonybean.mygraph.databinding.ItemCellBinding
import com.github.stonybean.mygraph.databinding.ItemCellTitleBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: ViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.activity = this@MainActivity

        val points = intArrayOf(20, 140, 100)

//        val =
            println("??? ${points.sum() / (points.size + 2)}")
        // 전체 개수 + 2 (맨 아래, 위)

        // 전체 합 / , 원점은 0, 총 10줄로 나누어진 그래프를 그린다
        binding.gvCell.setPoints("", points, points.sum() / (points.size + 2), 0, points.size)
        binding.gvCell.drawForBeforeDrawView()

//        viewModel.cellTitle.observe(this, Observer {
//
//        })

        val button = binding.fab
        button.setOnClickListener {
//            viewModel.addCell(binding.llCellItem, ItemCellBinding.inflate(layoutInflater))
            addCell()
        }

        binding.llCellTitle.removeAllViews()
//        viewModel.addCellTitle(this, binding.llCellTitle)
    }


    fun addCellTitle(childCount: Int, title: String) {

        val itemCellTitleBinding = ItemCellTitleBinding.inflate(layoutInflater)

        itemCellTitleBinding.tvCellTitle.tag = childCount
        itemCellTitleBinding.tvCellTitle.text = title

        binding.llCellTitle.addView(itemCellTitleBinding.root)
    }

    fun addCell() {

        val layout = binding.llCellItem
        val itemCellBinding = ItemCellBinding.inflate(layoutInflater)

        println("??? ${layout.childCount}")
        itemCellBinding.etTitle.tag = layout.childCount + 1

        itemCellBinding.etTitle.addTextChangedListener(CellTitleTextChangedListener(itemCellBinding.etTitle.tag as Int))

        itemCellBinding.etItem1.addTextChangedListener(CellItemTextChangedListener())
        itemCellBinding.etItem2.addTextChangedListener(CellItemTextChangedListener())
        itemCellBinding.etItem3.addTextChangedListener(CellItemTextChangedListener())

        // title TextView와 똑같이 맞추기 위함
        
//        itemCellBinding.etItem1.tag = "${layout.childCount + 1}"
//        itemCellBinding.etItem2.tag = "${layout.childCount + 1}"
//        itemCellBinding.etItem3.tag = "${layout.childCount + 1}"

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

    inner class CellItemTextChangedListener: TextWatcher {
        override fun afterTextChanged(p0: Editable?) {

        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }
    }
}
