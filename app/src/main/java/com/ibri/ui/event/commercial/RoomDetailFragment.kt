package com.ibri.ui.event.commercial

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ibri.R
import com.ibri.databinding.FragmentRoomDetailBinding
import com.ibri.model.messaging.Room
import com.ibri.ui.adapters.UserAdapter
import com.ibri.ui.adapters.UserOnClickListener
import com.ibri.ui.profile.ProfileFragment
import com.ibri.ui.viewmodel.RoomViewModel
import com.ibri.utils.GET_MEDIA_ENDPOINT
import com.ibri.utils.LOG_TEST
import java.text.SimpleDateFormat
import java.util.*

class RoomDetailFragment : Fragment(), UserOnClickListener {
    private val viewModel: RoomViewModel by activityViewModels()
    private lateinit var binding: FragmentRoomDetailBinding
    private lateinit var userAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoomDetailBinding.inflate(inflater, container, false)
        setObservableVM()
        setListeners()
        return binding.root
    }

    private fun setObservableVM() {
        viewModel.selectedRoom.observe(viewLifecycleOwner) {
            prepareStage(it)
        }
    }

    private fun setListeners() {
        binding.roomDetailsBackBtn.setOnClickListener { requireActivity().onBackPressed() }
    }

    private fun prepareStage(item: Room) {
        binding.roomDetailsName.text = item.name
        if (item.creator == null)
            binding.roomDetailsCreator.text =
                "La stanza è stata creata dal proprietario dell'evento"
        else
            binding.roomDetailsCreator.text = "${item.creator!!.name} ${item.creator!!.surname}"

        binding.roomDetailsDescription.text = item.description
        binding.roomDetailsCreationDate.text =
            SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(item.created)

        if (item.image != null) {
            val path = item.image?.url
            val url = "$GET_MEDIA_ENDPOINT/$path"
            Glide.with(requireContext())
                .load(url)
                .error(R.drawable.default_avatar)
                .into(binding.roomDetailsImage)
        } else {
            binding.roomDetailsImage.setImageResource(R.drawable.default_avatar)
        }

        Log.wtf(LOG_TEST, "room : ${item.name}, members : ${item.members}")

        if (item.members.isEmpty())
            binding.roomDetailsNoMembers.visibility = View.VISIBLE
        else {
            userAdapter = UserAdapter(requireContext(), this)
            userAdapter.setData(item.members)
            binding.roomDetailsMembersRv.adapter = userAdapter
            binding.roomDetailsMembersRv.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onUserClicked(userId: String) {
        val bundle = Bundle()
        bundle.putString(ProfileFragment.USER_ID, userId)
        findNavController().navigate(R.id.action_roomDetailFragment_to_profileFragment7, bundle)
    }
}