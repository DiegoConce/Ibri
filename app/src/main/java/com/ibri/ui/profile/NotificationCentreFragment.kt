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
import com.ibri.databinding.FragmentNotificationCentreBinding
import com.ibri.model.Question
import com.ibri.ui.adapters.NotificationAdapter
import com.ibri.ui.adapters.NotificationClickListener
import com.ibri.ui.viewmodel.ProfileViewModel
import com.ibri.utils.PreferenceManager

class NotificationCentreFragment : Fragment(), NotificationClickListener {
    private val viewModel: ProfileViewModel by activityViewModels()
    private lateinit var binding: FragmentNotificationCentreBinding
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var pref: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationCentreBinding.inflate(layoutInflater, container, false)
        pref = PreferenceManager.getSharedPreferences(requireContext())
        viewModel.loadQuestions(pref.getString(PreferenceManager.ACCOUNT_ID, "")!!)
        setObservableVM()
        setListeners()
        return binding.root
    }

    private fun setObservableVM() {
        viewModel.questionList.observe(viewLifecycleOwner) {
            setNotificationRecyclerView(it)
        }

    }

    private fun setListeners() {
        binding.notificationBackButton.setOnClickListener { requireActivity().onBackPressed() }
        binding.notificationSwipeRefresh.setOnRefreshListener {
            viewModel.loadQuestions(pref.getString(PreferenceManager.ACCOUNT_ID, "")!!)
            if (binding.notificationSwipeRefresh.isRefreshing)
                binding.notificationSwipeRefresh.isRefreshing = false
        }
    }

    private fun setNotificationRecyclerView(it: ArrayList<Question>) {
        if (it.isNullOrEmpty())
            binding.notificationNoItems.visibility = View.VISIBLE
        else
            binding.notificationNoItems.visibility = View.GONE

        notificationAdapter = NotificationAdapter(requireContext(), this)
        notificationAdapter.setData(it)
        binding.notificationRecyclerView.adapter = notificationAdapter
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
            }
            else
                answerProgressBar.visibility = View.VISIBLE
        }
    }


}