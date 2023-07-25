package com.example.customapp.fragment.block.blockideas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.customapp.R
import com.example.customapp.adapter.BlockIdeasAdapter
import com.example.customapp.objects.Idea
import com.example.customapp.objects.SpinnerReference
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BlockIdeas: AppCompatActivity() {

    private lateinit var adapter: BlockIdeasAdapter
    private var data = mutableListOf<Idea>()
    private var blockId: String? = null
    private var projectId: String? = null
    private val db = Firebase.firestore
    private lateinit var empty: TextView
    private lateinit var blockIdeasList: RecyclerView
    private var location = -1
    private lateinit var spinner: ArrayList<SpinnerReference>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.block_ideas_activity)
        empty = findViewById(R.id.empty_block_ideas)

        projectId = intent.getStringExtra("Project id")
        blockId = intent.getStringExtra("Block id")
        val blockName = intent.getStringExtra("Block name")
        spinner = getData(projectId!!)

        val label = findViewById<TextView>(R.id.block_ideas_label)
        label.text = blockName

        blockIdeasList = findViewById(R.id.block_ideas_list)
        adapter = BlockIdeasAdapter(data){ item, position ->
            editBlockIdeas(item, position)
        }
        blockIdeasList.adapter = adapter
        init()

        val fab = findViewById<FloatingActionButton>(R.id.block_ideas_add)
        fab.setOnClickListener {
            addBlockIdeas()
        }


    }

    private fun emptyList(){
        empty.visibility = if (data.isEmpty())  View.VISIBLE else View.GONE
    }

    private fun init(){

        db.collection("ideas").whereEqualTo("bid", blockId).orderBy("date", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result){
                    data.add(
                        Idea(
                            blockId!!,
                            document.id,
                            document.data["text"] as String,
                            document.data["type"].toString().toInt(),
                            document.data["option"].toString().toInt(),
                        ))
                    adapter.notifyItemInserted(data.size-1)
                }
                emptyList()
            }
            .addOnFailureListener {
                Log.e("Firebase Data", "Error getting documents $it")
            }
    }

    private fun editBlockIdeas(item: Idea, position: Int){
        location = position

        val intentToAddOrEdit = Intent(this, AddEditIdeas::class.java)
        intentToAddOrEdit.apply {
            putExtra("action", "edit")
            putExtra("item", item)
            putExtra("Spinner", spinner)
        }
        getResult.launch(intentToAddOrEdit)
    }

    private fun addBlockIdeas(){
        location = data.size
        val intentToAddOrEdit = Intent(this, AddEditIdeas::class.java)
        intentToAddOrEdit.apply {
            putExtra("action", "add")
            putExtra("Spinner", spinner)
        }
        getResult.launch(intentToAddOrEdit)
    }

    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        when (it.resultCode){
            RESULT_OK -> {
                val result = it.data
                val action = result?.getStringExtra("action")
                val itemResult = result?.getParcelableExtra<Idea>("item")

                when (action){
                    "add" ->{
                        val item = itemResult!!
                        val a = hashMapOf(
                            "bid" to blockId,
                            "text" to item.text,
                            "type" to item.type,
                            "option" to item.option,
                            "date" to FieldValue.serverTimestamp()
                        )
                        db.collection("ideas")
                            .add(a)
                            .addOnSuccessListener { it1 ->
                                data.add(Idea(blockId!!, it1.id, item.text, item.type, item.option))
                                adapter.notifyItemInserted(location)
                                emptyList()
                                Toast.makeText(this, "The idea has been added", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Unable to add the idea", Toast.LENGTH_SHORT).show()
                            }
                    }
                    "edit" ->{
                        val item = itemResult!!
                        val a = db.collection("ideas").document(data[location].id)
                        a.apply{
                            update("text", item.text)
                            update("type", item.type)
                            update("option", item.option)
                        }

                        data[location].apply {
                            text = item.text
                            type = item.type
                            option = item.option
                        }
                        Toast.makeText(this, "The idea has been changed", Toast.LENGTH_SHORT).show()
                        adapter.notifyItemChanged(location)
                    }
                    "delete" ->{
                        db.collection("ideas").document(data[location].id)
                            .delete()
                            .addOnFailureListener {
                                Toast.makeText(this, "Unable to delete the idea", Toast.LENGTH_SHORT).show()
                            }
                        data.removeAt(location)
                        adapter.notifyDataSetChanged()
                        emptyList()
                        Toast.makeText(this, "The idea has been deleted", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }

    private fun getData(projectId: String): ArrayList<SpinnerReference>{
        val data = arrayListOf<SpinnerReference>()

        val db = Firebase.firestore

        data.add(SpinnerReference("", "None", "", 0))

        db.collection("drafts").whereEqualTo("pid",projectId)
            .get()
            .addOnSuccessListener {
                for (document in it){
                    data.add(
                        SpinnerReference(
                            "draft",
                            document.data["name"].toString(),
                            document.data["text"].toString(),
                            0))
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firebase Data", "Error adding document", e)
            }

        db.collection("internetideas").whereEqualTo("pid",projectId)
            .get()
            .addOnSuccessListener {
                for (document in it){
                    val option = document.data["option"].toString().toInt()
                    val text = if (option == Idea.QUOTE) document.data["text"].toString() else document.data["paraphrase"].toString()
                    data.add(
                        SpinnerReference(
                            "source",
                            document.data["name"].toString(),
                            text,
                            option))
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firebase Data", "Error adding document", e)
            }

        return data
    }

}