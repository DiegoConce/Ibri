package com.ibri.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibri.databinding.FragmentAchievementsBinding
import com.ibri.model.Achievement
import com.ibri.ui.adapters.AchievementAdapter
import com.ibri.ui.viewmodel.ProfileViewModel

class AchievementsFragment : Fragment() {

    private val viewModel: ProfileViewModel by activityViewModels()
    private lateinit var binding: FragmentAchievementsBinding
    private lateinit var achievementAdapter: AchievementAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAchievementsBinding.inflate(inflater, container, false)
        setObservableVM()
        setListeners()
        return binding.root
    }

    private fun setObservableVM() {
        viewModel.achievementList.observe(viewLifecycleOwner) {
            setRecyclerView(it)
        }
    }

    private fun setListeners() {
        binding.achievementsBackButton.setOnClickListener { requireActivity().onBackPressed() }
    }

    private fun setRecyclerView(it: ArrayList<Achievement>) {
        achievementAdapter = AchievementAdapter()
        achievementAdapter.setData(it)
        binding.achievementsRecyclerView.adapter = achievementAdapter
        binding.achievementsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}