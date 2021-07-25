package com.ibri.ui.event.standard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ibri.databinding.FragmentStandardEventDetailBinding
import com.ibri.ui.viewmodel.StandardEventViewModel

class StandardEventDetailFragment : Fragment() {

    private lateinit var binding: FragmentStandardEventDetailBinding
    private val viewModel: StandardEventViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStandardEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
}