package com.ibri.ui.profile

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibri.R
import com.ibri.databinding.FragmentNotificationCentreBinding
import com.ibri.model.Question
import com.ibri.model.events.SubscribeRequest
import com.ibri.ui.adapters.NotificationAdapter
import com.ibri.ui.adapters.NotificationClickListener
import com.ibri.ui.adapters.SubRequestAdapter
import com.ibri.ui.viewmodel.ProfileViewModel
import com.ibri.utils.PreferenceManager

class NotificationCentreFragment : Fragment(), NotificationClickListener {
    private val viewModel: ProfileViewModel by activityViewModels()
    private lateinit var binding: FragmentNotificationCentreBinding
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var subRequestAdapter: SubRequestAdapter
    private lateinit var pref: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationCentreBinding.inflate(layoutInflater, container, false)
        pref = PreferenceManager.getSharedPreferences(requireContext())
        viewModel.loadQuestions(pref.getString(PreferenceManager.ACCOUNT_ID, "")!!)
        viewModel.loadSubscribeRequests(pref.getString(PreferenceManager.ACCOUNT_ID, "")!!)
        subRequestAdapter = SubRequestAdapter(requireContext(),this)
        notificationAdapter = NotificationAdapter(requireContext(), this)

        setObservableVM()
        setListeners()
        prepareStage()
        return binding.root
    }

    private fun setObservableVM() {
        viewModel.questionList.observe(viewLifecycleOwner) {
//            if (binding.questionToggleButton.isChecked) {
//                if (it.isNullOrEmpty())
//                    binding.notificationNoItems.visibility = View.VISIBLE
//                else {
//                    binding.notificationNoItems.visibility = View.GONE
//                }
//            }
            notificationAdapter.setData(it)
        }

        viewModel.subscribeRequestsList.observe(viewLifecycleOwner) {
//            if (binding.subRequestToggleButton.isChecked) {
//                if (it.isNullOrEmpty())
//                    binding.notificationNoItems.visibility = View.VISIBLE
//                else {
//                    binding.notificationNoItems.visibility = View.GONE
//                }
//            }
            subRequestAdapter.setData(it)
        }

    }

    private fun setListeners() {
        binding.notificationBackButton.setOnClickListener { requireActivity().onBackPressed() }

        binding.notificationSwipeRefresh.setOnRefreshListener {
            viewModel.loadQuestions(pref.getString(PreferenceManager.ACCOUNT_ID, "")!!)
            viewModel.loadSubscribeRequests(pref.getString(PreferenceManager.ACCOUNT_ID, "")!!)
            if (binding.notificationSwipeRefresh.isRefreshing)
                binding.notificationSwipeRefresh.isRefreshing = false
        }

        // prepareQuestionLayout()
        binding.notificationToggleButtons.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.question_toggle_button -> prepareQuestionLayout()
                    R.id.sub_request_toggle_button -> prepareSubRequestLayout()
                }
            }
        }
    }

    private fun prepareStage() {
        if (pref.getString(PreferenceManager.ACCOUNT_ROLE, "") == "COMPANY") {
            binding.notificationToggleButtons.visibility = View.GONE
        } else {
            binding.notificationToggleButtons.visibility = View.VISIBLE
        }
        binding.questionToggleButton.isChecked = true
        prepareQuestionLayout()
    }

    private fun prepareQuestionLayout() {
        if (notificationAdapter.questionsListAdapter.isNullOrEmpty())
            binding.notificationNoItems.visibility = View.VISIBLE
        else
            binding.notificationNoItems.visibility = View.GONE

        binding.notificationRecyclerView.adapter = notificationAdapter
        binding.notificationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun prepareSubRequestLayout() {
        if (subRequestAdapter.subRequestsList.isNullOrEmpty())
            binding.notificationNoItems.visibility = View.VISIBLE
        else
            binding.notificationNoItems.visibility = View.GONE

        binding.notificationRecyclerView.adapter = subRequestAdapter
        binding.notificationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDeteleClickListener(question: Question, position: Int) {
        viewModel.deleteQuestion(question.id)
        viewModel.deleteQuestionResponse.value = ""
        viewModel.deleteQuestionResponse.observe(viewLifecycleOwner) {
            if (it == "ok") {
                notificationAdapter.questionsListAdapter.remove(question)
                notificationAdapter.notifyItemRemoved(position)
                viewModel.questionList.value?.remove(question)
            }
        }
    }

    override fun onAnswerClickListener(
        question: Question,
        answer: String,
        position: Int,
        answerProgressBar: ProgressBar
    ) {
        viewModel.answerQuestion(question.id, answer)
        viewModel.answerQuestionResponse.value = ""
        viewModel.answerQuestionResponse.observe(viewLifecycleOwner) {
            if (it == "ok") {
                Toast.makeText(requireContext(), "Risposta inviata con successo", Toast.LENGTH_LONG)
                    .show()
                notificationAdapter.questionsListAdapter.remove(question)
                notificationAdapter.notifyItemRemoved(position)
                viewModel.questionList.value?.remove(question)
            } else
                answerProgressBar.visibility = View.VISIBLE
        }
    }

    override fun onAcceptSubRequestClickListener(
        subscribeRequest: SubscribeRequest,
        isAccepted: Boolean,
        position: Int
    ) {
        viewModel.acceptSubscribeRequest(subscribeRequest.id, isAccepted)
        viewModel.subscribeRequestResponse.value = ""
        viewModel.subscribeRequestResponse.observe(viewLifecycleOwner) {
            if (it == "ok") {
                //dialog?
                Toast.makeText(requireContext(), "Utente accettato", Toast.LENGTH_LONG)
                    .show()
                subRequestAdapter.subRequestsList.remove(subscribeRequest)
                subRequestAdapter.notifyItemRemoved(position)
            } else if (it == "declined") {
                subRequestAdapter.subRequestsList.remove(subscribeRequest)
                subRequestAdapter.notifyItemRemoved(position)
            }
        }
    }


}