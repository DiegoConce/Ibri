package com.ibri.ui.profile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.ibri.R
import com.ibri.databinding.FragmentProfileBinding
import com.ibri.model.Media
import com.ibri.model.events.CommercialEvent
import com.ibri.model.events.StandardEvent
import com.ibri.ui.activity.NewCommercialEventActivity
import com.ibri.ui.activity.NewStandardEventActivity
import com.ibri.ui.adapters.ProfilePagerAdapter
import com.ibri.ui.event.SelectedEventListener
import com.ibri.ui.viewmodel.ProfileViewModel
import com.ibri.ui.viewmodel.StandardEventViewModel
import com.ibri.utils.GET_MEDIA_ENDPOINT
import com.ibri.utils.LOG_TEST
import com.ibri.utils.PreferenceManager

class ProfileFragment : Fragment(), SelectedEventListener {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var pref: SharedPreferences
    private lateinit var profilePagerAdapter: ProfilePagerAdapter
    private val viewModel: ProfileViewModel by activityViewModels()
    private val standEventViewModel: StandardEventViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        pref = PreferenceManager.getSharedPreferences(requireContext())

        Log.wtf(LOG_TEST," dest : ${findNavController().currentDestination}")

        checkArguments()
        setObservableVM()
        setListeners()
        setViewPager()

        return binding.root
    }

    private fun checkArguments() {
        if (arguments?.containsKey(USER_ID) == true) {
            viewModel.isMyProfile.value = false
            viewModel.getGuestProfile(arguments?.getString(USER_ID)!!)
        } else {
            viewModel.isMyProfile.value = true
        }
    }

    private fun setObservableVM() {
        viewModel.isMyProfile.observe(viewLifecycleOwner) {
            if (it)
                prepareMyProfile()
            else
                hideLayouts()
        }

        viewModel.accountResponse.observe(viewLifecycleOwner) {
            if (it.user != null) {
                viewModel.isCompany.value = false
                prepareGuestProfile(
                    it.user!!.avatar,
                    it.user!!.bio,
                    it.user!!.name,
                    it.user!!.id
                )
            } else if (it.company != null) {
                viewModel.isCompany.value = true
                prepareGuestProfile(
                    it.company!!.avatar,
                    it.company!!.bio,
                    it.company!!.name,
                    it.company!!.id
                )
            }
        }

    }

    private fun setListeners() {
        binding.profileBackButton.setOnClickListener { requireActivity().onBackPressed() }
        binding.profileSettingsButton.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToProfileSettingsFragment()
            )
        }
        binding.profileNewEventButton.setOnClickListener { showNewEvent() }
        binding.profileNotificationButton.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToNotificationCentreFragment())
        }
    }

    private fun setViewPager() {
        profilePagerAdapter = ProfilePagerAdapter(requireActivity(),this)
        binding.profileViewPager.adapter = profilePagerAdapter

        TabLayoutMediator(binding.profileTabLayout, binding.profileViewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Eventi Correnti"
                }
                1 -> {
                    tab.text = "Eventi Passati"
                }
            }
        }.attach()

    }

    private fun prepareMyProfile() {
        binding.profileBackButton.visibility = View.GONE
        binding.profileNewEventButton.visibility = View.VISIBLE
        binding.profileNotificationButton.visibility = View.VISIBLE
        binding.profileSettingsButton.visibility = View.VISIBLE

        if (pref.getString(PreferenceManager.ACCOUNT_ROLE, "") == "COMPANY") {
            binding.profileCompanyIcon.visibility = View.VISIBLE
            viewModel.isCompany.value = true
            viewModel.commercialEventsByUser(pref.getString(PreferenceManager.ACCOUNT_ID, "")!!)
        } else {
            binding.profileCompanyIcon.visibility = View.GONE
            viewModel.isCompany.value = false
            viewModel.standardEventsByUser(pref.getString(PreferenceManager.ACCOUNT_ID, "")!!)
        }
        setAvatar()
        setNameAndBio()
    }

    private fun setAvatar() {
        val path = pref.getString(PreferenceManager.ACCOUNT_AVATAR, "")
        val url = "$GET_MEDIA_ENDPOINT/$path"

        if (path?.isEmpty() == true) {
            binding.profileAvatar.setImageResource(R.drawable.default_avatar)
        } else {
            Glide.with(requireContext())
                .load(url)
                .error(R.drawable.default_avatar)
                .into(binding.profileAvatar)
        }
    }

    private fun setNameAndBio() {
        binding.profileName.text = pref.getString(PreferenceManager.ACCOUNT_NAME, "Creator")
        val a = pref.getString(PreferenceManager.ACCOUNT_BIO, "")
        binding.profileBio.text = pref.getString(PreferenceManager.ACCOUNT_BIO, "")
        if (binding.profileBio.text.isNullOrEmpty())
            binding.profileBio.text = getString(R.string.questo_utente_non_ha_una_descrizione)
    }

    private fun hideLayouts() {
        binding.profileBackButton.visibility = View.VISIBLE
        binding.profileNotificationButton.visibility = View.GONE
        binding.profileSettingsButton.visibility = View.GONE
    }

    private fun prepareGuestProfile(avatar: Media?, bio: String?, name: String, id: String) {
        binding.profileName.text = name
        if (bio.isNullOrEmpty())
            binding.profileBio.text = getString(R.string.questo_utente_non_ha_una_descrizione)
        else
            binding.profileBio.text = bio

        if (viewModel.isCompany.value == true) {
            binding.profileCompanyIcon.visibility = View.VISIBLE
            viewModel.commercialEventsByUser(id)
        } else {
            binding.profileCompanyIcon.visibility = View.GONE
            viewModel.standardEventsByUser(id)
        }

        binding.profileNewEventButton.visibility = View.GONE

        setGuestAvatar(avatar)
    }

    private fun setGuestAvatar(avatar: Media?) {
        val path = avatar?.url
        val url = "$GET_MEDIA_ENDPOINT/$path"
        if (path?.isEmpty() == true) {
            binding.profileAvatar.setImageResource(R.drawable.default_avatar)
        } else {
            Glide.with(requireContext())
                .load(url)
                .error(R.drawable.default_avatar)
                .into(binding.profileAvatar)
        }
    }

    private fun showNewEvent() {
        if (viewModel.isCompany.value == true) {
            val intent = Intent(requireContext(), NewCommercialEventActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(requireContext(), NewStandardEventActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onItemSelected(item: StandardEvent) {
        standEventViewModel.selectedStandardEvent.value = item
       findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToNavStandEvent())
        Log.wtf(LOG_TEST," dest : ${findNavController().currentDestination}")
    }

    override fun onItemSelected(item: CommercialEvent) {
        TODO("Not yet implemented")
    }

    override fun onCreatorSelected(userId: String) {
    }

    companion object {
        var USER_ID = "USER_ID"
    }

    override fun onResume() {
        checkArguments()
        super.onResume()
    }
}