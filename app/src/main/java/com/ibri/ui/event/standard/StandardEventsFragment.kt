package com.ibri.ui.event.standard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibri.R
import com.ibri.databinding.FragmentStandardEventsBinding
import com.ibri.model.events.CommercialEvent
import com.ibri.model.events.StandardEvent
import com.ibri.ui.profile.ProfileFragment
import com.ibri.ui.adapters.StandardEventAdapter
import com.ibri.ui.event.SelectedEventListener
import com.ibri.ui.viewmodel.StandardEventViewModel

class StandardEventsFragment : Fragment(), SelectedEventListener {

    private lateinit var binding: FragmentStandardEventsBinding
    private val viewModel: StandardEventViewModel by activityViewModels()
    private lateinit var standardEventAdapter: StandardEventAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStandardEventsBinding.inflate(inflater, container, false)
        viewModel.getStandardEvents()
        setObservableVM()
        setListeners()
        return binding.root
    }

    private fun setObservableVM() {
        viewModel.eventList.observe(viewLifecycleOwner) {
            setRecyclerView(it)
        }
    }

    private fun setListeners() {
        binding.standEventSwipeRefresh.setOnRefreshListener {
            viewModel.getStandardEvents()
            if (binding.standEventSwipeRefresh.isRefreshing)
                binding.standEventSwipeRefresh.isRefreshing = false
        }
    }

    private fun setRecyclerView(it: ArrayList<StandardEvent>) {
        if (it.isNullOrEmpty())
            binding.noEventsCard.visibility = View.VISIBLE
        else
            binding.noEventsCard.visibility = View.GONE

        standardEventAdapter = StandardEventAdapter(requireContext(), this)
        standardEventAdapter.setData(it)
        binding.standEventRecyclerView.adapter = standardEventAdapter
        binding.standEventRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onItemSelected(item: StandardEvent) {
        viewModel.selectedStandardEvent.value = item
        findNavController().navigate(R.id.action_standardEventsFragment_to_standardEventDetail)
    }

    override fun onCreatorSelected(userId: String) {
        val bundle = Bundle()
        bundle.putString(ProfileFragment.USER_ID, userId)
        findNavController().navigate(R.id.action_standardEventsFragment_to_profileFragment2, bundle)
    }

    override fun onItemSelected(item: CommercialEvent) {
    }


}