package com.ibri.ui.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import com.ibri.R
import com.ibri.databinding.ActivityTagsBinding
import com.ibri.model.Tag
import com.ibri.utils.BASE_URL

class TagsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTagsBinding
    private val ALL_TAGS_ENDPOINT = Uri.parse("${BASE_URL}/tags/all")
    private val selectedTags = ArrayList<Tag>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTagsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prepareStage()
        setListeners()
    }

    private fun prepareStage() {
        binding.tagsBackButton.setOnClickListener { onBackPressed() }
        binding.tagsAddBtn.setOnClickListener { addTags() }
    }

    private fun addTags() {
        if (selectedTags.isNotEmpty()) {
            val intent = Intent()
                .putExtra(TAG_KEY, selectedTags)
            setResult(RESULT_OK, intent)
            finish()
        } else {
            Toast.makeText(this, "Scegli qualche tag", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setListeners() {
        val volley = Volley.newRequestQueue(this)
        val req = StringRequest(
            Request.Method.GET,
            ALL_TAGS_ENDPOINT.toString(),
            {
                setTags(it)
            },
            {
                Toast.makeText(this, "Riprova più tardi", Toast.LENGTH_SHORT).show()
                setResult(RESULT_CANCELED)
                finish()
            }
        )
        volley.add(req)
    }

    private fun setTags(it: String?) {
        val tags = Gson().fromJson(it, Array<Tag>::class.java)
        for (tag in tags) {
            val chip = Chip(this)
            chip.text = tag.name
            chip.isCheckable = true
            chip.setChipBackgroundColorResource(R.color.orange_200)
            chip.setOnCheckedChangeListener { _, isChecked ->
                chipClicked(
                    chip,
                    tag,
                    isChecked
                )
            }
            binding.tagsSelectableChipGroup.addView(chip)
        }
    }


    private fun chipClicked(chip: Chip, tag: Tag, isChecked: Boolean) {
        if (isChecked) {
            if (selectedTags.size < 5) {
                selectedTags.add(tag)
                binding.tagsLimit.text = "${selectedTags.size}/5"
            } else {
                chip.isChecked = false
            }
        } else {
            selectedTags.remove(tag)
            binding.tagsLimit.text = "${selectedTags.size}/5"
        }
    }

    companion object {
        val TAG_KEY = "TAG_KEY"
    }

}
