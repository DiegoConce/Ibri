package com.ibri.ui.event.standard

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ibri.R
import com.ibri.databinding.FragmentStandardEventDetailBinding
import com.ibri.model.events.StandardEvent
import com.ibri.ui.activity.EditStandardEventActivity
import com.ibri.ui.adapters.UserAdapter
import com.ibri.ui.adapters.UserOnClickListener
import com.ibri.ui.event.EventQuestionAnswerFragment
import com.ibri.ui.profile.ProfileFragment
import com.ibri.ui.viewmodel.StandardEventViewModel
import com.ibri.utils.GET_MEDIA_ENDPOINT
import com.ibri.utils.PreferenceManager
import java.text.SimpleDateFormat
import java.util.*

class StandardEventDetailFragment : Fragment(), OnMapReadyCallback, UserOnClickListener {

    private lateinit var binding: FragmentStandardEventDetailBinding
    private lateinit var pref: SharedPreferences
    private lateinit var mMap: GoogleMap
    private lateinit var userAdapter: UserAdapter
    private val viewModel: StandardEventViewModel by activityViewModels()
    private var topBarAnimationFinished: Boolean = true
    private var topBarHidden: Boolean = false
    private var mapReady: Boolean = false

    private lateinit var standEvent: StandardEvent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStandardEventDetailBinding.inflate(inflater, container, false)
        pref = PreferenceManager.getSharedPreferences(requireContext())
        setObservableVM()
        setListeners()
        setScrollListener()
        initMap()
        return binding.root
    }

    private var launcherEditStandActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (it.data != null) {
                    viewModel.reloadEvent()
                }
            }
            if (it.resultCode == Activity.RESULT_CANCELED) {
                requireActivity().onBackPressed()
            }
        }

    private fun setObservableVM() {
        viewModel.selectedStandardEvent.observe(viewLifecycleOwner) {
            prepareStage(it)
        }

        viewModel.isMyEvent.observe(viewLifecycleOwner) {
            if (it) {
                binding.standEventSubsButtonTextView.visibility = View.GONE
                binding.standEventEditButton.visibility = View.VISIBLE
            } else {
                binding.standEventSubsButtonTextView.visibility = View.VISIBLE
                binding.standEventEditButton.visibility = View.GONE
            }
        }

        viewModel.isSubcribed.observe(viewLifecycleOwner) {
            if (viewModel.isMyEvent.value == true) {
                binding.standEventSubsButtonTextView.visibility = View.GONE
                binding.standEventEditButton.visibility = View.VISIBLE
                return@observe
            }
            if (viewModel.isPending.value == true) {
                binding.standEventSubscribeButton.alpha = 0.85F
                binding.standEventSubscribeButton.isFocusable = false
                binding.standEventSubscribeButton.isClickable = false
                binding.standEventChatButton.alpha = 0.80F
                binding.standEventChatButton.isFocusable = false
                binding.standEventChatButton.isClickable = false
                binding.standEventSubsButtonTextView.text = "In Attesa"
                return@observe
            }
            if (it) {
                binding.standEventChatButton.alpha = 1F
                binding.standEventChatButton.isFocusable = true
                binding.standEventChatButton.isClickable = true
                binding.standEventSubsButtonTextView.text = "- Lascia"
                binding.standEventIsprivateMessage.visibility = View.GONE
            } else {
                binding.standEventChatButton.alpha = 0.80F
                binding.standEventChatButton.isFocusable = false
                binding.standEventChatButton.isClickable = false
                binding.standEventSubsButtonTextView.text = "+ Unisciti"
            }
        }

    }

    private fun setListeners() {
        binding.stantEventDetailBackButton.setOnClickListener { requireActivity().onBackPressed() }
        binding.standEventDetailAddress.setOnClickListener { showPlaceInNavigation() }
        binding.standEventSubscribeButton.setOnClickListener { subscribeToEvent() }
        binding.standEventEditButton.setOnClickListener {
            val intent = Intent(requireContext(), EditStandardEventActivity::class.java)
                .putExtra(EditStandardEventActivity.EDIT_STAND_EVENT, standEvent)
            launcherEditStandActivity.launch(intent)
        }
        binding.standEventChatButton.setOnClickListener {
            findNavController().navigate(
                StandardEventDetailFragmentDirections.actionStandardEventDetailFragmentToStandardEventChatFragment2()
            )
        }
        binding.standEventQnaButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(EventQuestionAnswerFragment.EVENT_ID, standEvent.id)
            findNavController().navigate(
                R.id.action_standardEventDetailFragment_to_eventQuestionAnswerFragment2,
                bundle
            )
        }
        binding.standEventCreatorButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(ProfileFragment.USER_ID, standEvent.creator.id)
            findNavController().navigate(
                R.id.action_standardEventDetailFragment_to_profileFragment4,
                bundle
            )
        }
    }

    private fun prepareStage(item: StandardEvent) {
        standEvent = item
        val myId = pref.getString(PreferenceManager.ACCOUNT_ID, "")

        if (standEvent.subscribers.isNullOrEmpty())
            viewModel.isSubcribed.value = false

        for (user in standEvent.subscribers!!) {
            if (user.id == myId) {
                viewModel.isMyEvent.value = false
                viewModel.isSubcribed.value = true
                viewModel.isPending.value = false
            } else {
                viewModel.isSubcribed.value = false
            }
        }
        viewModel.isMyEvent.value = myId == standEvent.creator.id

        if (standEvent.private)
            binding.standEventIsprivateMessage.visibility = View.VISIBLE
        else
            binding.standEventIsprivateMessage.visibility = View.GONE


        if (pref.getString(PreferenceManager.ACCOUNT_ROLE, "") == "COMPANY")
            binding.standEventSubscribeButton.visibility = View.GONE
        else
            binding.standEventSubscribeButton.visibility = View.VISIBLE


        if (standEvent.media != null) {
            val path = standEvent.media!!.url
            val url = "$GET_MEDIA_ENDPOINT/$path"
            Glide.with(requireContext())
                .load(url)
                .error(R.drawable.default_avatar)
                .into(binding.standEventDetailImage)
        } else {
            binding.standEventDetailImage.setImageResource(R.drawable.app_logo)
        }

        binding.standEventDetailTitle.text = standEvent.title
        binding.standEventDetailDescription.text = standEvent.description
        if (binding.standEventDetailDescription.text.isNullOrEmpty()) {
            binding.standEventDetailDescription.text = "Questo evento non ha una descrizione"
        }
        binding.standEventDetailDate.text =
            SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault()).format(standEvent.eventDay)
        binding.standEventDetailAddress.text = standEvent.address
        binding.standEventDetailGuests.text =
            standEvent.guests.toString() + "/" + standEvent.maxGuests.toString()

        binding.standEventCreatorName.text = standEvent.creator.name
        if (standEvent.creator.avatar != null) {
            val path = standEvent.creator.avatar!!.url
            val url = "$GET_MEDIA_ENDPOINT/$path"
            Glide.with(requireContext())
                .load(url)
                .error(R.drawable.default_avatar)
                .into(binding.standEventCreatorAvatar)
        } else {
            binding.standEventCreatorAvatar.setImageResource(R.drawable.default_avatar)
        }

        setUserRecyclerView(item)
    }

    private fun setUserRecyclerView(item: StandardEvent) {
        userAdapter = UserAdapter(requireContext(), this)

        if (item.subscribers.isNullOrEmpty()) {
            binding.noSubsCard.visibility = View.VISIBLE
        } else {
            binding.noSubsCard.visibility = View.GONE
        }

        userAdapter.setData(item.subscribers!!)
        binding.standEventUserRv.adapter = userAdapter
        binding.standEventUserRv.layoutManager = LinearLayoutManager(requireContext())
    }


    private fun subscribeToEvent() {
        val userId = pref.getString(PreferenceManager.ACCOUNT_ID, "")!!
        if (viewModel.isSubcribed.value == false) {
            viewModel.subscribeToEventRequest(userId, standEvent.id)
            if (standEvent.private) {
                viewModel.isPending.value = true
                showDialog()
            } else
                viewModel.isPending.value = false

        } else {
            viewModel.cancelSubscription(userId, standEvent.id)
        }
    }

    private fun showDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Richiesta di partecipazione inviata")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun initMap() {
        val mapFragment = SupportMapFragment.newInstance()
        childFragmentManager
            .beginTransaction()
            .add(R.id.map_preview, mapFragment)
            .commit()
        mapFragment.getMapAsync(this)
    }

    private fun showPlaceInNavigation() {
        val uri = Uri.parse("google.navigation:q=${standEvent.lat},${standEvent.lon}")
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(mapIntent)
        }
    }


    private fun setScrollListener() {
        binding.scrollingArea.viewTreeObserver.addOnScrollChangedListener {
            val scrollY: Int = binding.scrollingArea.scrollY
            if (scrollY != 0) {
                if (scrollY > binding.scrollingArea.height * 0.20) {
                    if (!topBarHidden)
                        if (topBarAnimationFinished) {
                            topBarAnimationFinished = false
                            binding.standEventDetailTopBar.animate()
                                .translationY(-binding.standEventDetailTopBar.height.toFloat())
                                .setDuration(200)
                                .setListener(object : AnimatorListenerAdapter() {
                                    override fun onAnimationEnd(animation: Animator) {
                                        topBarAnimationFinished = true
                                        topBarHidden = true
                                    }
                                })
                                .start()
                        }
                } else {
                    if (topBarHidden)
                        if (topBarAnimationFinished) {
                            topBarAnimationFinished = false
                            binding.standEventDetailTopBar.animate()
                                .translationY(0F)
                                .setDuration(200)
                                .setListener(object : AnimatorListenerAdapter() {
                                    override fun onAnimationEnd(animation: Animator) {
                                        topBarAnimationFinished = true
                                        topBarHidden = false
                                    }
                                })
                                .start()
                        }
                }
            } else {
                if (topBarHidden)
                    if (topBarAnimationFinished) {
                        topBarAnimationFinished = false
                        binding.standEventDetailTopBar.animate()
                            .translationY(0F)
                            .setDuration(200)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    topBarAnimationFinished = true
                                    topBarHidden = false
                                }
                            })
                            .start()
                    }
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mapReady = true
        mMap = p0
        setMapData()
    }

    private fun setMapData() {
        if (mapReady) {
            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(standEvent.lat.toDouble(), standEvent.lon.toDouble()))
            )
            val cameraPosition = CameraPosition.Builder()
                .target(LatLng(standEvent.lat.toDouble(), standEvent.lon.toDouble()))
                .zoom(15f).build()
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            mMap.setOnMapClickListener { showPlaceInNavigation() }
            mMap.uiSettings.isScrollGesturesEnabled = false
            mMap.uiSettings.isZoomControlsEnabled = false
            mMap.uiSettings.isZoomGesturesEnabled = false
            mMap.uiSettings.isMapToolbarEnabled = false
        }
    }

    override fun onUserClicked(userId: String) {
        val bundle = Bundle()
        bundle.putString(ProfileFragment.USER_ID, userId)
        findNavController().navigate(
            R.id.action_standardEventDetailFragment_to_profileFragment4,
            bundle
        )
    }
}