package com.example.customapp.fragment.block.blockideas

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.customapp.R
import com.example.customapp.objects.Idea
import com.example.customapp.objects.SpinnerReference

class AddEditIdeas:AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var text: EditText
    private var reference: ArrayList<SpinnerReference>? = null
    private var option: Int = 0
    private var start = true
    private lateinit var ownIdeaRB: RadioButton
    private lateinit var quoteRB: RadioButton
    private lateinit var paraphraseRB: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_edit_block_ideas_activity)

        val action = intent.getStringExtra("action")
        reference = intent.getParcelableArrayListExtra("Spinner")

        val label = findViewById<TextView>(R.id.add_edit_block_ideas_label)
        val typeSpinner = findViewById<Spinner>(R.id.type_spinner_block_ideas)
        val referenceSpinner = findViewById<Spinner>(R.id.reference_spinner_block_ideas)
        text = findViewById(R.id.text_input_block_ideas)
        val saveButton = findViewById<Button>(R.id.save_edit_button_block_ideas)
        val deleteButton = findViewById<Button>(R.id.delete_button_block_ideas)
        ownIdeaRB = findViewById(R.id.own_idea_block_ideas)
        quoteRB = findViewById(R.id.quote_block_ideas)
        paraphraseRB = findViewById(R.id.paraphrase_block_ideas)

        val str = arrayListOf<String>()
        for (i in reference!!){
            val j = if (i.name == "None") "None" else i.what + ": " + i.name
            str.add(j)
        }
        val ad: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this,
            android.R.layout.simple_spinner_item,
            str.toArray()
        )
        ad.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item)

        referenceSpinner.adapter = ad
        referenceSpinner.onItemSelectedListener = this

        if (action == "add"){

            label.text = getString(R.string.add)
            saveButton.text = getString(R.string.add)
            deleteButton.visibility = View.GONE

            saveButton.setOnClickListener{
                if (checkInput(text)){
                    option = when{
                        paraphraseRB.isChecked -> 1
                        quoteRB.isChecked -> 2
                        else -> 0
                    }
                    returnItem("add", Idea.convertTypeToValue(typeSpinner.selectedItem.toString()), option, text.text.toString())
                    finish()
                }
            }
        } else {
            val item = intent?.getParcelableExtra<Idea>("item")!!

            label.text = getString(R.string.edit)
            typeSpinner.setSelection(item.type)
            when (item.option) {
                0 -> ownIdeaRB.isChecked = true
                1 -> paraphraseRB.isChecked = true
                2 -> quoteRB.isChecked = true
            }
            referenceSpinner.setSelection(0)
            text.setText(item.text)

            saveButton.text = getString(R.string.save)
            deleteButton.visibility = View.VISIBLE

            saveButton.setOnClickListener{
                if (checkInput(text)){
                    option = when{
                        paraphraseRB.isChecked -> 1
                        quoteRB.isChecked -> 2
                        else -> 0
                    }
                    returnItem("edit", Idea.convertTypeToValue(typeSpinner.selectedItem.toString()), option, text.text.toString())
                    finish()
                }
            }

            deleteButton.setOnClickListener{
                delete()
                finish()
            }
        }

    }

    private fun checkInput(textET: EditText): Boolean{
        return if (textET.text.isBlank()){
            textET.error = "Text cannot be empty!"
            false
        } else true
    }

    private fun returnItem(action: String, type: Int = 0, option: Int, text: String){

        val item = Idea("temp", "temp", text, type, option)

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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (!start){
            text.setText(reference?.get(position)?.text)
            option = reference?.get(position)?.option!!
            when(option){
                0 -> ownIdeaRB.isChecked = true
                1 -> paraphraseRB.isChecked = true
                2 -> quoteRB.isChecked = true
            }
        } else start = false
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

}