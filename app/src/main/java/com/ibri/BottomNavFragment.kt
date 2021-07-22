package com.ibri

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ibri.databinding.FragmentBottomNavBinding
import com.ibri.ui.CommercialEventsFragment
import com.ibri.ui.IncomingEventsFragment
import com.ibri.ui.ProfileFragment
import com.ibri.ui.StandardEventsFragment

class BottomNavFragment : Fragment() {

    private lateinit var binding: FragmentBottomNavBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomNavBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setCurrentFragment(StandardEventsFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.standard_events -> setCurrentFragment(StandardEventsFragment())
                R.id.commercial_events -> setCurrentFragment(CommercialEventsFragment())
                R.id.subs_events -> setCurrentFragment(IncomingEventsFragment())
                R.id.profile -> setCurrentFragment(ProfileFragment())
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        childFragmentManager.beginTransaction().apply {
            replace(R.id.bottom_nav_container, fragment)
            commit()
        }
}

