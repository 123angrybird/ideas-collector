package com.example.customapp.fragment.source.internetideas

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
import com.example.customapp.adapter.InternetIdeasAdapter
import com.example.customapp.fragment.block.blockideas.AddEditIdeas
import com.example.customapp.objects.InternetIdea
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class InternetIdeas: AppCompatActivity() {
    private lateinit var adapter: InternetIdeasAdapter
    private var data = mutableListOf<InternetIdea>()
    private var sourceId: String? = null
    private var projectId: String? = null
    private val db = Firebase.firestore
    private lateinit var empty: TextView
    private lateinit var internetIdeasList: RecyclerView
    private var location = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.internet_ideas_acitvity)
        empty = findViewById(R.id.empty_internet_ideas)

        projectId = intent.getStringExtra("Project id")
        sourceId = intent.getStringExtra("Source id")
        val sourceName = intent.getStringExtra("Source title")

        val label = findViewById<TextView>(R.id.internet_ideas_label)
        label.text = sourceName

        internetIdeasList = findViewById(R.id.internet_ideas_list)
        adapter = InternetIdeasAdapter(data){ item, position ->
            editInternetIdeas(item, position)
        }
        internetIdeasList.adapter = adapter
        init()

        val fab = findViewById<FloatingActionButton>(R.id.internet_ideas_add)
        fab.setOnClickListener {
            addInternetIdeas()
        }


    }

    private fun emptyList(){
        empty.visibility = if (data.isEmpty())  View.VISIBLE else View.GONE
    }

    private fun init(){

        db.collection("internetideas").whereEqualTo("sid", sourceId).orderBy("date", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result){
                    data.add(
                        InternetIdea(
                            sourceId!!,
                            projectId!!,
                            document.id,
                            document.data["name"] as String,
                            document.data["text"] as String,
                            document.data["option"].toString().toInt(),
                            document.data["paraphrase"] as String
                        )
                    )
                    adapter.notifyItemInserted(data.size)
                }
                emptyList()
            }
            .addOnFailureListener {
                Log.e("Firebase Data", "Error getting documents $it")
            }
    }

    private fun editInternetIdeas(item: InternetIdea, position: Int){
        location = position

        val intentToAddOrEdit = Intent(this, AddEditInternetIdeas::class.java)
        intentToAddOrEdit.apply {
            putExtra("action", "edit")
            putExtra("Project id", projectId)
            putExtra("item", item)
        }
        getResult.launch(intentToAddOrEdit)
    }

    private fun addInternetIdeas(){
        location = data.size
        val intentToAddOrEdit = Intent(this, AddEditInternetIdeas::class.java)
        intentToAddOrEdit.apply {
            putExtra("action", "add")
            putExtra("Project id", projectId)
        }
        getResult.launch(intentToAddOrEdit)
    }

    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        when (it.resultCode){
            RESULT_OK -> {
                val result = it.data
                val action = result?.getStringExtra("action")
                val itemResult = result?.getParcelableExtra<InternetIdea>("item")

                when (action){
                    "add" ->{
                        val item = itemResult!!
                        val a = hashMapOf(
                            "sid" to sourceId,
                            "pid" to projectId,
                            "name" to item.name,
                            "text" to item.text,
                            "option" to item.option,
                            "paraphrase" to item.paraphrase,
                            "date" to FieldValue.serverTimestamp()
                        )
                        db.collection("internetideas")
                            .add(a)
                            .addOnSuccessListener { it1 ->
                                data
                                    .add(
                                        InternetIdea(
                                            sourceId!!,
                                            projectId!!,
                                            it1.id,
                                            item.name,
                                            item.text,
                                            item.option,
                                            item.paraphrase)
                                    )
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
                        val a = db.collection("internetideas").document(data[location].id)
                        a.apply{
                            update("name", item.name)
                            update("text", item.text)
                            update("option", item.option)
                            update("paraphrase", item.paraphrase)
                        }

                        data[location].apply {
                            name = item.name
                            text = item.text
                            option = item.option
                            paraphrase = item.paraphrase
                        }

                        adapter.notifyItemChanged(location)
                        Toast.makeText(this, "The idea has been changed", Toast.LENGTH_SHORT).show()
                    }
                    "delete" ->{
                        db.collection("internetideas").document(data[location].id)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "The idea has been deleted", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Unable to delete the idea", Toast.LENGTH_SHORT).show()
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