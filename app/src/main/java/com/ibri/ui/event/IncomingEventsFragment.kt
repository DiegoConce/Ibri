package com.ibri.ui.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ibri.databinding.FragmentIncomingEventsBinding

class IncomingEventsFragment : Fragment() {

    private lateinit var binding: FragmentIncomingEventsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIncomingEventsBinding.inflate(inflater, container, false)
        return binding.root
    }
}