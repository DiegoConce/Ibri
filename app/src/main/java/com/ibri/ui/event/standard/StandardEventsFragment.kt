package com.ibri.ui.event.standard

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ibri.R
import com.ibri.databinding.FragmentStandardEventsBinding
import com.ibri.databinding.ItemPositionSelectorBinding
import com.ibri.model.events.CommercialEvent
import com.ibri.model.events.StandardEvent
import com.ibri.ui.profile.ProfileFragment
import com.ibri.ui.adapters.StandardEventAdapter
import com.ibri.ui.adapters.EventsOnClickListener
import com.ibri.ui.viewmodel.SearchViewModel
import com.ibri.ui.viewmodel.StandardEventViewModel

class StandardEventsFragment : Fragment(), EventsOnClickListener {

    private lateinit var binding: FragmentStandardEventsBinding
    private lateinit var bindingDialog: ItemPositionSelectorBinding
    private val viewModel: StandardEventViewModel by activityViewModels()
    private val searchViewModel: SearchViewModel by activityViewModels()
    private lateinit var standardEventAdapter: StandardEventAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var location: Location
    private var distanceInM: Int = 30000

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
            perms.entries.forEach {
                val isGranted = it.value
                if (isGranted) {
                    // Permission is granted
                } else {
                    // Permission is denied
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStandardEventsBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        viewModel.getStandardEvents()
        setObservableVM()
        setListeners()
        getLocation()
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
        binding.positionSlider.setOnClickListener { showPositionDialog() }
        binding.searchButton.setOnClickListener {
            searchViewModel.isStandardEvent.value = true
            searchViewModel.distanceInM.value = distanceInM
            if (this::location.isInitialized)
                searchViewModel.location.value = location
            findNavController().navigate(
                StandardEventsFragmentDirections.actionStandardEventsFragmentToNavSearch()
            )
        }
    }

    private fun getLocation() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                fusedLocationClient.lastLocation.addOnSuccessListener {
                    if (it != null)
                        location = it
                }
            }
            //shouldShowRequestPermissionRationale
            else -> {
                requestPermissions.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
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

    private fun showPositionDialog() {
        bindingDialog =
            ItemPositionSelectorBinding.inflate(LayoutInflater.from(context), null, false)

        val distanceInKm = distanceInM / 1000
        bindingDialog.textViewKm.text = distanceInKm.toString() + "Km"
        if (bindingDialog.seekBar.progress < distanceInKm)
            bindingDialog.seekBar.progress = distanceInKm

        bindingDialog.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                bindingDialog.textViewKm.text = progress.toString() + "Km"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    distanceInM = seekBar.progress * 1000
                }
            }
        })

        MaterialAlertDialogBuilder(requireContext())
            .setView(bindingDialog.root)
            .setPositiveButton("OK") { _, _ ->
                viewModel.getStandEventByPosition(
                    location.latitude.toString(),
                    location.longitude.toString(),
                    distanceInM
                )
            }
            .setNegativeButton(R.string.indietro) { _, _ ->

            }
            .show()
    }

    override fun onItemSelected(item: StandardEvent) {
        viewModel.selectedStandardEvent.value = item
        findNavController().navigate(StandardEventsFragmentDirections.actionStandardEventsFragmentToNavStandEvent2())
    }

    override fun onCreatorSelected(userId: String) {
        val bundle = Bundle()
        bundle.putString(ProfileFragment.USER_ID, userId)
        findNavController().navigate(R.id.action_standardEventsFragment_to_profileFragment2, bundle)
    }

    override fun onItemSelected(item: CommercialEvent) {
    }

}