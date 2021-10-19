package com.example.customapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.customapp.R
import com.example.customapp.objects.Source

class SourceAdapter(
    private val data: List<Source>,
    val listener: (String, String) -> Unit,
    val edit: (Source, Int) -> Unit): RecyclerView.Adapter<SourceAdapter.ViewHolder>(){

    inner class ViewHolder(private val view: View): RecyclerView.ViewHolder(view){
        private val year: TextView = view.findViewById(R.id.source_year)
        private val title:TextView = view.findViewById(R.id.source_title)
        private val author: TextView = view.findViewById(R.id.source_author)
        private val editBT: Button = view.findViewById(R.id.source_editButton)

        fun bind(item: Source, position: Int){
            val authorText = "Author: " + item.author

            year.text = item.year.toString()
            title.text = item.title
            author.text = authorText

            view.setOnClickListener {
                listener(item.id, item.title)
            }
            editBT.setOnClickListener {
                edit(item, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).let {
            it.inflate(R.layout.source_row, parent, false) as View
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, position)
    }

    override fun getItemCount() = data.size
}