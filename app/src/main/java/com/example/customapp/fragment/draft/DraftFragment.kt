package com.example.customapp.fragment.draft

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.customapp.R
import com.example.customapp.adapter.DraftAdapter
import com.example.customapp.objects.Draft
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private const val PROJECT_ID = "Project id"
private const val DATA = "Data"

class DraftFragment : Fragment() {

    private lateinit var adapter: DraftAdapter
    private var data = mutableListOf<Draft>()
    private val db = Firebase.firestore
    private var projectId: String? = null
    private var location = -1
    private lateinit var draftList: RecyclerView
    private lateinit var empty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            projectId = it.getString(PROJECT_ID)
            data = it.getParcelableArrayList(DATA)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_draft, container, false)

        empty = view.findViewById(R.id.empty_draft)
        emptyList()

        draftList = view.findViewById(R.id.draft_list)
        adapter = DraftAdapter(data){ item, position ->
            editDraft(item, position)
        }
        draftList.adapter = adapter

        val fab = view.findViewById<FloatingActionButton>(R.id.add_draft)
        fab.setOnClickListener{
            addDraft()
        }

        return view
    }

    private fun editDraft(item: Draft, position: Int){
        location = position

        val intentToAddOrEdit = Intent(activity, AddEditDraft::class.java)
        intentToAddOrEdit.apply {
            putExtra("action", "edit")
            putExtra("item", item)
        }

        getResult.launch(intentToAddOrEdit)
    }

    private fun addDraft(){
        location = data.size
        val intentToAddOrEdit = Intent(activity, AddEditDraft::class.java)
        intentToAddOrEdit.apply {
            putExtra("action", "add")
        }
        getResult.launch(intentToAddOrEdit)
    }

    private lateinit var getResult: ActivityResultLauncher<Intent>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            when (it.resultCode){

                RESULT_OK -> {
                    val result = it.data
                    val action = result?.getStringExtra("action")
                    val name = result?.getStringExtra("name")
                    val text = result?.getStringExtra("text")

                    when (action){
                        "add" ->{
                            val a = hashMapOf(
                                "pid" to projectId,
                                "name" to name,
                                "text" to text,
                                "date" to FieldValue.serverTimestamp()
                            )
                            db.collection("drafts")
                                .add(a)
                                .addOnSuccessListener { it1 ->
                                    data.add(Draft(projectId!!, it1.id, name!!, text!!))
                                    adapter.notifyItemInserted(location)
                                    emptyList()
                                    Toast.makeText(this.context, "The draft has been added", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this.context, "Unable to delete the draft", Toast.LENGTH_SHORT).show()
                                }
                        }
                        "edit" ->{
                            val a = db.collection("drafts").document(data[location].id)
                            a.update("name", name)
                            a.update("text", text)
                            Log.i("Get result", "result in: name - $name, text - $text")
                            data[location].name = name.toString()
                            data[location].text = text.toString()

                            adapter.notifyItemChanged(location)
                            Toast.makeText(this.context, "The draft has been changed", Toast.LENGTH_SHORT).show()
                        }
                        "delete" ->{
                            db.collection("drafts").document(data[location].id)
                                .delete()
                                .addOnSuccessListener {
                                    Toast.makeText(this.context, "The draft has been deleted", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this.context, "Unable to delete the draft", Toast.LENGTH_SHORT).show()
                                }
                            data.removeAt(location)
                            adapter.notifyDataSetChanged()
                            emptyList()
                        }
                    }

                }
            }
        }
    }

    private fun emptyList(){
        empty.visibility = if (data.isEmpty())  View.VISIBLE else View.GONE
    }

    companion object {
        @JvmStatic
        fun newInstance(projectId: String, data: ArrayList<Draft>) =
            DraftFragment().apply {
                arguments = Bundle().apply {
                    putString(PROJECT_ID, projectId)
                    putParcelableArrayList(DATA, data)
                }
            }
    }

}