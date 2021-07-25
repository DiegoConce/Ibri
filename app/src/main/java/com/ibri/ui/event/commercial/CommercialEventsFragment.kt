package com.ibri.ui.event.commercial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ibri.databinding.FragmentCommercialEventsBinding

class CommercialEventsFragment : Fragment() {

    private lateinit var binding: FragmentCommercialEventsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommercialEventsBinding.inflate(inflater, container, false)
        return binding.root
    }
}