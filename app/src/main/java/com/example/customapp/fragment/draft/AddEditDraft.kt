package com.example.customapp.fragment.draft

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.customapp.R
import com.example.customapp.objects.Draft

class AddEditDraft: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_edit_draft_activity)

        val label = findViewById<TextView>(R.id.add_edit_draft_label)
        val inputName = findViewById<EditText>(R.id.name_input_draft)
        val inputText = findViewById<EditText>(R.id.text_input_draft)
        val saveAndEditButton = findViewById<Button>(R.id.save_edit_draft)
        val deleteButton = findViewById<Button>(R.id.delete_draft)

        val action = intent.getStringExtra("action")

        if (action == "add"){

            label.text = getString(R.string.add)
            saveAndEditButton.text = getString(R.string.add)
            deleteButton.visibility = View.GONE

            saveAndEditButton.setOnClickListener{
                if (checkInput(inputName, inputText)){
                    addDraft(inputName.text.toString(), inputText.text.toString())
                    finish()
                }
            }
        } else {
            val item = intent?.getParcelableExtra<Draft>("item")

            label.text = getString(R.string.edit)
            inputName.setText(item?.name)
            inputText.setText(item?.text)

            saveAndEditButton.text = getString(R.string.save)
            deleteButton.visibility = View.VISIBLE

            saveAndEditButton.setOnClickListener{
                if (checkInput(inputName, inputText)){
                    editDraft(inputName.text.toString(), inputText.text.toString())
                    finish()
                }
            }

            deleteButton.setOnClickListener{
                deleteDraft()
                finish()
            }
        }

    }

    private fun checkInput(nameET: EditText, textET: EditText): Boolean{
        var err = true
        if (nameET.text.isBlank()) {
            nameET.error = "Name cannot be empty!"
            err = false
        }
        if (textET.text.isBlank()) {
            textET.error = "Text cannot be empty!"
            err = false
        }

        return err
    }

    private fun addDraft(name: String, text: String){
        val i = intent.apply {
            putExtra("action", "add")
            putExtra("name", name)
            putExtra("text", text)
        }
        setResult(Activity.RESULT_OK, i)
    }

    private fun editDraft(name: String, text: String){
        val i = intent.apply {
            putExtra("action", "edit")
            putExtra("name", name)
            putExtra("text", text)
        }
        setResult(Activity.RESULT_OK, i)
    }

    private fun deleteDraft(){
        val i = intent.apply {
            putExtra("action", "delete")
        }
        setResult(Activity.RESULT_OK, i)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }
}
