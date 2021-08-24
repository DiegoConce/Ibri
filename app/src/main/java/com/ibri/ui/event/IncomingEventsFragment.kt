package com.ibri.ui.event

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibri.R
import com.ibri.databinding.FragmentIncomingEventsBinding
import com.ibri.model.events.CommercialEvent
import com.ibri.model.events.StandardEvent
import com.ibri.model.events.SubscribedEventResponse
import com.ibri.ui.adapters.CommercialEventAdapter
import com.ibri.ui.adapters.EventsOnClickListener
import com.ibri.ui.adapters.StandardEventAdapter
import com.ibri.ui.profile.ProfileFragment
import com.ibri.ui.viewmodel.CommercialEventViewModel
import com.ibri.ui.viewmodel.ProfileViewModel
import com.ibri.ui.viewmodel.StandardEventViewModel
import com.ibri.utils.PreferenceManager

class IncomingEventsFragment : Fragment(), EventsOnClickListener {

    private lateinit var binding: FragmentIncomingEventsBinding
    private lateinit var standardEventAdapter: StandardEventAdapter
    private lateinit var commercialEventAdapter: CommercialEventAdapter
    private lateinit var pref: SharedPreferences
    private val viewModel: ProfileViewModel by activityViewModels()
    private val standEventVM: StandardEventViewModel by activityViewModels()
    private val comEventVM: CommercialEventViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIncomingEventsBinding.inflate(inflater, container, false)
        standardEventAdapter = StandardEventAdapter(requireContext(), this)
        commercialEventAdapter = CommercialEventAdapter(requireContext(), this)
        pref = PreferenceManager.getSharedPreferences(requireContext())
        viewModel.getSubscribedEvents(pref.getString(PreferenceManager.ACCOUNT_ID, "").toString())
        setObservableVM()
        return binding.root
    }

    private fun setObservableVM() {
        viewModel.subscribedEventsList.observe(viewLifecycleOwner) {
            if (it != null) {
                prepareAdapter(it)
            }
        }
    }

    private fun prepareAdapter(it: ArrayList<SubscribedEventResponse>) {
        val standEventList = ArrayList<StandardEvent>()
        val comEventList = ArrayList<CommercialEvent>()

        for (item in it) {
            if (item.privateEvent != null)
                standEventList.add(item.privateEvent!!)
            else if (item.commercialEvent != null)
                comEventList.add(item.commercialEvent!!)
        }

        if (standEventList.isEmpty() && comEventList.isEmpty())
            binding.noSubsEvents.visibility = View.VISIBLE
        else
            binding.noSubsEvents.visibility = View.GONE

        standardEventAdapter.setData(standEventList)
        commercialEventAdapter.setData(comEventList)

        val concatAdapter = ConcatAdapter(commercialEventAdapter, standardEventAdapter)
        binding.incomingEventsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.incomingEventsRecyclerView.adapter = concatAdapter
    }

    override fun onItemSelected(item: StandardEvent) {
        standEventVM.selectedStandardEvent.value = item
        findNavController().navigate(IncomingEventsFragmentDirections.actionIncomingEventsFragmentToNavStandEvent())
    }

    override fun onItemSelected(item: CommercialEvent) {
        comEventVM.selectedCommercialEvent.value = item
        findNavController().navigate(IncomingEventsFragmentDirections.actionIncomingEventsFragmentToNavComEventDetail())
    }

    override fun onCreatorSelected(userId: String) {
        val bundle = Bundle()
        bundle.putString(ProfileFragment.USER_ID, userId)
        findNavController().navigate(R.id.action_incomingEventsFragment_to_profileFragment6, bundle)
    }
}