package com.ibri.ui.event.standard

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibri.R
import com.ibri.databinding.FragmentStandardEventChatBinding
import com.ibri.model.messaging.Message
import com.ibri.ui.adapters.ChatAdapter
import com.ibri.ui.adapters.UserOnClickListener
import com.ibri.ui.profile.ProfileFragment
import com.ibri.ui.viewmodel.StandardEventViewModel
import com.ibri.utils.PreferenceManager

class StandardEventChatFragment : Fragment(), UserOnClickListener {

    private val viewModel: StandardEventViewModel by activityViewModels()
    private lateinit var binding: FragmentStandardEventChatBinding
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var mainHandler: Handler
    private lateinit var pref: SharedPreferences

    private val updateMessage = object : Runnable {
        override fun run() {
            val chatId = viewModel.selectedStandardEvent.value?.chat?.id
            if (chatId != null) {
                viewModel.fetchMessages(chatId)
            }
            mainHandler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStandardEventChatBinding.inflate(inflater, container, false)
        mainHandler = Handler(Looper.getMainLooper())
        pref = PreferenceManager.getSharedPreferences(requireContext())

        setObservableVM()
        setListeners()
        return binding.root
    }

    private fun setObservableVM() {
        viewModel.messagesList.observe(viewLifecycleOwner) {
            setAdapter(it)
        }

        viewModel.standEventMessageResponse.value = ""
        viewModel.standEventMessageResponse.observe(viewLifecycleOwner) {
            if (it == "ok") {
                val chatId = viewModel.selectedStandardEvent.value?.chat?.id
                if (chatId != null) {
                    viewModel.fetchMessages(chatId)
                }
                binding.chatEditText.text.clear()
            }
        }
    }

    private fun setListeners() {
        binding.chatBackButton.setOnClickListener { goBack() }
        binding.chatSendButton.setOnClickListener { sendMessage() }
    }

    private fun sendMessage() {
        if (!binding.chatEditText.text.isNullOrBlank()) {
            val chatId = viewModel.selectedStandardEvent.value?.chat?.id
            if (chatId != null) {
                viewModel.sendMessage(
                    chatId,
                    binding.chatEditText.text.toString(),
                    pref.getString(PreferenceManager.ACCOUNT_ID, "")!!
                )
            }
        }
    }

    private fun setAdapter(it: ArrayList<Message>) {
        if (it.isNullOrEmpty())
            binding.chatNoMessagesTextView.visibility = View.VISIBLE
        else
            binding.chatNoMessagesTextView.visibility = View.GONE

        chatAdapter = ChatAdapter(requireContext(), this)
        chatAdapter.setData(it)
        binding.chatMessagesRecyclerView.adapter = chatAdapter
        binding.chatMessagesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(updateMessage)
    }

    override fun onResume() {
        super.onResume()
        mainHandler.post(updateMessage)
    }

    private fun goBack() {
        mainHandler.removeCallbacks(updateMessage)
        requireActivity().onBackPressed()
    }

    override fun onUserClicked(userId: String) {
        val bundle = Bundle()
        bundle.putString(ProfileFragment.USER_ID, userId)
        findNavController().navigate(
            R.id.action_standardEventChatFragment_to_profileFragment2,
            bundle
        )
    }
}