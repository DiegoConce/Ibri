package com.ibri.ui.event.standard

import android.os.Bundle
import android.util.Log
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
import com.ibri.ui.adapters.StandardEventAdapter
import com.ibri.ui.event.SelectedItemListener
import com.ibri.ui.viewmodel.StandardEventViewModel
import com.ibri.utils.LOG_TEST

class StandardEventsFragment : Fragment(), SelectedItemListener {

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

        return binding.root
    }

    private fun setObservableVM() {
        viewModel.eventList.observe(viewLifecycleOwner) {
            setRecyclerView(it)
        }

    }

    private fun setRecyclerView(it: ArrayList<StandardEvent>) {

        if (it.isNullOrEmpty())
            binding.noEventsCard.visibility = View.VISIBLE
        else
            binding.noEventsCard.visibility = View.INVISIBLE

        standardEventAdapter = StandardEventAdapter(requireContext(),this)
        standardEventAdapter.setData(it)
        binding.standEventRecyclerView.adapter = standardEventAdapter
        binding.standEventRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onItemSelected(item: StandardEvent) {
        viewModel.selectedStandardEvent.value = item

        Log.wtf(LOG_TEST,"item: $item")
        Log.wtf(LOG_TEST,"curr " + findNavController().currentDestination.toString())
        findNavController().navigate(R.id.action_standardEventsFragment_to_standardEventDetail)
    }

    override fun onItemSelected(item: CommercialEvent) {
    }


}