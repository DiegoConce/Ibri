package com.ibri.ui.event.commercial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibri.databinding.FragmentCommercialEventsBinding
import com.ibri.model.events.CommercialEvent
import com.ibri.ui.adapters.CommercialEventAdapter
import com.ibri.ui.viewmodel.CommercialEventViewModel

class CommercialEventsFragment : Fragment() {

    private lateinit var binding: FragmentCommercialEventsBinding
    private val viewModel: CommercialEventViewModel by activityViewModels()
    private lateinit var commercialEventAdapter: CommercialEventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommercialEventsBinding.inflate(inflater, container, false)
        viewModel.getCommercialEvents()
        setObservableVM()
        setListeners()
        return binding.root
    }

    private fun setObservableVM() {
        viewModel.comEventList.observe(viewLifecycleOwner) {
            setRecyclerView(it)
        }
    }

    private fun setListeners() {
        binding.comEventSwipeRefresh.setOnRefreshListener {
            viewModel.getCommercialEvents()
            if (binding.comEventSwipeRefresh.isRefreshing)
                binding.comEventSwipeRefresh.isRefreshing = false
        }
    }

    private fun setRecyclerView(it: ArrayList<CommercialEvent>) {
        if (it.isNullOrEmpty())
            binding.noComEventsCard.visibility = View.VISIBLE
        else
            binding.noComEventsCard.visibility = View.GONE

        commercialEventAdapter = CommercialEventAdapter(requireContext())
        commercialEventAdapter.setData(it)
        binding.comEventRecyclerView.adapter = commercialEventAdapter
        binding.comEventRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}