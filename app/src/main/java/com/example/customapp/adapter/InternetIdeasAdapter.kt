package com.example.customapp.adapter

import android.view.LayoutInflater
import android.content.res.Resources.getSystem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.customapp.R
import com.example.customapp.fragment.source.internetideas.InternetIdeas
import com.example.customapp.objects.Idea
import com.example.customapp.objects.InternetIdea

class InternetIdeasAdapter(
    private val data: List<InternetIdea>,
    val edit: (InternetIdea, Int) -> Unit): RecyclerView.Adapter<InternetIdeasAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view){
        private val name: TextView = view.findViewById(R.id.internet_ideas_name)
        private val option: TextView = view.findViewById(R.id.internet_ideas_option)
        val text: TextView = view.findViewById(R.id.internet_ideas_text)
        val details: TextView = view.findViewById(R.id.internet_ideas_details)
        private val editButton: Button = view.findViewById(R.id.internet_ideas_editButton)

        fun bind(item: InternetIdea, position: Int){

            name.text = item.name
            option.text = when (item.option){
                1 -> "Paraphrase"
                else -> "Quote"
            }
            val color =  view.resources.getColorStateList(R.color.black)
            name.setTextColor(color)
            details.text = if (item.option == Idea.QUOTE) item.text else item.paraphrase
            val subText = if (details.text.length < 28) details.text
                            else details.text.subSequence(0, 28).toString() + "..."
            text.text = subText

            if (item.expand){
                text.visibility = View.GONE
                details.visibility = View.VISIBLE
            } else {
                text.visibility = View.VISIBLE
                details.visibility = View.GONE
            }

            editButton.setOnClickListener {
                edit(item, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).let {
            it.inflate(R.layout.internet_ideas_row, parent, false) as View
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, position)

        holder.itemView.setOnClickListener {
            item.expand = !item.expand

            holder.details.visibility = if (item.expand) View.VISIBLE else View.GONE
            holder.text.visibility = if (!item.expand) View.VISIBLE else View.GONE

            notifyItemChanged(position)
        }
    }

    override fun getItemCount() = data.size
}