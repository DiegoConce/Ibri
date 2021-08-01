package com.ibri

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.android.navigationadvancedsample.setupWithNavController
import com.ibri.databinding.FragmentBottomNavBinding

class BottomNavFragment : Fragment() {

    private lateinit var binding: FragmentBottomNavBinding

    private val bottomNavSelectedItemIdKey = "BOTTOM_NAV_SELECTED_ITEM_ID_KEY"
    private var bottomNavSelectedItemId = R.id.standardEvents

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

        savedInstanceState?.let {
            bottomNavSelectedItemId =
                savedInstanceState.getInt(bottomNavSelectedItemIdKey, bottomNavSelectedItemId)
        }
        setUpBottomNavBar()
        /*
         binding.bottomNavigationView.setOnItemSelectedListener {
             when (it.itemId) {
                 R.id.standard_events -> setCurrentFragment(StandardEventsFragment())
                 R.id.commercial_events -> setCurrentFragment(CommercialEventsFragment())
                 R.id.subs_events -> setCurrentFragment(IncomingEventsFragment())
                 R.id.profile -> setCurrentFragment(ProfileFragment())
             }
             true
         }*/
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(bottomNavSelectedItemIdKey, bottomNavSelectedItemId)
        super.onSaveInstanceState(outState)
    }

    private fun setUpBottomNavBar() {
        val navGraphIds = listOf(
            R.navigation.nav_standard_event,
            R.navigation.nav_commercial_event,
            R.navigation.nav_incoming_event,
            R.navigation.nav_profile
        )

        binding.bottomNavigationView.selectedItemId = bottomNavSelectedItemId

        val controller = binding.bottomNavigationView.setupWithNavController(
            fragmentManager = childFragmentManager,
            navGraphIds = navGraphIds,
            containerId = R.id.bottom_nav_container,
            intent = requireActivity().intent
        )

        controller.observe(viewLifecycleOwner,{
            bottomNavSelectedItemId = it.graph.id
        })
    }

    private fun setCurrentFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction().apply {
            replace(R.id.bottom_nav_container, fragment)
            commit()
        }
    }

}