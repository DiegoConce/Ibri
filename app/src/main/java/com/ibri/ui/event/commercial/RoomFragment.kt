package com.ibri.ui.event.commercial

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ibri.R
import com.ibri.databinding.FragmentRoomBinding
import com.ibri.model.messaging.Message
import com.ibri.model.messaging.Room
import com.ibri.ui.adapters.ChatAdapter
import com.ibri.ui.adapters.UserOnClickListener
import com.ibri.ui.profile.ProfileFragment
import com.ibri.ui.viewmodel.RoomViewModel
import com.ibri.utils.GET_MEDIA_ENDPOINT
import com.ibri.utils.LOG_TEST
import com.ibri.utils.PreferenceManager

class RoomFragment : Fragment(), UserOnClickListener {
    private val viewModel: RoomViewModel by activityViewModels()
    private lateinit var binding: FragmentRoomBinding
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var mainHandler: Handler
    private lateinit var pref: SharedPreferences
    private lateinit var userId: String
    private lateinit var roomId: String
    private lateinit var chatId: String
    private lateinit var message: String

    private val updateMessages = object : Runnable {
        override fun run() {
            val chatId = viewModel.selectedRoom.value?.chat?.id
            if (chatId != null)
                viewModel.fetchMessages(chatId)
            mainHandler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoomBinding.inflate(inflater, container, false)
        mainHandler = Handler(Looper.getMainLooper())
        pref = PreferenceManager.getSharedPreferences(requireContext())
        userId = pref.getString(PreferenceManager.ACCOUNT_ID, "")!!

        setObservableVM()
        setListeners()

        return binding.root
    }

    private fun setObservableVM() {
        viewModel.selectedRoom.observe(viewLifecycleOwner) {
            prepareStage(it)
        }

        viewModel.messagesList.observe(viewLifecycleOwner) {
            setAdapter(it)
        }

        viewModel.enterRoomResponse.value = ""
        viewModel.enterRoomResponse.observe(viewLifecycleOwner) {
            if (it == "ok") {
                binding.roomEnterBtn.visibility = View.GONE
                binding.roomChatEditField.visibility = View.VISIBLE
            }
        }

        viewModel.deleteRoomResponse.value = ""
        viewModel.deleteRoomResponse.observe(viewLifecycleOwner) {
            if (it == "ok") {
                goBack()
            }
        }

        viewModel.leaveRoomResponse.value = ""
        viewModel.leaveRoomResponse.observe(viewLifecycleOwner) {
            if (it == "ok") {
                goBack()
            }
        }

        viewModel.messageResponse.value = ""
        viewModel.messageResponse.observe(viewLifecycleOwner) {
            if (it == "ok") {
                viewModel.fetchMessages(chatId)
                binding.roomChatEditMessage.text.clear()
            }
        }

        viewModel.isUserSubscribed.observe(viewLifecycleOwner) {
            if (it) {
                if (viewModel.isCreator.value == true)
                    binding.roomEnterBtn.visibility = View.GONE
                else
                    binding.roomEnterBtn.visibility = View.GONE
            } else
                binding.roomEnterBtn.visibility = View.VISIBLE
        }
    }

    private fun setListeners() {
        val popupMenu = PopupMenu(requireContext(), binding.roomMenu)
        if (viewModel.isUserSubscribed.value == true)
            popupMenu.menu.add("Abbandona Stanza").setOnMenuItemClickListener { leaveRoom() }

        if (viewModel.isCreator.value == true || viewModel.isOwner.value == true)
            popupMenu.menu.add("Elimina Stanza").setOnMenuItemClickListener { deleteRoom() }

        binding.roomMenu.setOnClickListener { popupMenu.show() }
        binding.roomBackBtn.setOnClickListener { goBack() }
        binding.roomEnterBtn.setOnClickListener { enterRoom() }
        binding.roomChatEditMessage.doOnTextChanged { text, start, before, count ->
            message = text.toString()
        }

        binding.roomChatSentMessageBtn.setOnClickListener {
            if (!message.isEmpty()) {
                if (viewModel.isOwner.value == true)
                    viewModel.sendCommercialMessage(chatId, message, userId)
                else
                    viewModel.sendPrivateMessage(chatId, message, userId)
            }
        }

        binding.roomDetailsBtn.setOnClickListener {
            findNavController().navigate(RoomFragmentDirections.actionRoomFragmentToRoomDetailFragment())
        }
    }

    private fun setAdapter(it: ArrayList<Message>) {
        if (it.isNullOrEmpty())
            binding.roomNoMessages.visibility = View.VISIBLE
        else
            binding.roomNoMessages.visibility = View.GONE

        chatAdapter = ChatAdapter(this)
        chatAdapter.setData(it)
        binding.roomRv.adapter = chatAdapter
        binding.roomRv.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun prepareStage(item: Room) {
        roomId = item.id
        chatId = item.chat.id

        binding.roomName.text = item.name
        binding.roomMembers.text = "Partecipanti ${item.members.size}/${item.maxMembers}"

        if (item.image != null) {
            val path = item.image?.url
            val url = "$GET_MEDIA_ENDPOINT/$path"
            Glide.with(requireContext())
                .load(url)
                .error(R.drawable.default_avatar)
                .into(binding.roomAvatar)
        } else {
            binding.roomAvatar.setImageResource(R.drawable.default_avatar)
        }

        if (item.chat.messages.isEmpty()) {
            binding.roomNoMessages.visibility = View.VISIBLE
        } else {
            binding.roomNoMessages.visibility = View.GONE
            viewModel.messagesList.value = item.chat.messages
        }

        viewModel.isCreator.value = userId == item.creator?.id

        viewModel.isOwner.value = userId == item.host.id

        for (user in item.members) {
            viewModel.isUserSubscribed.value = user.id == userId
        }

        Log.wtf(LOG_TEST, "isCreator ${viewModel.isCreator.value}")
        Log.wtf(LOG_TEST, "isOwner ${viewModel.isOwner.value}")
        Log.wtf(LOG_TEST, "isUserSubscribed ${viewModel.isUserSubscribed.value}")

    }

    private fun enterRoom() {
        viewModel.enterRoom(userId, roomId)
       // viewModel.isUserSubscribed.value = true
    }

    private fun leaveRoom(): Boolean {
        viewModel.leaveRoom(userId, roomId)
        return true
    }

    private fun deleteRoom(): Boolean {
        viewModel.deleteRoom(roomId)
        return true
    }

    override fun onPause() {
        super.onPause()
        mainHandler.post(updateMessages)
    }

    override fun onResume() {
        super.onResume()
        mainHandler.post(updateMessages)
    }

    private fun goBack() {
        mainHandler.removeCallbacks(updateMessages)
        requireActivity().onBackPressed()
    }

    override fun onUserClicked(userId: String) {
        val bundle = Bundle()
        bundle.putString(ProfileFragment.USER_ID, userId)
        findNavController().navigate(R.id.action_roomFragment_to_profileFragment7, bundle)
    }

}