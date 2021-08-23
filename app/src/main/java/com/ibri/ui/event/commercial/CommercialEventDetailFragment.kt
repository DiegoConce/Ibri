package com.ibri.ui.event.commercial

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ibri.databinding.FragmentCommercialEventDetailBinding
import com.ibri.model.events.CommercialEvent
import com.ibri.model.events.StandardEvent
import com.ibri.ui.viewmodel.CommercialEventViewModel
import com.ibri.utils.PreferenceManager

class CommercialEventDetailFragment : Fragment() {

    private lateinit var binding: FragmentCommercialEventDetailBinding
    private lateinit var pref: SharedPreferences

    private val viewModel: CommercialEventViewModel by activityViewModels()
    private var topBarAnimationFinished: Boolean = true
    private var topBarHidden: Boolean = false

    private lateinit var comEvent: CommercialEvent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommercialEventDetailBinding.inflate(inflater, container, false)
        pref = PreferenceManager.getSharedPreferences(requireContext())

        setObservableVM()
        setListeners()
        return binding.root
    }

    private fun setObservableVM() {

    }

    private fun setListeners() {

    }
}