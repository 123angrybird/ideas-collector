package com.example.customapp.fragment.source

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import com.example.customapp.R
import com.example.customapp.adapter.SourceAdapter
import com.example.customapp.fragment.source.internetideas.InternetIdeas
import com.example.customapp.objects.Source
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private const val PROJECT_ID = "Project id"
private const val DATA = "Data"

class SourceFragment : Fragment() {

    private lateinit var adapter: SourceAdapter
    private var data = mutableListOf<Source>()
    private val db = Firebase.firestore
    private var projectId: String? = null
    private var location = -1
    private lateinit var sourceList: RecyclerView
    private lateinit var empty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            projectId = it.getString(PROJECT_ID)
            data = it.getParcelableArrayList(DATA)!!
        }
    }

    // main
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_source, container, false)

        empty = view.findViewById(R.id.empty_source)
        emptyList()

        sourceList = view.findViewById(R.id.source_list)
        adapter = SourceAdapter(data, { sourceId, title ->
            callInternetIdeas(sourceId, title, projectId!!)
        }, ::editSource)

        sourceList.adapter = adapter

        val fab = view.findViewById<FloatingActionButton>(R.id.add_source)
        fab.setOnClickListener {
            addSource()
        }

        return view
    }


    //function
    private fun editSource(item: Source, position: Int){
        location = position

        val intentToAddOrEdit = Intent(activity, AddEditSource::class.java)
        intentToAddOrEdit.apply {
            putExtra("action", "edit")
            putExtra("item", item)
        }
        getResult.launch(intentToAddOrEdit)
    }

    private fun addSource(){
        location = data.size
        val intentToAddOrEdit = Intent(activity, AddEditSource::class.java)
        intentToAddOrEdit.apply {
            putExtra("action", "add")
        }
        getResult.launch(intentToAddOrEdit)
    }

    private fun callInternetIdeas(sourceId: String, title: String, projectId: String){
        // Toast.makeText(context.applicationContext, blockId, Toast.LENGTH_SHORT).show()
        val subTitle = if (title.length <= 25) title
                            else title.substring(0,25) + "..."

        val intent = Intent(activity, InternetIdeas::class.java)
        intent.apply {
            putExtra("Project id", projectId)
            putExtra("Source id", sourceId)
            putExtra("Source title", subTitle)
        }

        startActivity(intent)
    }

    private lateinit var getResult: ActivityResultLauncher<Intent>

    override fun onAttach(context: Context) {
        super.onAttach(context)

        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            when (it.resultCode){
                Activity.RESULT_OK -> {
                    val result = it.data
                    val action = result?.getStringExtra("action")
                    val itemResult = result?.getParcelableExtra<Source>("item")

                    when (action){
                        "add" ->{
                            val item = itemResult!!
                            val a = hashMapOf(
                                "pid" to projectId,
                                "title" to item.title,
                                "author" to item.author,
                                "year" to item.year,
                                "date" to FieldValue.serverTimestamp()
                            )
                            db.collection("sources")
                                .add(a)
                                .addOnSuccessListener { it1 ->
                                    data.add(Source(projectId!!, it1.id, item.title, item.author, item.year))
                                    adapter.notifyItemInserted(location)
                                    emptyList()
                                    Toast.makeText(this.context, "The source has been added", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this.context, "Unable to add the source", Toast.LENGTH_SHORT).show()
                                }
                        }
                        "edit" ->{
                            val item = itemResult!!
                            val a = db.collection("sources").document(data[location].id)
                            a.apply {
                                update("title", item.title)
                                update("author", item.author)
                                update("year", item.year)
                            }

                            data[location].apply {
                                this.title = item.title
                                this.author = item.author
                                this.year = item.year
                            }

                            adapter.notifyItemChanged(location)
                            Toast.makeText(this.context, "The source has been changed", Toast.LENGTH_SHORT).show()
                        }
                        "delete" ->{
                            db.collection("sources").document(data[location].id)
                                .delete()
                                .addOnSuccessListener {
                                    Toast.makeText(this.context, "The source has been deleted", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this.context, "Unable to delete the source", Toast.LENGTH_SHORT).show()
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
        fun newInstance(projectId: String, data: ArrayList<Source>) =
            SourceFragment().apply {
                arguments = Bundle().apply {
                    putString(PROJECT_ID, projectId)
                    putParcelableArrayList(DATA, data)
                }
            }
    }
}