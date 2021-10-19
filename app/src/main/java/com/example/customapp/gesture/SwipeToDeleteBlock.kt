package com.example.customapp.gesture

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.customapp.MainActivity
import com.example.customapp.fragment.block.BlockFragment

abstract class SwipeToDeleteBlock(val context: BlockFragment, dragDir: Int, swipeDir: Int): ItemTouchHelper.SimpleCallback(dragDir, swipeDir) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

}