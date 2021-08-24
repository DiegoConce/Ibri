package com.ibri.ui.event

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibri.databinding.FragmentEventQuestionAnswerBinding
import com.ibri.model.Question
import com.ibri.ui.adapters.QnaAdapter
import com.ibri.ui.viewmodel.QnAViewModel
import com.ibri.ui.viewmodel.StandardEventViewModel

class EventQuestionAnswerFragment : Fragment() {

    private val viewModel: QnAViewModel by activityViewModels()
    private lateinit var binding: FragmentEventQuestionAnswerBinding
    private lateinit var qnaAdapter: QnaAdapter

    private var inputQuestion = ""
    private lateinit var eventId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventQuestionAnswerBinding.inflate(inflater, container, false)
        checkArguments()
        setObservableVM()
        setListeners()
        return binding.root
    }

    private fun checkArguments() {
        if (arguments?.containsKey(EVENT_ID) == true) {
            eventId = arguments?.getString(EVENT_ID)!!
            viewModel.getQuestions(eventId)
        }
    }

    private fun setObservableVM() {
        viewModel.questionList.observe(viewLifecycleOwner) {
            binding.qnaProgressBar.visibility = View.VISIBLE
            setQuestionRecyclerView(it)
        }

        viewModel.newQuestionResponse.value = ""
        viewModel.newQuestionResponse.observe(viewLifecycleOwner) {
            if (it == "ok") {
                binding.questionEdit.setText("")
                Toast.makeText(
                    requireContext(),
                    "La tua domanda è stata salvata, aspetta che il creatore dell'evento risponda",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setListeners() {
        binding.qnaBackButton.setOnClickListener { requireActivity().onBackPressed() }

        binding.qnaSendBtn.setOnClickListener {
            viewModel.sendQuestion(
                inputQuestion,
                eventId
            )
        }

        binding.questionEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputQuestion = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun setQuestionRecyclerView(it: ArrayList<Question>) {
        binding.qnaProgressBar.visibility = View.GONE
        if (it.isNullOrEmpty())
            binding.qnaNoQuestionsTab.visibility = View.VISIBLE
        else
            binding.qnaNoQuestionsTab.visibility = View.GONE

        qnaAdapter = QnaAdapter(requireContext())
        qnaAdapter.setData(it)
        binding.qnaRecycler.adapter = qnaAdapter
        binding.qnaRecycler.layoutManager = LinearLayoutManager(requireContext())
    }

    companion object {
        val EVENT_ID = "EVENT_ID"
    }

}