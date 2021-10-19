package com.example.customapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.customapp.R
import com.example.customapp.objects.Draft

class DraftAdapter(
    private val data: List<Draft>,
    private val edit: (Draft, Int)->Unit): RecyclerView.Adapter<DraftAdapter.ViewHolder>() {

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.draft_name)
        val text: TextView = view.findViewById(R.id.draft_text)
        val button: Button = view.findViewById(R.id.draft_editButton)
        val details: TextView = view.findViewById(R.id.draft_details)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).let {
            it.inflate(R.layout.draft_row, parent, false) as View

        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DraftAdapter.ViewHolder, position: Int) {
        val item = data[position]

        val subText = if (item.text.length < 10) item.text
                        else item.text.subSequence(0, 10).toString() + "..."
        val detail = "Text:  " + item.text

        holder.apply {
            val itemName = item.name + ":"
            name.text = itemName
            text.text = subText
            details.text = detail
            details.visibility = if (item.expand) View.VISIBLE else View.GONE
            text.visibility = if (!item.expand) View.VISIBLE else View.GONE

            button.setOnClickListener{
                edit(item, position)
            }
        }

        holder.itemView.setOnClickListener {
            item.expand = !item.expand
            holder.details.visibility = if (item.expand) View.VISIBLE else View.GONE
            holder.text.visibility = if (!item.expand) View.VISIBLE else View.GONE

            notifyItemChanged(position)
        }
    }


    override fun getItemCount() = data.size
}