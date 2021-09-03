package com.ibri.ui.event.commercial

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
import androidx.activity.result.contract.ActivityResultContract
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
import com.ibri.R
import com.ibri.databinding.FragmentCommercialEventDetailBinding
import com.ibri.model.events.CommercialEvent
import com.ibri.model.messaging.Room
import com.ibri.ui.activity.EditCommercialEventActivity
import com.ibri.ui.activity.NewRoomActivity
import com.ibri.ui.adapters.RoomAdapter
import com.ibri.ui.adapters.RoomOnClickListener
import com.ibri.ui.adapters.UserAdapter
import com.ibri.ui.adapters.UserOnClickListener
import com.ibri.ui.event.EventQuestionAnswerFragment
import com.ibri.ui.profile.ProfileFragment
import com.ibri.ui.viewmodel.CommercialEventViewModel
import com.ibri.ui.viewmodel.RoomViewModel
import com.ibri.utils.GET_MEDIA_ENDPOINT
import com.ibri.utils.PreferenceManager
import java.text.SimpleDateFormat
import java.util.*

class CommercialEventDetailFragment : Fragment(), OnMapReadyCallback, UserOnClickListener,
    RoomOnClickListener {

    private lateinit var binding: FragmentCommercialEventDetailBinding
    private lateinit var pref: SharedPreferences
    private lateinit var roomAdapter: RoomAdapter
    private lateinit var userAdapter: UserAdapter
    private lateinit var mMap: GoogleMap
    private val viewModel: CommercialEventViewModel by activityViewModels()
    private val roomViewModel: RoomViewModel by activityViewModels()

    private var topBarAnimationFinished: Boolean = true
    private var topBarHidden: Boolean = false
    private var mapReady: Boolean = false

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
        setScrollListener()
        initMap()
        return binding.root
    }

    private var launcherEditComEvent =
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

    private var launcherNewRoomActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (it.data != null) {
                    viewModel.reloadEvent()
                }
            }
        }

    private fun setObservableVM() {
        viewModel.selectedCommercialEvent.observe(viewLifecycleOwner) {
            prepareStage(it)
        }

        viewModel.isMyEvent.observe(viewLifecycleOwner) {
            if (it) {
                binding.comEventSubscribeButton.visibility = View.GONE
                binding.comEventEditButton.visibility = View.VISIBLE
            } else {
                binding.comEventSubscribeButton.visibility = View.VISIBLE
                binding.comEventEditButton.visibility = View.GONE
            }
        }

        viewModel.isSubcribed.observe(viewLifecycleOwner) {
            if (viewModel.isMyEvent.value == true) {
                binding.comEventSubscribeButton.visibility = View.GONE
                binding.comEventEditButton.visibility = View.VISIBLE
                return@observe
            }
            if (it) {
                binding.comEventDetailNewRoom.alpha = 1F
                binding.comEventDetailNewRoom.isFocusable = true
                binding.comEventDetailNewRoom.isClickable = true
                binding.comEventSubsButtonTextView.text = "- Lascia"
            } else {
                binding.comEventDetailNewRoom.alpha = 0.80F
                binding.comEventDetailNewRoom.isFocusable = false
                binding.comEventDetailNewRoom.isClickable = false
                binding.comEventSubsButtonTextView.text = "+ Unisciti"
            }
        }
    }

    private fun setListeners() {
        binding.comEventDetailBackButton.setOnClickListener { requireActivity().onBackPressed() }
        binding.comEventDetailAddress.setOnClickListener { showPlaceInNavigation() }
        binding.comEventSubscribeButton.setOnClickListener { subscribeToEvent() }

        binding.comEventDetailNewRoom.setOnClickListener {
            val intent = Intent(requireContext(), NewRoomActivity::class.java)
                .putExtra(NewRoomActivity.EVENT_ID, comEvent.id)
                .putExtra(NewRoomActivity.MAX_MEMBERS, comEvent.maxGuests)
            launcherNewRoomActivity.launch(intent)
        }

        binding.comEventEditButton.setOnClickListener {
            val intent = Intent(requireContext(), EditCommercialEventActivity::class.java)
                .putExtra(EditCommercialEventActivity.EDIT_COM_EVENT, comEvent)
            launcherEditComEvent.launch(intent)
        }
        binding.comEventQnaButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(EventQuestionAnswerFragment.EVENT_ID, comEvent.id)
            findNavController().navigate(
                R.id.action_commercialEventDetailFragment_to_eventQuestionAnswerFragment,
                bundle
            )
        }
        binding.comEventCreatorButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(ProfileFragment.USER_ID, comEvent.creator.id)
            findNavController().navigate(
                R.id.action_commercialEventDetailFragment_to_profileFragment7,
                bundle
            )
        }
    }

    private fun prepareStage(item: CommercialEvent) {
        comEvent = item
        val myId = pref.getString(PreferenceManager.ACCOUNT_ID, "")

        if (comEvent.subscribers.isNullOrEmpty())
            viewModel.isSubcribed.value = false

        for (user in comEvent.subscribers!!) {
            if (user.id == myId) {
                viewModel.isMyEvent.value = false
                viewModel.isSubcribed.value = true
            } else {
                viewModel.isSubcribed.value = false
            }
        }
        viewModel.isMyEvent.value = myId == comEvent.creator.id


        if (comEvent.media != null) {
            val path = comEvent.media!!.url
            val url = "$GET_MEDIA_ENDPOINT/$path"
            Glide.with(requireContext())
                .load(url)
                .error(R.drawable.default_avatar)
                .into(binding.comEventDetailImage)
        } else {
            binding.comEventDetailImage.setImageResource(R.drawable.app_logo)
        }

        binding.comEventDetailTitle.text = comEvent.title
        binding.comEventDetailDescription.text = comEvent.description
        if (binding.comEventDetailDescription.text.isNullOrEmpty())
            binding.comEventDetailDescription.text = "Questo evento non ha una descrizione"

        binding.comEventDetailDate.text =
            SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault()).format(comEvent.eventDay)
        binding.comEventDetailAddress.text = comEvent.address
        binding.comEventDetailGuests.text =
            comEvent.guests.toString() + "/" + comEvent.maxGuests.toString()
        binding.comEventCreatorName.text = comEvent.creator.name

        if (comEvent.creator.avatar != null) {
            val path = comEvent.creator.avatar!!.url
            val url = "$GET_MEDIA_ENDPOINT/$path"
            Glide.with(requireContext())
                .load(url)
                .error(R.drawable.default_avatar)
                .into(binding.comEventCreatorAvatar)
        } else {
            binding.comEventCreatorAvatar.setImageResource(R.drawable.default_avatar)
        }

        setUserRecyclerView(item)
        setRoomsRecyclerView(item)
    }

    private fun setUserRecyclerView(item: CommercialEvent) {
        userAdapter = UserAdapter(requireContext(), this)
        if (item.subscribers.isNullOrEmpty())
            binding.noSubsCard.visibility = View.VISIBLE
        else
            binding.noSubsCard.visibility = View.GONE

        userAdapter.setData(item.subscribers!!)
        binding.comUserRecyclerView.adapter = userAdapter
        binding.comUserRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setRoomsRecyclerView(item: CommercialEvent) {
        roomAdapter = RoomAdapter(requireContext(),this)
        if (item.rooms.isNullOrEmpty())
            binding.comNoRoomsTextView.visibility = View.VISIBLE
        else
            binding.comNoRoomsTextView.visibility = View.GONE

        roomAdapter.setData(item.rooms)
        binding.roomRecyclerView.adapter = roomAdapter
        binding.roomRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun subscribeToEvent() {
        val userId = pref.getString(PreferenceManager.ACCOUNT_ID, "")!!

        if (viewModel.isSubcribed.value == false)
            viewModel.subscribeToCommercialEvent(userId, comEvent.id)
        else
            viewModel.cancelSubscription(userId, comEvent.id)
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
        val uri = Uri.parse("google.navigation:q=${comEvent.lat},${comEvent.lon}")
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(mapIntent)
        }
    }

    private fun setScrollListener() {
        binding.comScrollingArea.viewTreeObserver.addOnScrollChangedListener {
            val scrollY: Int = binding.comScrollingArea.scrollY
            if (scrollY != 0) {
                if (scrollY > binding.comScrollingArea.height * 0.20) {
                    if (!topBarHidden)
                        if (topBarAnimationFinished) {
                            topBarAnimationFinished = false
                            binding.comEventDetailTopLayout.animate()
                                .translationY(-binding.comEventDetailTopLayout.height.toFloat())
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
                            binding.comEventDetailTopLayout.animate()
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
                        binding.comEventDetailTopLayout.animate()
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
                    .position(LatLng(comEvent.lat.toDouble(), comEvent.lon.toDouble()))
            )
            val cameraPosition = CameraPosition.Builder()
                .target(LatLng(comEvent.lat.toDouble(), comEvent.lon.toDouble()))
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
            R.id.action_commercialEventDetailFragment_to_profileFragment7,
            bundle
        )
    }

    override fun onRoomClicked(item: Room) {
        roomViewModel.selectedRoom.value = item
        findNavController().navigate(CommercialEventDetailFragmentDirections.actionCommercialEventDetailFragmentToRoomFragment())
    }

}