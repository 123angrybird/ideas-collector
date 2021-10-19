package com.example.customapp.fragment.source.internetideas

import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.customapp.R
import com.example.customapp.objects.Idea
import com.example.customapp.objects.InternetIdea
import com.example.customapp.objects.Source

class AddEditInternetIdeas: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_edit_internet_ideas)

        val action = intent.getStringExtra("action")
        val projectId = intent.getStringExtra("Project id")

        val label = findViewById<TextView>(R.id.add_edit_internet_ideas_label)
        val nameET = findViewById<EditText>(R.id.name_input_internet_ideas)
        val textET = findViewById<EditText>(R.id.text_input_internet_ideas)
        val switch = findViewById<SwitchCompat>(R.id.paraphrase_add_edit_internet_ideas)
        val paraphraseET = findViewById<EditText>(R.id.paraphrase_input_internet_ideas)
        val saveButton = findViewById<Button>(R.id.save_edit_internet_ideas)
        val deleteButton = findViewById<Button>(R.id.delete_internet_ideas)

        switch.setOnClickListener {
            paraphraseET.visibility = if (switch.isChecked) View.VISIBLE else View.GONE
        }

        if (action == "add"){

            label.text = getString(R.string.add)
            saveButton.text = getString(R.string.add)
            deleteButton.visibility = View.GONE

            saveButton.setOnClickListener{
                if (checkInput(nameET, textET, switch, paraphraseET)){
                    returnItem("add", nameET.text.toString(), textET.text.toString(), switch, paraphraseET.text.toString())
                    finish()
                }
            }
        } else {
            val item = intent?.getParcelableExtra<InternetIdea>("item")!!

            label.text = getString(R.string.edit)
            nameET.setText(item.name)
            textET.setText(item.text)
            paraphraseET.setText(item.paraphrase)
            switch.isChecked = item.option == Idea.PARAPHRASE
            paraphraseET.visibility = if (switch.isChecked) View.VISIBLE else View.GONE

            saveButton.text = getString(R.string.save)
            deleteButton.visibility = View.VISIBLE

            saveButton.setOnClickListener{
                if (checkInput(nameET, textET, switch, paraphraseET)){
                    returnItem("edit", nameET.text.toString(), textET.text.toString(), switch, paraphraseET.text.toString())
                    finish()
                }
            }

            deleteButton.setOnClickListener{
                delete()
                finish()
            }
        }
    }

    private fun checkInput(nameET: EditText, textET: EditText, switch: SwitchCompat, paraphraseET: EditText): Boolean {
        return when{
            nameET.text.isBlank() -> {
                nameET.error = "Name cannot be empty"
                false
            }
            textET.text.isBlank() -> {
                textET.error = "Text cannot be empty"
                false
            }
            switch.isChecked && paraphraseET.text.isBlank() -> {
                paraphraseET.error = "Paraphrase cannot be empty"
                false
            }
            else -> true
        }
    }

    private fun returnItem(action: String, name: String, text: String, switch: SwitchCompat, paraphrase: String = ""){

        val option = if (switch.isChecked) 1 else 2
        val item = InternetIdea("temp", "temp", "temp", name, text, option, paraphrase)

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