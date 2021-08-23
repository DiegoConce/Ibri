package com.ibri.ui.event

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibri.R
import com.ibri.databinding.FragmentSearchDetailBinding
import com.ibri.model.events.CommercialEvent
import com.ibri.model.events.StandardEvent
import com.ibri.ui.adapters.CommercialEventAdapter
import com.ibri.ui.adapters.EventsOnClickListener
import com.ibri.ui.adapters.StandardEventAdapter
import com.ibri.ui.profile.ProfileFragment
import com.ibri.ui.viewmodel.CommercialEventViewModel
import com.ibri.ui.viewmodel.SearchViewModel
import com.ibri.ui.viewmodel.StandardEventViewModel

class SearchDetailFragment : Fragment(), EventsOnClickListener {
    private lateinit var binding: FragmentSearchDetailBinding
    private lateinit var standardEventAdapter: StandardEventAdapter
    private lateinit var commercialEventAdapter: CommercialEventAdapter
    private val viewModel: SearchViewModel by activityViewModels()
    private val standEventVM: StandardEventViewModel by activityViewModels()
    private val comEventVM: CommercialEventViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchDetailBinding.inflate(inflater, container, false)
        standardEventAdapter = StandardEventAdapter(requireContext(), this)
        commercialEventAdapter = CommercialEventAdapter(requireContext(), this)
        prepareStage()
        setObservableVM()
        setListeners()
        return binding.root
    }

    private fun setObservableVM() {
        standEventVM.eventList.observe(viewLifecycleOwner) {
            standardEventAdapter.setData(it)
        }
        comEventVM.comEventList.observe(viewLifecycleOwner) {
            commercialEventAdapter.setData(it)
        }
    }

    private fun setListeners() {
        binding.searchDetailBackButton.setOnClickListener { requireActivity().onBackPressed() }
        binding.clearSearch.setOnClickListener { binding.searchQuery.text.clear() }
        binding.searchQuery.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (viewModel.isStandardEvent.value == true)
                    standardEventAdapter.filter(s.toString())
                else
                    commercialEventAdapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

    }

    private fun prepareStage() {
        binding.searchEventRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        if (viewModel.isStandardEvent.value == true) {
            binding.searchEventRecyclerView.adapter = standardEventAdapter
            standEventVM.filterListByTag(viewModel.selectedTag.value!!)

            if (standEventVM.eventList.value.isNullOrEmpty())
                binding.noEventsCard.visibility = View.VISIBLE
            else
                binding.noEventsCard.visibility = View.GONE

        } else {
            binding.searchEventRecyclerView.adapter = commercialEventAdapter
            comEventVM.filterListByTag(viewModel.selectedTag.value!!)

            if (comEventVM.comEventList.value.isNullOrEmpty())
                binding.noEventsCard.visibility = View.VISIBLE
            else
                binding.noEventsCard.visibility = View.GONE
        }

    }

    override fun onItemSelected(item: StandardEvent) {
        standEventVM.selectedStandardEvent.value = item
        findNavController().navigate(SearchDetailFragmentDirections.actionSearchDetailFragmentToNavStandEvent())
    }

    override fun onItemSelected(item: CommercialEvent) {
        comEventVM.selectedCommercialEvent.value = item
        findNavController().navigate(SearchDetailFragmentDirections.actionSearchDetailFragmentToNavComEventDetail())
    }

    override fun onCreatorSelected(userId: String) {
        val bundle = Bundle()
        bundle.putString(ProfileFragment.USER_ID, userId)
        findNavController().navigate(R.id.action_searchDetailFragment_to_profileFragment5)
    }
}