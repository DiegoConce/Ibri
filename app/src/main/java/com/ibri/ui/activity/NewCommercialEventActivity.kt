package com.ibri.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ibri.databinding.ActivityNewCommercialEventBinding

class NewCommercialEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewCommercialEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewCommercialEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}