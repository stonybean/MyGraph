package com.github.stonybean.mygraph

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.stonybean.mygraph.databinding.ItemCellBinding

/**
 * Created by Joo on 2021/09/03
 */
class Adapter(private val context: Context, private val cellDataList: ArrayList<CellData>): RecyclerView.Adapter<Adapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ItemCellBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(data: CellData) {
            binding.etTitle.setText(data.title)
            binding.etItem1.setText(data.itemFirst)
            binding.etItem2.setText(data.itemSecond)
            binding.etItem3.setText(data.itemThird)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemCellBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_cell, parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = cellDataList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(cellDataList[position])
    }
}