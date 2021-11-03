package com.example.customapp.fragment.block

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.customapp.R
import com.example.customapp.adapter.BlockAdapter
import com.example.customapp.fragment.block.blockideas.BlockIdeas
import com.example.customapp.gesture.SwipeToDeleteBlock
import com.example.customapp.objects.Block
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


private const val PROJECT_ID = "Project id"
private const val PROJECT_NAME = "Project name"
private const val DATA = "Data"


class BlockFragment : Fragment() {

    private var projectId: String? = null
    private var projectName: String? = null
    private var data = mutableListOf<Block>()
    private lateinit var empty: TextView
    private lateinit var adapter: BlockAdapter
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            data = it.getParcelableArrayList(DATA)!!
            projectId = it.getString(PROJECT_ID)
            projectName = it.getString(PROJECT_NAME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_block, container, false)

        val projectNameTV = view.findViewById<TextView>(R.id.block_project_name)
        val projectTitle = "Project: $projectName"
        projectNameTV.text = projectTitle

        empty = view.findViewById(R.id.empty_block)
        emptyList()

        val blockList = view.findViewById<RecyclerView>(R.id.block_list)
        adapter = BlockAdapter(data,{ blockId, blockName ->
            callBlockIdeas(blockId, blockName, projectId!!)
        }, { name, position->
            showDialog(name, position, view.context)
        })
        blockList.adapter = adapter

        // Add new project item
        val fab = view.findViewById<FloatingActionButton>(R.id.add_block_item)
        fab.setOnClickListener{
            showDialog("Add", data.size, view.context)
        }

        //swipe to delete item
        val item1 = object : SwipeToDeleteBlock(this, 0, ItemTouchHelper.RIGHT){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                showAlert(viewHolder.adapterPosition, view.context)
            }
        }
        ItemTouchHelper(item1).attachToRecyclerView(blockList)
        return view
    }

    private fun emptyList(){
        empty.visibility = if (data.isEmpty())  View.VISIBLE else View.GONE
    }

    private fun showDialog(action: String, position: Int, context: Context) {
        val builder = AlertDialog.Builder(context)
        val view = layoutInflater.inflate(R.layout.add_edit_project, null)
        val editText = view.findViewById<EditText>(R.id.add_edit)

        editText.hint = getString(R.string.block_name)

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
            "pid" to projectId!!,
            "name" to name,
            "date" to FieldValue.serverTimestamp()
        )

        db.collection("blocks")
            .add(firebaseData)
            .addOnSuccessListener { documentReference ->
                Log.d("Firebase Data", "DocumentSnapshot written with ID: ${documentReference.id}")
                data.add(Block(projectId!!, documentReference.id, name))
                adapter.notifyItemInserted(data.size)
                emptyList()
                Toast.makeText(this.context, "The block has been added", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w("Firebase Data", "Error adding document", e)
                Toast.makeText(this.context, "Unable to add the block", Toast.LENGTH_SHORT).show()
            }
    }

    private fun edit(name: String, position: Int){
       db.collection("blocks").document(data[position].id)
            .update("name", name)
            .addOnSuccessListener {
                Log.d("Firebase Data", "DocumentSnapshot successfully written!")
                Toast.makeText(this.context, "The block has been changed", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w("Firebase Data", "Error adding document", e)
                Toast.makeText(this.context, "Unable to change the block", Toast.LENGTH_SHORT).show()
            }

        data[position].name = name
        adapter.notifyItemChanged(position)
        emptyList()
    }

    //delete
    private fun showAlert(position: Int, context: Context){
        val builder = AlertDialog.Builder(context)
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
// copy from Main Activity
    private fun deleteProject(position: Int){
        db.collection("blocks").document(data[position].id)
            .delete()
            .addOnSuccessListener {
                Log.d("Firebase Data", "DocumentSnapshot successfully deleted!")
                Toast.makeText(this.context, "The block has been deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w("Firebase Data", "Error deleting document", e)
                Toast.makeText(this.context, "Unable to delete the block", Toast.LENGTH_SHORT).show()
            }


        data.removeAt(position)
        adapter.notifyDataSetChanged()
        emptyList()
    }

    private fun callBlockIdeas(blockId: String, blockName: String, projectId: String){
       // Toast.makeText(context.applicationContext, blockId, Toast.LENGTH_SHORT).show()

        val intent = Intent(activity, BlockIdeas::class.java)
        intent.apply {
            putExtra("Project id", projectId)
            putExtra("Block id", blockId)
            putExtra("Block name", blockName)
        }

        startActivity(intent)
    }



// Static
    companion object {
        @JvmStatic
        fun newInstance(projectId: String, projectName: String, data: ArrayList<Block>) =
            BlockFragment().apply {
                arguments = Bundle().apply {
                    putString(PROJECT_ID, projectId)
                    putString(PROJECT_NAME, projectName)
                    putParcelableArrayList(DATA, data)
                }
            }
    }
}