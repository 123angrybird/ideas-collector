package com.example.customapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.customapp.R
import com.example.customapp.objects.Idea

class BlockIdeasAdapter(
    val data: List<Idea>,
    val edit: (Idea, Int)->Unit): RecyclerView.Adapter<BlockIdeasAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val typeTV = view.findViewById<TextView>(R.id.block_ideas_type)
        val textTV = view.findViewById<TextView>(R.id.block_ideas_text)
        val editBT = view.findViewById<Button>(R.id.block_ideas_editButton)
        val expansionRL = view.findViewById<RelativeLayout>(R.id.block_ideas_expansion)
        val optionTV = view.findViewById<TextView>(R.id.block_ideas_option)
        val detailTV = view.findViewById<TextView>(R.id.block_ideas_details)

        fun bind(item: Idea){
            val subText = if (item.text.length < 25) item.text
                                else item.text.subSequence(0, 25).toString() + "..."
            val typeText = Idea.convertTypeToString(item.type) + "->"


            typeTV.text = typeText

            textTV.apply {
                text = subText
                visibility = if (item.expand) View.GONE else View.VISIBLE
            }

            optionTV.apply {
                text = when (item.option) {
                    Idea.PARAPHRASE -> "Paraphrase"
                    Idea.QUOTE -> "Quote"
                    else -> "Own Idea"
                }
                visibility = if (item.option == Idea.OWN_IDEA) View.GONE else View.VISIBLE
            }

            detailTV.text = item.text

            expansionRL.visibility = if (item.expand) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).let {
            it.inflate(R.layout.block_ideas_row, parent, false) as View
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        holder.bind(item)
        holder.editBT.setOnClickListener{
            edit(item, position)
        }

        holder.itemView.setOnClickListener {
            item.expand = !item.expand

            holder.expansionRL.visibility = if (item.expand) View.VISIBLE else View.GONE
            holder.textTV.visibility = if (!item.expand) View.VISIBLE else View.GONE

            notifyItemChanged(position)
        }
    }

    override fun getItemCount() = data.size
}