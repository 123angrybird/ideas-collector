package com.example.customapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.customapp.objects.Project
import com.example.customapp.adapter.ProjectAdapter
import com.example.customapp.gesture.SwipeToDeleteProject
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {
    private lateinit var adapter: ProjectAdapter

    private var data = mutableListOf<Project>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // project list
        data = init()
        val projectList = findViewById<RecyclerView>(R.id.project_list)
        adapter = ProjectAdapter(data, ::callIdeas, ::showDialog)
        projectList.adapter = adapter

        // Add new project item
        val fab = findViewById<FloatingActionButton>(R.id.add_project_item)
        fab.setOnClickListener{
            showDialog("Add", data.size)
        }

        //swipe to delete item
        val item = object : SwipeToDeleteProject(this, 0, ItemTouchHelper.RIGHT){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                showAlert(viewHolder.adapterPosition)
            }
        }
        ItemTouchHelper(item).attachToRecyclerView(projectList)
    }

    private fun showDialog(action: String, position: Int){
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.add_edit_project, null)
        val editText = view.findViewById<EditText>(R.id.add)

        val text = if (action == "Edit") data[position].name else ""
        editText.setText(text)

        with(builder){
            setTitle(action)
            setPositiveButton("OK"){dialog, which ->
                if (action == "Add") add(editText.text.toString(), position)
                else edit(editText.text.toString(), position)
            }
            setNegativeButton("Cancel"){dialog, which ->
            }

            setView(view)
            show()
        }
    }

    private fun add(name: String, position: Int){
        data.add(Project(position, name))
        adapter.notifyItemInserted(position)
    }

    private fun edit(name: String, position: Int){
        data[position].name = name;
        adapter.notifyDataSetChanged()
    }

    //delete
    private fun showAlert(position: Int){
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.alert_layout, null)
        val textView = view.findViewById<TextView>(R.id.delete)

        with(builder){
            setTitle("Delete")
            textView.text = "Do you want to delete ${data[position].name}?"
            setPositiveButton("Delete"){dialog, which ->
                deleteProject(position)
            }
            setNegativeButton("Cancel"){dialog, which ->
                adapter.notifyItemChanged(position)
            }
            setView(view)
            show()
        }

    }

    private fun deleteProject(position: Int){
        data.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

    private fun callIdeas(a: String){
        Toast.makeText(applicationContext, a, Toast.LENGTH_SHORT).show()
        //todo : call Ideas Activity
    }

    private fun init(): MutableList<Project>{
        //todo: get data
        var data = mutableListOf<Project>()

        data.add(Project(1, "IT security"))
        data.add(Project(2, "Software development for mobile device"))
        data.add(Project(3, "Computer System"))
        data.add(Project(4,"Advance Web development"))

        return data
    }
}