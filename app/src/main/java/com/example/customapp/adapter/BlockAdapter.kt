package com.example.customapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.customapp.R
import com.example.customapp.objects.Block
import com.example.customapp.objects.Draft
import com.example.customapp.objects.Project

class BlockAdapter(
    private val data: List<Block>,
    private val listener: (String, String)->Unit,
    private val edit: (String, Int)->Unit): RecyclerView.Adapter<BlockAdapter.ViewHolder>() {

    inner class ViewHolder(private val view: View):RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.block_name)
        val edit: Button = view.findViewById(R.id.block_editButton)

        fun bind(block: Block, position: Int){
            name.text = block.name

            edit.setOnClickListener{
                edit("Edit", position)
            }
            view.setOnClickListener{
                listener(block.id, block.name)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).let {
            it.inflate(R.layout.block_row, parent, false) as View
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlockAdapter.ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, position)
    }


    override fun getItemCount() = data.size
}