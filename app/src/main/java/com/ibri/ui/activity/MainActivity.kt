package com.ibri.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ibri.R
import com.ibri.databinding.ActivityMainBinding
import com.ibri.utils.ConnectionLiveData

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var connectionLiveData: ConnectionLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        checkNetwork()
        setContentView(binding.root)
    }



    private fun checkNetwork() {
        connectionLiveData = ConnectionLiveData(this)
        connectionLiveData.isNetworkAvailable()

        connectionLiveData.observe(this) {
            if (it) {
                binding.noInternetCard.visibility = View.GONE
            } else {
                binding.noInternetCard.visibility = View.VISIBLE
            }
        }
    }

}