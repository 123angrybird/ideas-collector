package com.example.customapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.OnReceiveContentListener
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.customapp.R
import com.example.customapp.objects.Project

class ProjectAdapter(
    private val data: List<Project>,
    private val listener: (String, String)->Unit,
    private val edit: (String, Int)->Unit): RecyclerView.Adapter<ProjectAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).let {
            it.inflate(R.layout.project_row, parent, false) as View
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(data[position], position)

    }


    override fun getItemCount() = data.size

    inner class ViewHolder(private val view: View): RecyclerView.ViewHolder(view){
        private val name = view.findViewById<TextView>(R.id.project_name)
        val edit = view.findViewById<Button>(R.id.project_editButton)

        fun bind(project: Project, position: Int){
            name.text = project.name

            view.setOnClickListener{
                listener(project.id, project.name)
            }
            edit.setOnClickListener{
                edit("Edit", position)
            }
        }

    }
}