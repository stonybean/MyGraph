package com.github.stonybean.mygraph

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.stonybean.mygraph.databinding.ActivityMainBinding
import com.github.stonybean.mygraph.databinding.ItemCellBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: ViewModel by viewModel()

    // adapter 관련
    private lateinit var adapter: Adapter
    private var cellDataList: ArrayList<CellData> = ArrayList()

    private lateinit var cellsLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.activity = this@MainActivity

        cellsLayout = binding.llCell


        val points = intArrayOf(20, 140, 100)

//        val =
            println("??? ${points.sum() / (points.size + 2)}")
        // 전체 개수 + 2 (맨 아래, 위)

        // 전체 합 / , 원점은 0, 총 10줄로 나누어진 그래프를 그린다
        binding.gvCell.setPoints("", points, points.sum() / (points.size + 2), 0, points.size + 2)
        binding.gvCell.drawForBeforeDrawView()


        val button = binding.fab
        button.setOnClickListener {
            addCell()
        }
    }

    private fun addCell() {
        val binding: ItemCellBinding = ItemCellBinding.inflate(layoutInflater)

        // TODO 선 색상
        cellsLayout.addView(binding.root)
    }
}