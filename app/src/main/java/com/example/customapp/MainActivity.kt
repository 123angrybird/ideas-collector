package com.example.customapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ProjectAdapter
    private lateinit var data: MutableList<Project>
    private val db = Firebase.firestore
    private lateinit var projectList: RecyclerView
    private lateinit var item: SwipeToDeleteProject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // project list
        init()


        // Add new project item
        val fab = findViewById<FloatingActionButton>(R.id.add_project_item)
        fab.setOnClickListener{
            showDialog("Add", data.size)
        }

        //swipe to delete item
        item = object : SwipeToDeleteProject(this, 0, ItemTouchHelper.RIGHT){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                showAlert(viewHolder.adapterPosition)
            }
        }
    }

    private fun showDialog(action: String, position: Int) {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.add_edit_project, null)
        val editText = view.findViewById<EditText>(R.id.add_edit)
        editText.hint = getString(R.string.project_name)

        val text = if (action == "Edit") data[position].name else ""
        editText.setText(text)

        with(builder) {
            setTitle(action)
            setPositiveButton("OK", null)
            setNegativeButton("Cancel", null)
            setView(view)
        }

        val dialog = builder.show()

        dialog.apply {
            getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (editText.text.isBlank()) {
                    view.findViewById<TextInputLayout>(R.id.add_edit_layout).let {
                        it.error = resources.getString(R.string.empty_input)
                        editText.focusable
                    }
                } else {
                    if (action == "Add") add(editText.text.toString())
                    else edit(editText.text.toString(), position)
                    dialog.dismiss()
                }
            }

            getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener{
                dialog.dismiss()
            }
        }

    }

    private fun add(name: String){
        val firebaseData = hashMapOf(
            "name" to name
        )

        db.collection("projects")
            .add(firebaseData)
            .addOnSuccessListener { documentReference ->
                Log.d("Firebase Data", "DocumentSnapshot written with ID: ${documentReference.id}")
                data.add(Project(documentReference.id, name))
                adapter.notifyItemInserted(data.size-1)
                Toast.makeText(this, "The project has been added", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w("Firebase Data", "Error adding document", e)
                Toast.makeText(this, "Unable to add the project", Toast.LENGTH_SHORT).show()
            }
    }

    private fun edit(name: String, position: Int){
        val firebaseData = hashMapOf(
            "name" to name
        )
        db.collection("projects").document(data[position].id)
            .set(firebaseData)
            .addOnSuccessListener { documentReference ->
                Log.d("Firebase Data", "DocumentSnapshot successfully written!")
                Toast.makeText(this, "The project has been changed", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w("Firebase Data", "Error adding document", e)
                Toast.makeText(this, "Unable to change the project", Toast.LENGTH_SHORT).show()
            }

        data[position].name = name
        adapter.notifyItemChanged(position)
    }

    //delete
    private fun showAlert(position: Int){
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.alert_layout, null)
        val textView = view.findViewById<TextView>(R.id.delete)

        with(builder){
            setTitle("Delete")
            val message = "Do you want to delete \"${data[position].name}\"?"
            textView.text = message
            setPositiveButton("Delete"){ _, _ ->
                deleteProject(position)
            }
            setNegativeButton("Cancel"){ _, _ ->
                adapter.notifyItemChanged(position)
            }
            setOnCancelListener {
                adapter.notifyItemChanged(position)
            }
            setView(view)
            show()
        }

    }

    private fun deleteProject(position: Int){
        db.collection("projects").document(data[position].id)
            .delete()
            .addOnSuccessListener {
                Log.d("Firebase Data", "DocumentSnapshot successfully deleted!")
                Toast.makeText(this, "The project has been deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w("Firebase Data", "Error deleting document", e)
                Toast.makeText(this, "Unable to delete the project", Toast.LENGTH_SHORT).show()
            }


        data.removeAt(position)
        adapter.notifyDataSetChanged()
    }

    private fun callIdeas(projectId: String, projectName: String){
        val intent = Intent(this, IdeasActivity::class.java)
        intent.apply {
            putExtra("Project id", projectId)
            putExtra("Project name", projectName)
        }

        startActivity(intent)
    }

    private fun init(){
        data = mutableListOf()

        db.collection("projects")
            .get()
            .addOnSuccessListener { result ->
                for (document in result){
                    this.data.add(Project(document.id, document.data["name"] as String, ))
                    projectList = findViewById(R.id.project_list)
                    adapter = ProjectAdapter(data, ::callIdeas, ::showDialog)
                    projectList.adapter = adapter
                    ItemTouchHelper(item).attachToRecyclerView(projectList)
                }
            }
            .addOnFailureListener {
                Log.e("Firebase Data", "Error getting documents $it")
            }
    }
}