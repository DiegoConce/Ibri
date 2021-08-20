package com.ibri.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibri.databinding.LayoutProfileEventsBinding
import com.ibri.model.events.CommercialEvent
import com.ibri.model.events.Event
import com.ibri.model.events.StandardEvent
import com.ibri.ui.adapters.CommercialEventAdapter
import com.ibri.ui.adapters.StandardEventAdapter
import com.ibri.ui.viewmodel.ProfileViewModel
import java.util.*
import kotlin.collections.ArrayList

class ProfileEventsFragment : Fragment() {
    private lateinit var binding: LayoutProfileEventsBinding
    private lateinit var standardEventAdapter: StandardEventAdapter
    private lateinit var commercialEventAdapter: CommercialEventAdapter
    private val viewModel: ProfileViewModel by activityViewModels()
    private val now = Date()
    private val currentEvents = ArrayList<Event>()
    private val pastEvents = ArrayList<Event>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutProfileEventsBinding.inflate(inflater, container, false)
        prepareEvents()
        prepareStage()
        return binding.root
    }

    private fun prepareStage() {
        currentEvents.clear()
        pastEvents.clear()
        arguments?.let {
            when (it.getInt(ARG_STAGE)) {
                0 -> setCurrentEvents()
                1 -> setPastEvents()
                else -> {

                }
            }
        }
    }

    private fun prepareEvents() {
        viewModel.standardEventList.observe(viewLifecycleOwner) {
            for (item in it) {
                if (item.eventDay.before(now))
                    currentEvents.add(item)
                else if (item.eventDay.after(now))
                    pastEvents.add(item)
            }

            when (arguments?.getInt(ARG_STAGE)) {
                0 -> standardEventAdapter.setData(currentEvents.map { event -> event as StandardEvent })
                1 -> standardEventAdapter.setData(pastEvents.map { event -> event as StandardEvent })
            }
        }

        viewModel.comEventList.observe(viewLifecycleOwner) {
            for (item in it) {
                if (item.eventDay.before(now))
                    currentEvents.add(item)
                else if (item.eventDay.after(now))
                    pastEvents.add(item)

                when (arguments?.getInt(ARG_STAGE)) {
                    0 -> commercialEventAdapter.setData(currentEvents.map { event -> event as CommercialEvent })
                    1 -> commercialEventAdapter.setData(pastEvents.map { event -> event as CommercialEvent })

                }
            }
        }

    }

    private fun setCurrentEvents() {
        if (viewModel.isCompany.value == true)
            setCommercialEventsAdapter()
        else if (viewModel.isCompany.value == false)
            setStandardEventsAdapter()
    }

    private fun setPastEvents() {
        if (viewModel.isCompany.value == true)
            setCommercialEventsAdapter()
        else if (viewModel.isCompany.value == false)
            setStandardEventsAdapter()
    }

    private fun setStandardEventsAdapter() {
        standardEventAdapter = StandardEventAdapter(requireContext(), null)
        binding.profileRecyperView.adapter = standardEventAdapter
        binding.profileRecyperView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setCommercialEventsAdapter() {
        commercialEventAdapter = CommercialEventAdapter(requireContext())
        binding.profileRecyperView.adapter = commercialEventAdapter
        binding.profileRecyperView.layoutManager = LinearLayoutManager(requireContext())
    }

    companion object {
        const val ARG_STAGE = "ARG_STAGE"

        @JvmStatic
        fun newInstance(position: Int): Fragment {
            val args = Bundle()
            args.putInt(ARG_STAGE, position)
            val frag = ProfileEventsFragment()
            frag.arguments = args
            return frag
        }
    }
}