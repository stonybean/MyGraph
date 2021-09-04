package com.github.stonybean.mygraph

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.stonybean.mygraph.databinding.ItemCellBinding

/**
 * Created by Joo on 2021/09/03
 */
class ViewModel : ViewModel() {

    val _editList = MutableLiveData<ArrayList<EditText>>()
    val editList = _editList

    val _cellTitle = MutableLiveData<String>()
    val cellTitle = _cellTitle

    fun addCellTitle(context: Context, layout: LinearLayout) {
        val textView = TextView(context)
        textView.tag = "textView1"
        textView.text = "Test"
        textView.textSize = 16F
        textView.setTextColor(Color.BLACK)

        layout.addView(textView)
    }

    fun addCell(layout: LinearLayout, itemCellBinding: ItemCellBinding) {

        println("??? ${layout.childCount}")
        itemCellBinding.etTitle.addTextChangedListener(CellTitleTextChangedListener())
        itemCellBinding.etItem1.addTextChangedListener(CellItemTextChangedListener())
        itemCellBinding.etItem2.addTextChangedListener(CellItemTextChangedListener())
        itemCellBinding.etItem3.addTextChangedListener(CellItemTextChangedListener())

        layout.addView(itemCellBinding.root)
    }


    inner class CellTitleTextChangedListener : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {

        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            _cellTitle.postValue(p0.toString())
        }
    }

    private class CellItemTextChangedListener : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {

        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }
    }
}