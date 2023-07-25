package com.example.customapp.fragment.source

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.customapp.R
import com.example.customapp.objects.Source

class AddEditSource: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_edit_source_activity)


        val action = intent.getStringExtra("action")
        val label = findViewById<TextView>(R.id.add_edit_source_label)
        val titleET = findViewById<EditText>(R.id.source_input_title)
        val authorET = findViewById<EditText>(R.id.source_input_author)
        val yearET = findViewById<EditText>(R.id.source_input_year)
        val saveButton = findViewById<Button>(R.id.save_edit_button_source)
        val deleteButton = findViewById<Button>(R.id.delete_button_source)

        if (action == "add"){

            label.text = getString(R.string.add)
            saveButton.text = getString(R.string.add)
            deleteButton.visibility = View.GONE

            saveButton.setOnClickListener{
                if (checkInput(titleET, authorET, yearET)){
                    returnItem("add", titleET.text.toString(), authorET.text.toString(), yearET.text.toString().toInt())
                    finish()
                }
            }
        } else {
            val item = intent?.getParcelableExtra<Source>("item")!!

            label.text = getString(R.string.edit)
            titleET.setText(item.title)
            authorET.setText(item.author)
            yearET.setText(item.year.toString())

            saveButton.text = getString(R.string.save)
            deleteButton.visibility = View.VISIBLE

            saveButton.setOnClickListener{
                if (checkInput(titleET, authorET, yearET)){
                    returnItem("edit", titleET.text.toString(), authorET.text.toString(), yearET.text.toString().toInt())
                    finish()
                }
            }

            deleteButton.setOnClickListener{
                delete()
                finish()
            }
        }
    }

    private fun checkInput(titleET: EditText, authorET: EditText, yearET: EditText): Boolean{

        if (titleET.text.isBlank()) {
            titleET.error = "Title cannot be empty!"
            return false
        }

        if (authorET.text.isBlank()) {
            authorET.error = "Author cannot be empty!"
            return false
        }

        if (yearET.text.isBlank()){
            yearET.error = "Year cannot be empty"
            return false
        }

        if (yearET.text.toString().toIntOrNull() == null){
            yearET.error = "Year must be a number"
            return false
        } else {
            if (yearET.text.toString().toInt() !in (1900..2021)){
                yearET.error = "Year must be in the range of 1900 and 2021"
                return false
            }
        }

        return true
    }

    private fun returnItem(action: String, title: String, author: String, year: Int){

        val item = Source("temp", "temp", title, author, year)

        val i = intent.apply {
            putExtra("action", action)
            putExtra("item",item)
        }
        setResult(Activity.RESULT_OK, i)
    }

    private fun delete(){
        val i = intent.apply {
            putExtra("action", "delete")
        }
        setResult(Activity.RESULT_OK, i)
    }
}