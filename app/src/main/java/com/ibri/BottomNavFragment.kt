package com.ibri

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ibri.databinding.FragmentBottomNavBinding
import com.ibri.ui.event.commercial.CommercialEventsFragment
import com.ibri.ui.event.IncomingEventsFragment
import com.ibri.ui.ProfileFragment
import com.ibri.ui.event.standard.StandardEventsFragment
import it.bereadysoftware.topmarche.view.BackButtonBehaviour
import it.bereadysoftware.topmarche.view.setupWithNavController

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
        setupBottomNavBar(view)

        /* setCurrentFragment(StandardEventsFragment())

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


    // Needed to maintain correct state over rotations
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(bottomNavSelectedItemIdKey, bottomNavSelectedItemId)
        super.onSaveInstanceState(outState)
    }


    private fun setupBottomNavBar(view: View) {
        val bottomNavView = view.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
//        val toolbar = view.findViewById<Toolbar>(R.id.bottom_nav_toolbar)
        // Your navGraphIds must have the same ids as your menuItem ids
        val navGraphIds = listOf(
            R.navigation.nav_standard_event,
            R.navigation.nav_commercial_event,
            R.navigation.nav_incoming_event,
            R.navigation.nav_profile
        )

//        addToolbarListener(toolbar)
        bottomNavView.selectedItemId =
            bottomNavSelectedItemId // Needed to maintain correct state on return

        val controller = bottomNavView.setupWithNavController(
            fragmentManager = childFragmentManager,
            navGraphIds = navGraphIds,
            backButtonBehaviour = BackButtonBehaviour.POP_HOST_FRAGMENT,
            containerId = R.id.bottom_nav_container,
            firstItemId = R.id.top, // Must be the same as bottomNavSelectedItemId
            intent = requireActivity().intent
        )

        controller.observe(viewLifecycleOwner, { navController ->
//            NavigationUI.setupWithNavController(toolbar, navController)
            bottomNavSelectedItemId =
                navController.graph.id // Needed to maintain correct state on return
        })
    }


    private fun setCurrentFragment(fragment: Fragment) =
        childFragmentManager.beginTransaction().apply {
            replace(R.id.bottom_nav_container, fragment)
            commit()
        }
}

